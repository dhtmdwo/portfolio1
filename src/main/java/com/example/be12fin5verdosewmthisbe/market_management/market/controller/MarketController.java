package com.example.be12fin5verdosewmthisbe.market_management.market.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @PostMapping
    public BaseResponse<String> registerInventorySale(@RequestBody InventorySaleDto.InventorySaleRequestDto dto) {
        marketService.saleRegister(dto);
        return BaseResponse.success("ok");
    }
    @PostMapping
    public BaseResponse<String> register(@RequestBody InventoryPurchaseDto.InventoryPurchaseRequestDto dto) {
        marketService.purchaseRegister(dto);
        return BaseResponse.success("ok");
    }
}
