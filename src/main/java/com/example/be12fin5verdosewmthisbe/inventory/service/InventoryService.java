package com.example.be12fin5verdosewmthisbe.inventory.service;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.*;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDetailRequestDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryInfoDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryMenuDto;
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

}