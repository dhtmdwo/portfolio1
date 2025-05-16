package com.example.orderservice.order.service;



import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.common.kafka.dto.InventoryConsumeEvent;
import com.example.orderservice.inventory.model.StoreInventory;
import com.example.orderservice.inventory.repository.StoreInventoryRepository;
import com.example.orderservice.menu_management.menu.model.Menu;
import com.example.orderservice.menu_management.menu.model.Recipe;
import com.example.orderservice.menu_management.menu.repository.MenuRepository;
import com.example.orderservice.menu_management.option.model.Option;
import com.example.orderservice.menu_management.option.model.OptionValue;
import com.example.orderservice.menu_management.option.repository.OptionRepository;
import com.example.orderservice.order.model.Order;
import com.example.orderservice.order.model.OrderMenu;
import com.example.orderservice.order.model.OrderOption;
import com.example.orderservice.order.model.dto.*;
import com.example.orderservice.order.repository.OrderMenuRepository;
import com.example.orderservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String INVENTORY_TOPIC = "inventory.consume";

    @Transactional
    public OrderDto.OrderCreateResponse createOrder(
            OrderDto.OrderCreateRequest request,
            Long storeId) {

        // 1) 요청에서 menuId, optionId, recipeId 수집
        List<Long> menuIds   = request.getOrderMenus().stream()
                .map(OrderDto.OrderMenuRequest::getMenuId)
                .distinct().toList();
        List<Long> optionIds = request.getOrderMenus().stream()
                .flatMap(m -> m.getOptionIds().stream())
                .distinct().toList();

        // 2) 메뉴, 레시피, 옵션, 옵션값을 한 번에 배치 조회
        List<Menu> menus = menuRepository.load(menuIds);
        Map<Long, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        List<Option> options = optionRepository.findAllById(optionIds);
        Map<Long, Option> optionMap = options.stream()
                .collect(Collectors.toMap(Option::getId, Function.identity()));

        // 3) 주문 엔티티 생성
        Order order = Order.builder()
                .tableNumber(request.getTableNumber())
                .status(Order.OrderStatus.PAID)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .storeId(storeId)
                .orderType(Order.OrderType.valueOf(request.getOrderType()))
                .orderMenuList(new ArrayList<>())
                .build();

        int totalPrice = 0;
        // 임시 누적 맵: StoreInventory.id → 총 사용량
        Map<Long, BigDecimal> usedInventoryQty = new HashMap<>();
        // 임시 누적 맵: Inventory.id → 총 수정량 (음수)
        Map<Long, BigDecimal> modifyInventoryQty = new HashMap<>();
        // 메뉴 카운트 누적
        Map<Long, Integer> menuCountMap = new HashMap<>();

        // 4) 각 OrderMenu 처리
        for (OrderDto.OrderMenuRequest menuReq : request.getOrderMenus()) {
            Menu menu = menuMap.get(menuReq.getMenuId());
            if (menu == null) throw new CustomException(ErrorCode.MENU_NOT_FOUND);

            OrderMenu om = OrderMenu.builder()
                    .order(order)
                    .price(menuReq.getPrice())
                    .quantity(menuReq.getQuantity())
                    .menu(menu)
                    .orderOptionList(new ArrayList<>())
                    .build();

            int menuTotal = menuReq.getPrice() * menuReq.getQuantity();

            // 4-a) 레시피별 재고 차감량 누적
            for (Recipe recipe : menu.getRecipeList()) {
                Long invId = recipe.getStoreInventory().getId();
                BigDecimal deduct = recipe.getQuantity()
                        .multiply(BigDecimal.valueOf(menuReq.getQuantity()));
                usedInventoryQty.merge(invId, deduct, BigDecimal::add);
                modifyInventoryQty.merge(invId, deduct.negate(), BigDecimal::add);
            }

            // 4-b) 옵션별 처리
            for (Long optId : menuReq.getOptionIds()) {
                Option opt = optionMap.get(optId);
                if (opt == null) throw new RuntimeException("Option not found");

                OrderOption oo = OrderOption.builder()
                        .orderMenu(om)
                        .option(opt)
                        .build();
                om.getOrderOptionList().add(oo);
                menuTotal += opt.getPrice() * menuReq.getQuantity();

                // 옵션값 재고 차감
                for (OptionValue ov : opt.getOptionValueList()) {
                    Long invId = ov.getStoreInventory().getId();
                    BigDecimal deduct = ov.getQuantity()
                            .multiply(BigDecimal.valueOf(menuReq.getQuantity()));
                    usedInventoryQty.merge(invId, deduct, BigDecimal::add);
                    modifyInventoryQty.merge(invId, deduct.negate(), BigDecimal::add);
                }
            }

            order.getOrderMenuList().add(om);
            totalPrice += menuTotal;
            menuCountMap.merge(menu.getId(), menuReq.getQuantity(), Integer::sum);
        }


        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        InventoryConsumeEvent evt = new InventoryConsumeEvent(
                storeId,
                usedInventoryQty,
                Instant.now().toString()
        );
        kafkaTemplate.send(INVENTORY_TOPIC, storeId.toString(), evt);

        return OrderDto.OrderCreateResponse.toOrderCreateResponse(order);
    }

    public List<OrderDto.AllOrderList> getOrdersByStoreId(Long storeId) {
        List<Order> orders = orderRepository.findByStoreId(storeId);
        List<OrderDto.AllOrderList> allOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderDto.AllOrderList list = OrderDto.AllOrderList.toAllOrderList(order);
            allOrders.add(list);
        }
        return allOrders;
    }

    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
    }

    public OrderTodayDto.OrderTodayResponse getTodaySales(Long storeId) {
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

        Timestamp start = Timestamp.valueOf(startOfWeek.atStartOfDay());
        Timestamp end = Timestamp.valueOf(endOfWeek.plusDays(1).atStartOfDay());

        // PageRequest.of(0, 3) → 첫 페이지(0)에서 3개만
        List<Object[]> rows = orderMenuRepository
                .findBestSellingMenusByStoreAndPeriod(
                        storeId, start, end,
                        PageRequest.of(0, 3)
                );

        String first  = rows.size() > 0 ? (String) rows.get(0)[0] : "";
        String second = rows.size() > 1 ? (String) rows.get(1)[0] : "";
        String third  = rows.size() > 2 ? (String) rows.get(2)[0] : "";

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


    private List<OrderSaleDetailDto.OneTimeResponse> initSaleOneTimeList() {
        List<OrderSaleDetailDto.OneTimeResponse> list = new ArrayList<>();
        list.add(OrderSaleDetailDto.OneTimeResponse.of("hall", 0, 0));
        list.add(OrderSaleDetailDto.OneTimeResponse.of("baemin", 0, 0));
        list.add(OrderSaleDetailDto.OneTimeResponse.of("coupang", 0, 0));
        list.add(OrderSaleDetailDto.OneTimeResponse.of("yogiyo", 0, 0));
        return list;
    }

    public List<OrderSaleDetailDto.TotalResponse> getSalesDetail(Long storeId, LocalDate startDate, LocalDate endDate) {

        List<OrderSaleDetailDto.TotalResponse> saleDetailList = new ArrayList<>();

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate calDate = endDate.plusDays(1);
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(calDate.atStartOfDay());

        List<Order> orderList = orderRepository.findByCreatedAtBetween(storeId, startTimestamp, endTimestamp);


        if(days ==0){
            Map<String, List<OrderSaleDetailDto.OneTimeResponse>> hourlyMap = new LinkedHashMap<>();

            for (int i = 0; i < 24; i++) {
                String hourKey = String.format("%02d", i);
                List<OrderSaleDetailDto.OneTimeResponse> saleOneTimeList = initSaleOneTimeList();
                hourlyMap.put(hourKey, saleOneTimeList);
            }

            // 주문 데이터를 시간별로 분류 및 누적
            for (Order order : orderList) {
                Timestamp createdAt = order.getCreatedAt();
                String hour = String.format("%02d", createdAt.toLocalDateTime().getHour()); // 예: "09", "15"
                String orderType = order.getOrderType().name(); // 예: baemin, hall 등

                List<OrderSaleDetailDto.OneTimeResponse> saleOneTimeList = hourlyMap.get(hour);
                for (int i = 0; i < saleOneTimeList.size(); i++) {
                    OrderSaleDetailDto.OneTimeResponse response = saleOneTimeList.get(i);
                    if (response.getSaleMethod().equalsIgnoreCase(orderType)) {
                        int updatedQuantity = response.getSaleQuantity() + 1;
                        int updatedPrice = response.getSalePrice() + order.getTotalPrice();
                        saleOneTimeList.set(i, OrderSaleDetailDto.OneTimeResponse.of(orderType, updatedQuantity, updatedPrice));
                        break;
                    }
                }
            }

            // 결과를 TotalResponse 리스트에 담기
            for (Map.Entry<String, List<OrderSaleDetailDto.OneTimeResponse>> entry : hourlyMap.entrySet()) {
                saleDetailList.add(OrderSaleDetailDto.TotalResponse.of(entry.getKey(), entry.getValue()));
            }
        }// 하루 검색

        else if (Math.abs(days) <= 30) {
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                List<OrderSaleDetailDto.OneTimeResponse> saleOneTimeList = initSaleOneTimeList();

                // ✨ 현재 날짜에 해당하는 주문만 필터링
                LocalDate finalCurrentDate = currentDate;
                List<Order> filteredOrders = new ArrayList<>();
                for (Order order : orderList) {
                    LocalDate orderDate = order.getCreatedAt().toLocalDateTime().toLocalDate();
                    if (orderDate.equals(finalCurrentDate)) {
                        filteredOrders.add(order);
                    }
                }

                // ✨ OrderType별로 수량과 금액 누적
                for (Order order : filteredOrders) {
                    String type = order.getOrderType().name(); // hall, baemin 등
                    int totalPrice = Optional.ofNullable(order.getTotalPrice()).orElse(0);
                    for (OrderSaleDetailDto.OneTimeResponse response : saleOneTimeList) {
                        if (response.getSaleMethod().equalsIgnoreCase(type)) {
                            // 리플렉션 안 쓰고 새로 객체 생성해야 하므로 리스트를 새로 구성해야 함
                            int updatedQuantity = response.getSaleQuantity() + 1;
                            int updatedPrice = response.getSalePrice() + totalPrice;
                            saleOneTimeList.set(
                                    saleOneTimeList.indexOf(response),
                                    OrderSaleDetailDto.OneTimeResponse.of(type, updatedQuantity, updatedPrice)
                            );
                            break;
                        }
                    }
                }

                // ✨ 날짜별 전체 결과 리스트에 추가
                OrderSaleDetailDto.TotalResponse saleDetail = OrderSaleDetailDto.TotalResponse.of(String.valueOf(currentDate), saleOneTimeList);
                saleDetailList.add(saleDetail);

                currentDate = currentDate.plusDays(1);
            }
        } // 한달 이내

        else if (Math.abs(days) <= 365) {
            YearMonth currentMonth = YearMonth.from(startDate);
            YearMonth endMonth = YearMonth.from(endDate);

            while (!currentMonth.isAfter(endMonth)) {
                List<OrderSaleDetailDto.OneTimeResponse> saleOneTimeList = initSaleOneTimeList();

                // 월별로 주문 필터링 (for문 사용)
                List<Order> filteredOrders = new ArrayList<>();
                for (Order order : orderList) {
                    LocalDate orderDate = order.getCreatedAt().toLocalDateTime().toLocalDate();
                    YearMonth orderMonth = YearMonth.from(orderDate);
                    if (orderMonth.equals(currentMonth)) {
                        filteredOrders.add(order);
                    }
                }

                // OrderType 별로 수량, 가격 누적
                for (Order order : filteredOrders) {
                    String type = order.getOrderType().name();
                    int totalPrice = Optional.ofNullable(order.getTotalPrice()).orElse(0);
                    for (int i = 0; i < saleOneTimeList.size(); i++) {
                        OrderSaleDetailDto.OneTimeResponse response = saleOneTimeList.get(i);
                        if (response.getSaleMethod().equalsIgnoreCase(type)) {
                            int updatedQuantity = response.getSaleQuantity() + 1;
                            int updatedPrice = response.getSalePrice() + totalPrice;
                            saleOneTimeList.set(i, OrderSaleDetailDto.OneTimeResponse.of(type, updatedQuantity, updatedPrice));
                            break;
                        }
                    }
                }

                // 월별 결과 추가
                OrderSaleDetailDto.TotalResponse saleDetail = OrderSaleDetailDto.TotalResponse.of(currentMonth.toString(), saleOneTimeList);
                saleDetailList.add(saleDetail);

                // 다음 달로 이동
                currentMonth = currentMonth.plusMonths(1);
            }
        } // 일년 이내

        else{
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }
        return(saleDetailList);
    }
    @Transactional
    public List<String> validateOrder(Long storeId, InventoryValidateOrderDto dto) {
        List<String> insufficientItems = new ArrayList<>();

        // 1) 요청에서 메뉴 ID, 옵션 ID, 레시피 ID 수집
        List<Long> menuIds   = dto.getOrderMenus().stream()
                .map(InventoryValidateOrderDto.OrderMenuRequest::getMenuId)
                .distinct().toList();

        List<Long> optionIds = dto.getOrderMenus().stream()
                .flatMap(m -> Optional.ofNullable(m.getOptionIds()).orElse(List.<Long>of()).stream())
                .distinct().toList();

        // 2) 배치 조회: 메뉴 → 레시피 → storeInventory
        //    (레시피에 연관된 StoreInventory를 fetch join)
        List<Menu> menus = menuRepository.load(menuIds);
        Map<Long, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        // 3) 배치 조회: 옵션 → 옵션값 → storeInventory
        List<Option> options = optionRepository.findAllByIdInFetchOptionValues(optionIds);
        Map<Long, Option> optionMap = options.stream()
                .collect(Collectors.toMap(Option::getId, Function.identity()));

        // 4) storeInventory 전체 현황 조회 (필요하다면 storeId 조건 추가)
        List<StoreInventory> allInventories = storeInventoryRepository.findByStoreId(storeId);
        // Map<inventoryId, availableQuantity>
        Map<Long, BigDecimal> inventoryAvailMap = allInventories.stream()
                .collect(Collectors.toMap(
                        StoreInventory::getId,
                        StoreInventory::getQuantity,
                        BigDecimal::add
                ));

        // 5) 각 OrderMenuRequest 별 검증
        for (var menuReq : dto.getOrderMenus()) {
            Menu menu = menuMap.get(menuReq.getMenuId());
            if (menu == null) {
                throw new CustomException(ErrorCode.MENU_NOT_FOUND);
            }

            BigDecimal multiplier = BigDecimal.valueOf(menuReq.getQuantity());

            // ——— 레시피 기반 재고 확인 ———
            for (Recipe recipe : menu.getRecipeList()) {
                Long invId = recipe.getStoreInventory().getId();
                BigDecimal required = recipe.getQuantity().multiply(multiplier);
                BigDecimal available = inventoryAvailMap.getOrDefault(invId, BigDecimal.ZERO);

                if (available.compareTo(required) < 0) {
                    insufficientItems.add("[" + recipe.getStoreInventory().getName() + "]");
                }
            }

            // ——— 옵션 기반 재고 확인 ———
            if (menuReq.getOptionIds() != null) {
                for (Long optId : menuReq.getOptionIds()) {
                    Option option = optionMap.get(optId);
                    if (option == null) {
                        throw new CustomException(ErrorCode.OPTION_NOT_FOUND);
                    }

                    for (OptionValue ov : option.getOptionValueList()) {
                        Long invId = ov.getStoreInventory().getId();
                        BigDecimal required = ov.getQuantity().multiply(multiplier);
                        BigDecimal available = inventoryAvailMap.getOrDefault(invId, BigDecimal.ZERO);

                        if (available.compareTo(required) < 0) {
                            insufficientItems.add("[" + option.getName() + "] 옵션 구성 재료가 부족할 수도 있습니다.");
                        }
                    }
                }
            }
        }

        return insufficientItems;
    }



}
        