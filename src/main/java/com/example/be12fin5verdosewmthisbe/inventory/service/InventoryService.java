package com.example.be12fin5verdosewmthisbe.inventory.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.*;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDetailRequestDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryDto;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.InventoryUpdateResponseDto;
import com.example.be12fin5verdosewmthisbe.inventory.repository.InventoryRepository;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import jakarta.transaction.Transactional;
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
            // DTO의 toEntity() 메서드 사용
            StoreInventory newStoreInventory = dto.toEntity(); // toEntity() 호출

            return storeInventoryRepository.save(newStoreInventory); // 저장
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
            // DTO의 toEntity() 메서드 사용
            StoreInventory newStoreInventory = dto.toEntity(); // toEntity() 호출

            return storeInventoryRepository.save(newStoreInventory); // 저장
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
        }
    }


    public Inventory DetailInventory(InventoryDto dto) {
        // StoreInventory 객체를 찾아옵니다.
        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        // unitPrice 계산 (단가 계산)
        Integer unitPrice = new BigDecimal(dto.getTotalPrice())
                .divide(dto.getQuantity(), 2, RoundingMode.CEILING)
                .intValue();

        // purchaseDate에서 유통기한을 더하여 expiryDate를 계산합니다.
        LocalDate expiryDate = dto.getPurchaseDate().toLocalDateTime().toLocalDate()
                .plusDays(storeInventory.getExpiryDate()); // 유통기한 추가

        Inventory newInventory = Inventory.builder()
                .purchaseDate(dto.getPurchaseDate())
                .expiryDate(expiryDate)
                .quantity(dto.getQuantity())
                .unitPrice(unitPrice)
                .inventoryId(dto.getStoreInventoryId())
                .build();


        return inventoryRepository.save(newInventory);
    }

    // ID로 기존 재고 조회
    public StoreInventory findById(Long inventoryId) {
        return storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    @Transactional
    public InventoryUpdateResponseDto updateInventory(Long inventoryId, InventoryDetailRequestDto dto) {
        StoreInventory inventory = storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        inventory.setName(dto.getName());
        inventory.setMiniquantity(dto.getMiniquantity());
        inventory.setUnit(dto.getUnit());
        inventory.setExpiryDate(dto.getExpiryDate());

        return InventoryUpdateResponseDto.from(inventory);
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
}