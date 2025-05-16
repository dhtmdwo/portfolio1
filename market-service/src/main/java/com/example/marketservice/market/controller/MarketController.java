package com.example.marketservice.market.controller;

import com.example.common.common.config.BaseResponse;
import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.marketservice.market.model.Inventory;
import com.example.marketservice.market.model.InventorySale;
import com.example.marketservice.market.model.Store;
import com.example.marketservice.market.model.dto.InventoryPurchaseDto;
import com.example.marketservice.market.model.dto.InventorySaleDto;
import com.example.marketservice.market.model.dto.TransactionDto;
import com.example.marketservice.market.service.MarketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
    public BaseResponse<String> registerInventorySale(@RequestBody @Valid InventorySaleDto.InventorySaleRequestDto dto,@RequestHeader("X-Store-Id") String storeId) {
        Inventory inventory = marketService.getFirstInventoryToUse(dto.getStoreInventoryId());
        if(inventory == null) {
            throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
        }
        marketService.saleRegister(dto,Long.parseLong(storeId),inventory);

        return BaseResponse.success("ok");
    }

    @PostMapping("/registerPurchase")
    public BaseResponse<String> registerInventoryPurchase(@RequestBody @Valid InventoryPurchaseDto.InventoryPurchaseRequestDto dto,@RequestHeader("X-Store-Id") String storeId) {
        marketService.purchaseRegister(dto, Long.parseLong(storeId));
        return BaseResponse.success("ok");
    }

    @GetMapping("/get/{saleId}/purchaseList")
    public List<InventoryPurchaseDto.InventoryPurchaseResponseDto> getPurchasesBySaleId(@PathVariable Long saleId) {
        return marketService.getPurchasesBySaleId(saleId);
    }

    @GetMapping("/get/{saleId}/detail")
    public BaseResponse<InventorySaleDto.InventorySaleDetailDto> getSalesDetail(@PathVariable Long saleId) {
        InventorySale entity = marketService.findInventorySaleById(saleId);
        InventorySaleDto.InventorySaleDetailDto dto = InventorySaleDto.InventorySaleDetailDto.fromEntity(entity);
        return BaseResponse.success(dto);
    }


    @GetMapping("/approve")
    public BaseResponse<String> approve(
            @RequestParam Long saleId,
            @RequestParam Long purchaseId
    ) {
        marketService.approvePurchase(saleId, purchaseId);
        return BaseResponse.success("ok");
    }

    @GetMapping("/reject")
    public BaseResponse<String> reject(
            @RequestParam Long purchaseId
    ) {
        marketService.rejectPurchase(purchaseId);
        return BaseResponse.success("ok");
    }

    @GetMapping("/transaction")
    public BaseResponse<List<TransactionDto>> transaction(
           @RequestHeader("X-Store-Id") String storeId,
            @RequestParam(required = false) String keyword) {
        List<TransactionDto> transactionDtoList = marketService.getAllTransactions(Long.parseLong(storeId),keyword);
        return BaseResponse.success(transactionDtoList.stream()
                .sorted(Comparator.comparing(TransactionDto::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()) // 최신순으로 정렬
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


    // 요청보낸 가게의 주변 3km이내의 가게만 보여줌
    @GetMapping("/getList")
    public BaseResponse<List<InventorySaleDto.InventorySaleListDto>> getList(@RequestHeader("X-Store-Id") String storeId) {

        List<Store> storeList = marketService.getNearbyStoreIds(Long.parseLong(storeId));
        List<Long> storeIds = storeList.stream().map(Store::getId).toList();
        return BaseResponse.success(marketService.getNearbyAvailableSalesDto(storeIds,Long.parseLong(storeId)));
    }

}