package com.example.be12fin5verdosewmthisbe.inventory.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;

import com.example.be12fin5verdosewmthisbe.inventory.model.dto.*;
import com.example.be12fin5verdosewmthisbe.inventory.service.InventoryService;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "재고 관련 API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = {"Authorization", "Content-Type", "*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/api/inventory")
@Tag(name = "재고관리", description = "재고 관리 API") // 이 라인을 추가하여 CORS 허용
public class InventoryController {
    private final InventoryService inventoryService;
    private final JwtTokenProvider jwtTokenProvider;

    //dto로 정보 받아서 StoreInventory 저장
    @PostMapping("/registerStoreInventory")
    public BaseResponse<String> registerStoreInventory(@RequestBody InventoryDetailRequestDto dto) {
        inventoryService.registerInventory(dto);
        return BaseResponse.success("ok");
    }

    @PostMapping("/totalInventory")
    public BaseResponse<String> totalInventory(@RequestBody InventoryDetailRequestDto dto) {
        inventoryService.totalInventory(dto);
        return BaseResponse.success("ok");
    }

    //dto로 정보 받아서Inventory 저장
    @PostMapping("/DetailInventory")
    public BaseResponse<String> DetailInventory(@RequestBody InventoryDto dto) {
        inventoryService.DetailInventory(dto);
        return BaseResponse.success("ok");
    }

    @GetMapping("/storeInventory/{inventoryId}")
    public BaseResponse<StoreInventory> getInventoryById(@PathVariable Long inventoryId) {
        StoreInventory inventory = inventoryService.findById(inventoryId);
        return BaseResponse.success(inventory);
    }

    @PutMapping("/storeInventory/{inventoryId}")
    public BaseResponse<StoreInventory> updateInventory(
            @PathVariable Long inventoryId,
            @RequestBody InventoryDetailRequestDto dto) {
        StoreInventory updatedInventory = inventoryService.updateInventory(inventoryId, dto);
        return BaseResponse.success(updatedInventory);
    }

    @DeleteMapping("/storeInventory/{inventoryId}")
    public BaseResponse<String> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteById(inventoryId);
        return BaseResponse.success("재고가 성공적으로 삭제되었습니다.");
    }


    @GetMapping("/storeInventory/getList")
    public BaseResponse<List<StoreInventoryDto.responseDto>> getAllStoreInventories() {
        List<StoreInventoryDto.responseDto> result = inventoryService.getAllStoreInventories();
        return BaseResponse.success(result);
    }

    @GetMapping("/inventoryList")
    public BaseResponse<List<InventoryInfoDto.Response>> getInventoryList(HttpServletRequest request) {

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
        // JWT 읽기
        String storeIdStr = claims.get("storeId", String.class);
        Long storeId = Long.parseLong(storeIdStr);
        List<InventoryInfoDto.Response> inventoryList = inventoryService.getInventoryList(storeId);
        return BaseResponse.success(inventoryList);
    }
    // 재고 종류 리스트로 뽑기

    @PostMapping("/menuSale")
    public BaseResponse<List<InventoryChangeDto.Response>> getSaleList(HttpServletRequest request, @RequestBody InventoryChangeDto.DateRequest dto) {

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
        // JWT 읽기
        String storeIdStr = claims.get("storeId", String.class);
        Long storeId = Long.parseLong(storeIdStr);
        List<InventoryChangeDto.Response> SaleList = inventoryService.getSaleList(storeId, dto);
        return BaseResponse.success(SaleList);
    }
    // 메뉴로 재고가 얼마나 사용됐나 조회

    @PostMapping("/marketSale")
    public BaseResponse<List<InventoryChangeDto.Response>> getMarketList(HttpServletRequest request, @RequestBody InventoryChangeDto.DateRequest dto) {

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
        // JWT 읽기
        String storeIdStr = claims.get("storeId", String.class);
        Long storeId = Long.parseLong(storeIdStr);
        List<InventoryChangeDto.Response> SaleList = inventoryService.getMarketList(storeId, dto);
        return BaseResponse.success(SaleList);
    }
    // 장터로 재고가 얼마나 변동했나 조회


    @GetMapping("/inventoryCall")
    public BaseResponse<InventoryCallDto.Response> getInventoryCall(HttpServletRequest request) {

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
        // JWT 읽기
        String storeIdStr = claims.get("storeId", String.class);
        Long storeId = Long.parseLong(storeIdStr);
        InventoryCallDto.Response inventoryCall = inventoryService.getInventoryCall(storeId);
        return BaseResponse.success(inventoryCall);
    }



}