package com.example.be12fin5verdosewmthisbe.market_management.market.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @PostMapping("/registerSale")
    public BaseResponse<String> registerInventorySale(@RequestBody InventorySaleDto.InventorySaleRequestDto dto) {
        marketService.saleRegister(dto);
        return BaseResponse.success("ok");
    }
    @PostMapping("/registerPurchase")
    public BaseResponse<String> registerInventoryPurchase(@RequestBody InventoryPurchaseDto.InventoryPurchaseRequestDto dto) {
        marketService.purchaseRegister(dto);
        return BaseResponse.success("ok");
    }
    /*@GetMapping("/get/{storeId}/active")
    public BaseResponse<List<InventorySaleDto.InventorySaleResponseDto>> getSaleList(@PathVariable Long storeId) {
        return marketService.getAvailableOrWaitingSales(storeId);
    }*/



}
