package com.example.be12fin5verdosewmthisbe.order.service;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.model.OrderOption;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderDto;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderMonthDto;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderTodayDto;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderTopMenuDto;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderMenuRepository;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final OrderMenuRepository orderMenuRepository;


    @Transactional
    public Order createOrder(OrderDto.OrderCreateRequest request) {
        Order order = Order.builder()
                .tableNumber(request.getTableNumber())
                .status(Order.OrderStatus.PAID)
                .orderType(Order.OrderType.valueOf(request.getOrderType()))
                .build();

        int totalPrice = 0;

        for (OrderDto.OrderMenuRequest menuReq : request.getOrderMenus()) {
            OrderMenu orderMenu = OrderMenu.builder()
                    .order(order)
                    .price(menuReq.getPrice())
                    .quantity(menuReq.getQuantity())
                    .build();

            int menuTotal = menuReq.getPrice() * menuReq.getQuantity();

            for (Long optionId : menuReq.getOptionIds()) {
                Option option = optionRepository.findById(optionId)
                        .orElseThrow(() -> new RuntimeException("Option not found"));

                OrderOption orderOption = OrderOption.builder()
                        .orderMenu(orderMenu)
                        .option(option)
                        .build();

                orderMenu.getOrderOptionList().add(orderOption);
                menuTotal += option.getPrice() * menuReq.getQuantity(); // 수량 고려
            }

            order.getOrderMenuList().add(orderMenu);
            totalPrice += menuTotal;
        }

        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }
    public List<Order> getOrdersByStoreId(long storeId) {
        return orderRepository.findByStoreId(storeId);
    }
    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
    }



    public OrderTodayDto.OrderTodayResponse getTodaySales(String storeId) {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        List<Order> orderList = orderRepository.findTodayOrderByStoreIdx(storeId, today);
        List<Order> beforeList = orderRepository.findTodayOrderByStoreIdx(storeId, sevenDaysAgo);

        int todayTotal = orderList.stream().mapToInt(Order::getTotalPrice).sum();
        int minusSevendaysTotal = beforeList.stream().mapToInt(Order::getTotalPrice).sum();
        int interval = todayTotal - minusSevendaysTotal;

        List<OrderTodayDto.OrderTodayTime> timeList = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            int finalHour = hour;

            // 이 시간대의 주문 필터링
            List<Order> ordersInHour = orderList.stream()
                    .filter(order -> order.getCreatedAt().toLocalDateTime().getHour() == finalHour)
                    .toList();

            // hall 매출
            int hallSales = ordersInHour.stream()
                    .filter(order -> order.getOrderType() == Order.OrderType.hall)
                    .mapToInt(Order::getTotalPrice)
                    .sum();

            // 배달(나머지) 매출
            int deliverySales = ordersInHour.stream()
                    .filter(order -> order.getOrderType() != Order.OrderType.hall)
                    .mapToInt(Order::getTotalPrice)
                    .sum();



            timeList.add(OrderTodayDto.OrderTodayTime.of(hour, hallSales, deliverySales));
        }
        return(OrderTodayDto.OrderTodayResponse.of(
                todayTotal, interval,timeList
        ));

    }

    public OrderTopMenuDto.TopWeekResponse getTopWeekSales(Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Timestamp startTimestamp = Timestamp.valueOf(startOfWeek.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endOfWeek.plusDays(1).atStartOfDay());

        List<Object[]> result = orderMenuRepository.findBestSellingMenusByStoreAndPeriod(storeId, startTimestamp, endTimestamp);
        int temp = 0;
        String first ="";
        String second ="";
        String third ="";
        for (Object[] row : result) {
            String menuName = (String) row[0];
            if(temp>2){
                break;
            }
            if(temp ==0){
                first = menuName;
            }
            else if(temp ==1){
                second = menuName;
            }
            else if(temp ==2){
                third = menuName;
            }
            else{
                break;
            }
            temp++;
        }
        return OrderTopMenuDto.TopWeekResponse.of(first, second, third);
    }

    public List<OrderMonthDto.TotalSaleResponse> getMonthSales(Long storeId, int year, int month) {

        List<OrderMonthDto.TotalSaleResponse> monthSaleList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay());

        Map<LocalDate, Integer[] > monthSales = new HashMap<>();
        LocalDate currentDate = startDate;
        while(!currentDate.equals(endDate)) {
            monthSales.put(currentDate, new Integer[]{0,0});
            currentDate = currentDate.plusDays(1);
        }

        List<Order> orderList = orderRepository.findByCreatedAtBetween(storeId, startTimestamp, endTimestamp);
        for (Order order : orderList) {
            LocalDate date = order.getCreatedAt().toLocalDateTime().toLocalDate();
            Integer price = order.getTotalPrice();
            Integer[] stat = monthSales.get(date);
            stat[0] += price;
            stat[1] += 1;
        }
        for (Map.Entry<LocalDate, Integer[]> entry : monthSales.entrySet()) {
            LocalDate date = entry.getKey();
            Integer[] data = entry.getValue();
            OrderMonthDto.TotalSaleResponse monthsale = OrderMonthDto.TotalSaleResponse.of(Date.valueOf(date), data[0], data[1]);
            monthSaleList.add(monthsale);
        }
        return(monthSaleList);
    }



}
        