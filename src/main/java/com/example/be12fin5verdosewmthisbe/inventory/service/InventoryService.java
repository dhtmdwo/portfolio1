package com.example.be12fin5verdosewmthisbe.inventory.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.*;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDetailRequestDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.repository.InventoryRepository;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;

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
}