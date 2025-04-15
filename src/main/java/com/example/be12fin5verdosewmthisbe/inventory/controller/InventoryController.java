package com.example.be12fin5verdosewmthisbe.inventory.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDetailRequestDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.service.InventoryService;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.dto.MenuRegistrationDto;
import com.example.be12fin5verdosewmthisbe.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "재고 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/inventory")
@Tag(name = "재고관리", description = "재고 관리 API")
public class InventoryController {
    private final InventoryService inventoryService;


    //dto로 정보 받아서 StoreInventory 저장
    @PostMapping("/registerStoreInventory")
    public BaseResponse<String> registerStoreInventory(@RequestBody InventoryDetailRequestDto dto) {
        inventoryService.registerInventory(dto);
        return BaseResponse.success("ok");
    }

    //dto로 정보 받아서Inventory 저장
    @PostMapping("/DetailInventory")
    public BaseResponse<String> DetailInventory(@RequestBody InventoryDto dto) {
      inventoryService.DetailInventory(dto);
      return BaseResponse.success("ok");
    }

    @GetMapping("/storeInventory/{id}")
    public BaseResponse<StoreInventory> getInventoryById(@PathVariable Long id) {
        StoreInventory inventory = inventoryService.findById(id);
        return BaseResponse.success(inventory);
    }

    @PutMapping("/storeInventory/{id}")
    public BaseResponse<StoreInventory> updateInventory(@PathVariable Long id, @RequestBody InventoryDetailRequestDto dto) {
        StoreInventory updatedInventory = inventoryService.updateInventory(id, dto);
        return BaseResponse.success(updatedInventory);
    }

    @DeleteMapping("/storeInventory/{id}")
    public BaseResponse<String> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteById(id);
        return BaseResponse.success("재고가 성공적으로 삭제되었습니다.");
    }

}