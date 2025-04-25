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
@RequestMapping("/api/inventory")
@Tag(name = "재고관리", description = "재고 관리 API") // 이 라인을 추가하여 CORS 허용
public class InventoryController {
    private final InventoryService inventoryService;
    private final JwtTokenProvider jwtTokenProvider;

    //dto로 정보 받아서 StoreInventory 저장
    @PostMapping("/registerStoreInventory")
    public BaseResponse<String> registerStoreInventory(HttpServletRequest request, @RequestBody InventoryDetailRequestDto dto) {
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

        inventoryService.registerInventory(dto, storeId);
        return BaseResponse.success("ok");
    }

    @PostMapping("/totalInventory")
    public BaseResponse<String> totalInventory(@RequestBody TotalInventoryDto dto) {
//        String token = null;
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if ("ATOKEN".equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//        Claims claims = jwtTokenProvider.getClaims(token);
//        Long storeId = Long.valueOf(claims.get("storeId", String.class));

        inventoryService.totalInventory(dto);
        return BaseResponse.success("ok");
    }


    //dto로 정보 받아서Inventory 저장
    @GetMapping("/DetailInventory/{storeId}")
    public BaseResponse<List<InventoryDto>> getDetailInventoryList(@PathVariable Long storeId) {
        List<InventoryDto> list = inventoryService.getDetailInventoryList(storeId);
        return BaseResponse.success(list);
    }

//    @GetMapping("/storeInventory/{storeinventoryId}")
//    public BaseResponse<StoreInventory> getInventoryById(@PathVariable Long storeinventoryId) {
//        StoreInventory inventory = inventoryService.findById(storeinventoryId);
//        return BaseResponse.success(inventory);
//    }

    @PutMapping("/storeInventory/{storeinventoryId}")
    public BaseResponse<StoreInventory> updateInventory(
            @PathVariable Long storeinventoryId,
            @RequestBody InventoryDetailRequestDto dto) {
        StoreInventory updatedInventory = inventoryService.updateInventory(storeinventoryId, dto);
        return BaseResponse.success(updatedInventory);
    }

    @DeleteMapping("/storeInventory/{storeinventoryId}")
    public BaseResponse<String> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteById(inventoryId);
        return BaseResponse.success("재고가 성공적으로 삭제되었습니다.");
    }


    @GetMapping("/storeInventory/getList")
    public BaseResponse<List<StoreInventoryDto.responseDto>> getAllStoreInventories(HttpServletRequest request) {
        List<StoreInventoryDto.responseDto> result = inventoryService.getAllStoreInventories(getStoreId(request));
        Long id = (getStoreId(request));
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

    @PostMapping("/updateSolo")
    public BaseResponse<List<InventoryChangeDto.Response>> getUpdateList(HttpServletRequest request, @RequestBody InventoryChangeDto.DateRequest dto) {

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
        List<InventoryChangeDto.Response> SaleList = inventoryService.getUpdateList(storeId, dto);
        return BaseResponse.success(SaleList);
    }
    // 수정으로로 재고가 얼마나 변동했나 조회

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

    @GetMapping("/inventoryAmount")
    public BaseResponse<Integer> getTotalUpdateNumber(HttpServletRequest request) {

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
        Integer result = inventoryService.getTotalUpdateNumber(storeId);
        return BaseResponse.success(result);
    }
    // 이번주 재료 보정 얼마나 발생했는지

    @GetMapping("/marketAmount")
    public BaseResponse<InventoryNotUsed> getMaximumMarketPurchase(HttpServletRequest request) {

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
        InventoryNotUsed inventoryNotUsed = inventoryService.getMaximumMarketPurchase(storeId);
        return BaseResponse.success(inventoryNotUsed);
    }
    // 이번주 재료 보정 얼마나 발생했는지

    @PostMapping("/getRecipes")
    public BaseResponse<InventoryRecipes.Response> getRecipes(HttpServletRequest request, @RequestBody InventoryRecipes.Request req) {

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
        InventoryRecipes.Response result = inventoryService.getInventoryRecipes(storeId, req.getInventoryId());
        return BaseResponse.success(result);
    }

}