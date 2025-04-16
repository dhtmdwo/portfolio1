package com.example.be12fin5verdosewmthisbe.order.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderDto;
import com.example.be12fin5verdosewmthisbe.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public BaseResponse<Order> createOrder(@RequestBody OrderDto.OrderCreateRequest request) {
        Order created = orderService.createOrder(request);
        return BaseResponse.success(created);
    }
    @GetMapping("/getList")
    public BaseResponse<List<Order>> getAllOrders(Long storeId) {
        return BaseResponse.success(orderService.getOrdersByStoreId(storeId));
    }
    @GetMapping("/{orderId}")
    public BaseResponse<Order> getOrder(@PathVariable Long orderId) {
        return BaseResponse.success(orderService.getOrderById(orderId));
    }

}
        