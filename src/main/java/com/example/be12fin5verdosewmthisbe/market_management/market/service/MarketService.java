package com.example.be12fin5verdosewmthisbe.market_management.market.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.Images;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.ImagesRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventoryPurchaseRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventorySaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final ImagesRepository imagesRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final InventorySaleRepository inventorySaleRepository;

    public void saleRegister(InventorySaleDto.InventorySaleRequestDto dto) {
        InventorySale inventorySale = InventorySale.builder()
                .inventoryId(dto.getInventoryId())
                .sellerStoreId(dto.getSellerStoreId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventorySale.saleStatus.valueOf(dto.getStatus()))
                .content(dto.getContent())
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        // 이미지 엔티티 생성 및 연결
        if (dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                Images image = Images.builder()
                        .url(url)
                        .inventorySale(inventorySale)
                        .build();
                inventorySale.getImageList().add(image);
            }
        }

        inventorySaleRepository.save(inventorySale);
    }
    public void purchaseRegister(InventoryPurchaseDto.InventoryPurchaseRequestDto dto) {
        InventorySale sale = inventorySaleRepository.findById(dto.getInventorySaleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));

        InventoryPurchase purchase = InventoryPurchase.builder()
                .buyerStoreId(dto.getBuyerStoreId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventoryPurchase.purchaseStatus.valueOf(dto.getStatus()))
                .method(InventoryPurchase.purchaseMethod.valueOf(dto.getMethod()))
                .createdAt(Timestamp.from(Instant.now()))
                .inventorySale(sale)
                .build();

        inventoryPurchaseRepository.save(purchase);
    }
    /*public List<InventorySaleDto.InventorySaleResponseDto> getAvailableOrWaitingSales(Long storeId) {
        List<InventorySale> sales = inventorySaleRepository.findBySellerStoreIdAndStatusIn(
                storeId,
                Arrays.asList(InventorySale.saleStatus.available, InventorySale.saleStatus.waiting)
        );
        return sales.stream()
                .map(sale -> {
                    String inventoryName = inventoryRepository.findById(sale.getInventoryId())
                            .map(Inventory::getName)
                            .orElse("Unknown Inventory");

                    String sellerStoreName = storeRepository.findById(sale.getSellerStoreId())
                            .map(Store::getName)
                            .orElse("Unknown Store");

                    return new InventorySaleDto.InventorySaleResponseDto(
                            inventoryName,
                            sellerStoreName,
                            sale.getQuantity(),
                            sale.getPrice()
                    );
                })
                .toList();
    }*/
}
        