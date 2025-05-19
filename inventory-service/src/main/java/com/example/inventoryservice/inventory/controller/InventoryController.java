package com.example.inventoryservice.inventory.controller;

import com.example.common.common.config.BaseResponse;
import com.example.inventoryservice.inventory.model.dto.*;
import com.example.inventoryservice.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "재고 관련 API")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/inventory")
@Tag(name = "재고관리", description = "재고 관리 API") // 이 라인을 추가하여 CORS 허용
public class InventoryController {
    private final InventoryService inventoryService;

    //dto로 정보 받아서 StoreInventory 저장
    @PostMapping("/registerStoreInventory")
    public BaseResponse<String> registerStoreInventory(@RequestHeader("X-Store-Id") String storeId, @Valid @RequestBody InventoryDetailRequestDto dto) {
        inventoryService.registerStoreInventory(dto, Long.parseLong(storeId));
        return BaseResponse.success("ok");
    }

    //dto로 정보 받아서 Inventory 저장
    @PostMapping("/registerInventory")
    public BaseResponse<String> registerInventory(@RequestHeader("X-Store-Id") String storeId, @Valid @RequestBody InventoryDto.InventoryRegisterDto dto) {
        inventoryService.registerInventory(dto);
        return BaseResponse.success("ok");
    }

    @PostMapping("/totalInventory")
    public BaseResponse<String> totalInventory(@RequestBody TotalInventoryDto dto) {
        inventoryService.totalInventory(dto);
        return BaseResponse.success("ok");
    }

    @GetMapping("/DetailInventory/{storeInventoryId}")
    public BaseResponse<List<InventoryDto>> getDetailInventoryList(@PathVariable Long storeInventoryId) {
        List<InventoryDto> list = inventoryService.getInventoriesByStoreInventoryId(storeInventoryId);
        return BaseResponse.success(list);
    }

    @GetMapping("/totalInventory/{storeInventoryId}")
    public BaseResponse<List<TotalResponseDto.Response>> getDetailedTotalInventoryList(@RequestHeader("X-Store-Id") String storeId, @PathVariable Long storeInventoryId) {
        // 입고 내역 리스트 조회
        List<TotalResponseDto.Response> list = inventoryService.getDetailedTotalInventoryList(Long.parseLong(storeId), storeInventoryId);
        return BaseResponse.success(list);
    }

    @PutMapping("/storeInventory")
    public BaseResponse<String> updateInventory(
            @RequestBody InventoryDetailRequestDto dto) {
        inventoryService.updateInventory(dto);
        return BaseResponse.success("수정되었습니다.");
    }

    @DeleteMapping("/storeInventory")
    public BaseResponse<String> deleteInventories(@RequestBody List<Long> inventoryIds) {
        inventoryService.deleteByIds(inventoryIds);
        return BaseResponse.success("선택한 재고가 성공적으로 삭제되었습니다.");
    }


    @GetMapping("/storeInventory/getList")
    public BaseResponse<Page<StoreInventoryDto.responseDto>> getAllStoreInventories(
            @RequestHeader("X-Store-Id") String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword  // ✅ 추가
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<StoreInventoryDto.responseDto> result = inventoryService.getAllStoreInventories(Long.parseLong(storeId), pageable, keyword);
        return BaseResponse.success(result);
    }


    @GetMapping("/inventoryList")
    public BaseResponse<List<InventoryListDto>> getInventoryList(@RequestHeader("X-Store-Id") String storeId) {

        List<InventoryListDto> inventoryList = inventoryService.getInventoryList(Long.parseLong(storeId));
        return BaseResponse.success(inventoryList);
    }
    // 재고 종류 리스트로 뽑기

    @PostMapping("/menuMarket")
    public BaseResponse<List<InventoryChangeDto.Response>> getInventoryChangeList(@RequestHeader("X-Store-Id") String storeId, @RequestBody InventoryChangeDto.DateRequest dto) {
        List<InventoryChangeDto.Response> SaleList = inventoryService.getInventoryChangeList(Long.parseLong(storeId), dto);
        return BaseResponse.success(SaleList);
    }
    // 메뉴, 장터로 재고 얼마나 사용됐나

    @PostMapping("/updateSolo")
    public BaseResponse<List<InventoryChangeDto.Response>> getUpdateList(@RequestHeader("X-Store-Id") String storeId, @RequestBody InventoryChangeDto.DateRequest dto) {
        List<InventoryChangeDto.Response> SaleList = inventoryService.getUpdateList(Long.parseLong(storeId), dto);
        return BaseResponse.success(SaleList);
    }

    @GetMapping("/inventoryCall")
    public BaseResponse<InventoryCallDto.Response> getInventoryCall(@RequestHeader("X-Store-Id") String storeId) {
        InventoryCallDto.Response inventoryCall = inventoryService.getInventoryCall(Long.parseLong(storeId));
        return BaseResponse.success(inventoryCall);
    }

    @GetMapping("/inventoryAmount")
    public BaseResponse<InventoryUpdateDto.Response> getTotalUpdateNumber(@RequestHeader("X-Store-Id") String storeId) {
        InventoryUpdateDto.Response result = inventoryService.getTotalUpdateNumber(Long.parseLong(storeId));
        return BaseResponse.success(result);
    }

    /*@GetMapping("/marketAmount")
    public BaseResponse<InventoryNotUsed> getMaximumMarketPurchase(@RequestHeader("X-Store-Id") String storeId) {
        InventoryNotUsed inventoryNotUsed = inventoryService.getMaximumMarketPurchase(storeId);
        return BaseResponse.success(inventoryNotUsed);
    }*/
    // 이번주 재료 보정 얼마나 발생했는지


    @PutMapping("/inventory")
    public BaseResponse<String> updateInventory(@RequestBody @Valid InventoryDto.InventoryUpdateDto dto) {
        inventoryService.updateInventory(dto);
        return BaseResponse.success("재고 보정 완료");
    }
    @DeleteMapping("/inventory")
    public BaseResponse<String> deleteInventory(@RequestBody List<Long> inventoryid) {
        inventoryService.deleteById(inventoryid);
        return BaseResponse.success("선택한 재고가 성공적으로 삭제되었습니다.");
    }


}