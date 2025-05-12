package com.example.orderservice.order.controller;

import com.example.be12fin5verdosewmthisbe.order.model.dto.OrderMonthDto;
import com.example.common.BaseResponse;
import com.example.orderservice.order.model.Order;
import com.example.orderservice.order.model.dto.OrderDto;
import com.example.orderservice.order.model.dto.OrderSaleDetailDto;
import com.example.orderservice.order.model.dto.OrderTodayDto;
import com.example.orderservice.order.model.dto.OrderTopMenuDto;
import com.example.orderservice.order.service.OrderService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public BaseResponse<OrderDto.OrderCreateResponse> createOrder(@RequestBody OrderDto.OrderCreateRequest request, @RequestHeader("X-Store-Id") Long storeId) {

        OrderDto.OrderCreateResponse created = orderService.createOrder(request, request.getStoreId());
        return BaseResponse.success(created);
    }
    @GetMapping("/getList")
    public BaseResponse<List<OrderDto.AllOrderList>> getAllOrders(@RequestHeader("X-Store-Id") Long storeId) {

        return BaseResponse.success(orderService.getOrdersByStoreId(storeId));
    }

    @GetMapping("/{orderId}")
    public BaseResponse<Order> getOrder(@PathVariable Long orderId) {
        return BaseResponse.success(orderService.getOrderById(orderId));
    }

    @GetMapping("/todaySales")
    public BaseResponse<OrderTodayDto.OrderTodayResponse> getTodaySales(@RequestHeader("X-Store-Id") Long storeId) {
        OrderTodayDto.OrderTodayResponse todayResponse = orderService.getTodaySales(storeId);

        return BaseResponse.success(todayResponse);
    }

    @GetMapping("/weekbestmenu")
    public BaseResponse<OrderTopMenuDto.TopWeekResponse> getWeekBestMenu(@RequestHeader("X-Store-Id") Long storeId) {
        OrderTopMenuDto.TopWeekResponse todayResponse = orderService.getTopWeekSales(storeId);
        return BaseResponse.success(todayResponse);
    }

    @PostMapping("/monthSales")
    public BaseResponse<List<OrderMonthDto.TotalSaleResponse>> getMonthSales(@RequestHeader("X-Store-Id") Long storeId, @Valid @RequestBody OrderMonthDto.TotalRequest totalRequest) {
        int year = totalRequest.getYear();
        int month = totalRequest.getMonth();
        List<OrderMonthDto.TotalSaleResponse> monthSaleList = orderService.getMonthSales(storeId, year, month);
        return BaseResponse.success(monthSaleList);
    }
    //매출 분석 리스트

    @PostMapping("/saleDetail")
    public BaseResponse<List<OrderSaleDetailDto.TotalResponse>> getSalesDetail(@RequestHeader("X-Store-Id") Long storeId, @RequestBody OrderSaleDetailDto.OrderSaleDetailRequest dto) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        List<OrderSaleDetailDto.TotalResponse> detailSaleList = orderService.getSalesDetail(storeId, startDate, endDate);
        return BaseResponse.success(detailSaleList);
    }
    // 매출 분석 상세

}
        