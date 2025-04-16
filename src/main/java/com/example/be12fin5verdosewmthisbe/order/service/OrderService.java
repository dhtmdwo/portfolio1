package com.example.be12fin5verdosewmthisbe.order.service;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.model.OrderOption;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderDto;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;


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
}
        