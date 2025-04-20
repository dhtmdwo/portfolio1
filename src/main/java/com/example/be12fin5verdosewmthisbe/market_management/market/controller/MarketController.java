package com.example.be12fin5verdosewmthisbe.market_management.market.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.TransactionDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.service.MarketService;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.service.StoreService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final StoreService storeService;

    @PostMapping("/registerSale")
    public BaseResponse<String> registerInventorySale(@RequestBody InventorySaleDto.InventorySaleRequestDto dto, HttpServletRequest request) {
        marketService.saleRegister(dto,getStoreId(request));
        return BaseResponse.success("ok");
    }

    @PostMapping("/registerPurchase")
    public BaseResponse<String> registerInventoryPurchase(@RequestBody InventoryPurchaseDto.InventoryPurchaseRequestDto dto, HttpServletRequest request) {
        marketService.purchaseRegister(dto, getStoreId(request));
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


    // 요청보낸 가게의 주변 3km이내의 가게만 보여줌
    @GetMapping("/getList")
    public BaseResponse<List<InventorySaleDto.InventorySaleListDto>> getList(HttpServletRequest request) {
        Long storeId = getStoreId(request);
        List<Long> storeIdList = storeService.getNearbyStoreIds(storeId);
        return BaseResponse.success(marketService.getNearbyAvailableSalesDto(storeIdList));
    }

    private Long getStoreId(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ATOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        Claims claims = jwtTokenProvider.getClaims(token);
        Long storeId = Long.valueOf(claims.get("storeId", String.class));
        return  storeId;
    }
}