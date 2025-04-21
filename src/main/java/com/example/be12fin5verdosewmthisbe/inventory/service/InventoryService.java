package com.example.be12fin5verdosewmthisbe.inventory.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.*;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDetailRequestDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryInfoDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryMenuDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.StoreInventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.repository.InventoryRepository;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderMenuRepository;
import com.example.be12fin5verdosewmthisbe.payment.model.Payment;
import com.example.be12fin5verdosewmthisbe.payment.repository.PaymentRepository;
import com.example.be12fin5verdosewmthisbe.payment.service.PaymentService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final OrderMenuRepository orderMenuRepository;

    public StoreInventory registerInventory(InventoryDetailRequestDto dto) {
        // 이름 중복 검사
        if (storeInventoryRepository.existsByName(dto.getName())) {
            throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
        }

        try {
            StoreInventory newStoreInventory = StoreInventory.builder()
                    .name(dto.getName())
                    .miniquantity(dto.getMiniquantity())
                    .unit(dto.getUnit())
                    .quantity(BigDecimal.ZERO)
                    .expiryDate(dto.getExpiryDate())
                    .build();

            return storeInventoryRepository.save(newStoreInventory);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
        }
    }

    public StoreInventory totalInventory(InventoryDetailRequestDto dto) {
        // 이름 중복 검사
        if (storeInventoryRepository.existsByName(dto.getName())) {
            throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
        }

        try {
            StoreInventory newStoreInventory = StoreInventory.builder()
                    .name(dto.getName())
                    .miniquantity(dto.getMiniquantity())
                    .unit(dto.getUnit())
                    .quantity(BigDecimal.valueOf(10.2))
                    .expiryDate(dto.getExpiryDate())
                    .build();

            return storeInventoryRepository.save(newStoreInventory);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
        }
    }

    public Inventory DetailInventory(InventoryDto dto) {
        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(()-> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        Integer unitPrice = new BigDecimal(dto.getTotalPrice()).divide(dto.getQuantity(),2, RoundingMode.CEILING).intValue();
        Timestamp purchaseDate = dto.getPurchaseDate();
        LocalDate expiryDate = purchaseDate.toLocalDateTime().toLocalDate().plusDays(storeInventory.getExpiryDate());
        Inventory newInventory = Inventory.builder()
                .purchaseDate(dto.getPurchaseDate())
                .expiryDate(expiryDate)
                .quantity(dto.getQuantity())
                .unitPrice(unitPrice)
                .storeInventory(storeInventory)
                .build();
        return inventoryRepository.save(newInventory);
    }
    // ID로 기존 재고 조회
    public StoreInventory findById(Long inventoryId) {
        return storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    public StoreInventory updateInventory(Long inventoryId, InventoryDetailRequestDto dto) {
        try {
            StoreInventory inventory = storeInventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

            inventory.setName(dto.getName());
            inventory.setMiniquantity(dto.getMiniquantity());
            inventory.setUnit(dto.getUnit());
            inventory.setExpiryDate(dto.getExpiryDate());

            return storeInventoryRepository.save(inventory);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_UPDATE_FAIL);
        }
    }



    public void deleteById(Long inventoryId) {
        StoreInventory inventory = storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        try {
            storeInventoryRepository.delete(inventory);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.INVENTORY_DELETE_FAIL);
        }
    }
    public List<StoreInventoryDto.responseDto> getAllStoreInventories() {
        return storeInventoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private StoreInventoryDto.responseDto toDto(StoreInventory storeInventory) {
        return StoreInventoryDto.responseDto.builder()
                .id(storeInventory.getStoreinventoryId())
                .name(storeInventory.getName())
                .expiryDate(storeInventory.getExpiryDate())
                .miniquantity(storeInventory.getMiniquantity())
                .unit(storeInventory.getUnit())
                .build();
    }

    public List<InventoryInfoDto.Response> getInventoryList(Long storeId) {

        List<StoreInventory> inventoryList = storeInventoryRepository.findInventoryListByStore(storeId);
        List<InventoryInfoDto.Response> inventoryResponseList = new ArrayList<>();

        for (StoreInventory inventory : inventoryList) {
            String name = inventory.getName();
            BigDecimal quantity = inventory.getQuantity();
            String unit = inventory.getUnit();
            InventoryInfoDto.Response inventoryResponse = InventoryInfoDto.Response.of(name, quantity, unit);
            inventoryResponseList.add(inventoryResponse);
        }
        return(inventoryResponseList);
    }

    public List<InventoryMenuDto.SaleResponse> getSaleList(Long storeId, InventoryMenuDto.DateRequest dto) {

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());


        List<OrderMenu> saleList = orderMenuRepository.findSaleMenusForInventoryByStoreAndPeriod(storeId, startTimestamp, endTimestamp);
        List<InventoryMenuDto.SaleResponse> menuSaleList = new ArrayList<>();

        for (OrderMenu orderMenu : saleList) {
            Timestamp date = orderMenu.getOrder().getCreatedAt();
            List<Recipe> RecipeList = orderMenu.getMenu().getRecipeList();
            String changeReason = orderMenu.getMenu().getName();
            int menuQuantity = orderMenu.getQuantity();
            for(Recipe recipe : RecipeList ) {
                String stockName = recipe.getStoreInventory().getName();
                BigDecimal quantity = recipe.getPrice().multiply(BigDecimal.valueOf(menuQuantity));
                String unit = recipe.getStoreInventory().getUnit();
                InventoryMenuDto.SaleResponse menuSale = InventoryMenuDto.SaleResponse.of(date, stockName, changeReason, quantity, unit);
                menuSaleList.add(menuSale);
            }
        }
        return(menuSaleList);
    }
  
    @Transactional
    public void consumeInventory(Long storeInventoryId, BigDecimal requestedQuantity) {
        List<Inventory> inventories = inventoryRepository
                .findByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId);

        BigDecimal remaining = requestedQuantity;

        for (Inventory inventory : inventories) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal available = inventory.getQuantity();

            if (available.compareTo(remaining) <= 0) {
                // 전부 쓰고 삭제
                remaining = remaining.subtract(available);
                inventoryRepository.delete(inventory);
            } else {
                // 일부만 사용
                inventory.setQuantity(available.subtract(remaining));
                inventoryRepository.save(inventory);
                remaining = BigDecimal.ZERO;
            }
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("재고가 부족하여 요청 수량만큼 차감할 수 없습니다.");
        }
    }
    // 전체를 유통기한 빠른 순으로
    public List<Inventory> getSortedInventoriesByExpiry(Long storeInventoryId) {
        return inventoryRepository.findByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId);
    }

    // 가장 먼저 써야 하는 재고 1개
    public Inventory getFirstInventoryToUse(Long storeInventoryId) {
        return inventoryRepository.findTopByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId)
                .orElseThrow(() -> new RuntimeException("해당 storeInventory에 재고가 없습니다."));
    }

}