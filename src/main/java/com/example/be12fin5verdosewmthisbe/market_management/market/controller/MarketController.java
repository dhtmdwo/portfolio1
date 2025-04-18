package com.example.be12fin5verdosewmthisbe.market_management.market.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.TransactionDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
    /*@GetMapping("/get/{saleId}/purchaseList")
    public List<InventoryPurchaseDto.InventoryPurchaseResponseDto> getPurchasesBySaleId(@PathVariable Long saleId) {
        return marketService.getPurchasesBySaleId(saleId);
    }*/

    @GetMapping("/get/{saleId}/detail")
    public BaseResponse<InventorySale> getSalesDetail(@PathVariable Long saleId) {
        return BaseResponse.success(marketService.findInventorySaleById(saleId));
    }

    @PostMapping("/approve")
    public BaseResponse<String> approve(
            @RequestParam Long saleId,
            @RequestParam Long purchaseId
    ) {
        marketService.approvePurchase(saleId, purchaseId);
        return BaseResponse.success("ok");
    }

    @PostMapping("/transaction")
    public BaseResponse<List<TransactionDto>> transaction(
            @RequestParam Long storeId,
            @RequestParam(required = false) String keyword) {
        List<TransactionDto> transactionDtoList = marketService.getAllTransactions(storeId,keyword);
        return BaseResponse.success(transactionDtoList.stream()
                .sorted(Comparator.comparing(TransactionDto::getCreatedAt).reversed()) // 최신순으로 정렬
                .toList());
    }

    @PutMapping("/confirm")
    public BaseResponse<String> confirm(
            @RequestParam Long purchaseId
    ) {
        marketService.confirmEnd(purchaseId);
        return BaseResponse.success("ok");
    }
    @PostMapping("/images/upload")
    public BaseResponse<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> filePaths = new ArrayList<>();
        String uploadDir = "uploads/";

        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                filePaths.add("/" + uploadDir + fileName); // 프론트에 보낼 경로
            } catch (IOException e) {
                e.printStackTrace();
                return BaseResponse.error(ErrorCode.ERROR_CODE);
            }
        }
        return BaseResponse.success(filePaths);
    }

}
