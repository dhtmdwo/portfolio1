package com.example.be12fin5verdosewmthisbe.market_management.market.service;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.Inventory;
import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.Images;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventoryPurchaseDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.TransactionDto;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.ImagesRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventoryPurchaseRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventorySaleRepository;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final ImagesRepository imagesRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final InventorySaleRepository inventorySaleRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final StoreRepository storeRepository;

    public void saleRegister(InventorySaleDto.InventorySaleRequestDto dto, Long storeId, Inventory inventory) {

        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND));


        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        InventorySale inventorySale = InventorySale.builder()
                .inventoryName(storeInventory.getName())
                .storeInventory(storeInventory)
                .expiryDate(inventory.getExpiryDate())
                .sellerStoreName(store.getName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventorySale.saleStatus.valueOf("available"))
                .content(dto.getContent())
                .imageList(new ArrayList<>())
                .store(store)
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
    public void purchaseRegister(InventoryPurchaseDto.InventoryPurchaseRequestDto dto,Long storeId) {
        InventorySale sale = inventorySaleRepository.findById(dto.getInventorySaleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));

        if(sale.getStatus() == InventorySale.saleStatus.valueOf("available")) {
            sale.setStatus(InventorySale.saleStatus.valueOf("waiting"));
            inventorySaleRepository.save(sale);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        InventoryPurchase purchase = InventoryPurchase.builder()
                .inventoryName(dto.getInventoryName())
                .store(store)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventoryPurchase.purchaseStatus.PENDING_APPROVAL)
                .method(InventoryPurchase.purchaseMethod.valueOf(dto.getMethod()))
                .createdAt(Timestamp.from(Instant.now()))
                .inventorySale(sale)
                .build();

        inventoryPurchaseRepository.save(purchase);
    }

    public InventorySale findInventorySaleById(Long id) {
        return inventorySaleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));
    }

    public List<InventorySale> findInventorySaleBySellerStoreId(Long sellerStoreId) {
        return inventorySaleRepository.findByStore_Id(sellerStoreId);
    }
    public List<InventoryPurchase> findInventoryPurchaseByBuyerStoreId(Store store) {
        return inventoryPurchaseRepository.findInventoryPurchaseByStore(store);
    }
    @Transactional
    public List<TransactionDto> getAllTransactions(Long storeId,String keyword) {
        List<InventorySale> inventorySaleList = findInventorySaleBySellerStoreId(storeId);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        List<InventoryPurchase> inventoryPurchaseList = findInventoryPurchaseByBuyerStoreId(store);

        List<TransactionDto> saleTransactionDtoList = new ArrayList<>(inventorySaleList.stream()
                .map(sale -> {
                    return TransactionDto.builder()
                            .inventorySaleId(sale.getId())
                            .name(sale.getInventoryName())
                            .price(sale.getPrice())
                            .type(true)
                            .quantity(sale.getQuantity())
                            .status(String.valueOf(sale.getStatus()))
                            .otherStoreName(sale.getBuyerStoreName())
                            .createdAt(sale.getCreatedAt().toLocalDateTime().toLocalDate())
                            .build();
                }).toList());
        List<TransactionDto> purchaseTransactionDtoList = inventoryPurchaseList.stream()
                .map(sale -> {
                    return TransactionDto.builder()
                            .inventoryPurchaseId(sale.getId())
                            .name(sale.getInventorySale().getInventoryName()) // n+1예상
                            .price(sale.getPrice())
                            .type(false)
                            .quantity(sale.getQuantity())
                            .status(String.valueOf(sale.getStatus()))
                            .createdAt(sale.getCreatedAt().toLocalDateTime().toLocalDate())
                            .otherStoreName(sale.getInventorySale().getSellerStoreName())
                            .build();
                }).toList();

        List<TransactionDto> allTransactions = new ArrayList<>();
        allTransactions.addAll(saleTransactionDtoList);
        allTransactions.addAll(purchaseTransactionDtoList);

        return allTransactions.stream()
                .filter(dto -> keyword == null || dto.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public void confirmEnd(Long purchaseId) {
        InventoryPurchase inventoryPurchase = inventoryPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new CustomException(ErrorCode.PURCHASE_NOT_FOUND));
        InventorySale inventorySale = inventoryPurchase.getInventorySale();
        inventoryPurchase.setStatus(InventoryPurchase.purchaseStatus.end);
        inventorySale.setStatus(InventorySale.saleStatus.sold);
        inventorySaleRepository.save(inventorySale);
        inventoryPurchaseRepository.save(inventoryPurchase);
    }

    @Transactional
    public void approvePurchase(Long saleId, Long purchaseId) {
        InventorySale sale = inventorySaleRepository.findById(saleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));

        List<InventoryPurchase> purchases = sale.getPurchaseList();

        boolean found = false;
        for (InventoryPurchase purchase : purchases) {
            if (purchase.getId().equals(purchaseId)) {
                purchase.setStatus(InventoryPurchase.purchaseStatus.isPaymentInProgress);

                sale.setPrice(purchase.getPrice());
                sale.setQuantity(purchase.getQuantity());
                sale.setBuyerStoreName(purchase.getStore().getName());
                sale.setStatus(InventorySale.saleStatus.isPaymentPending);
                sale.setInventoryPurchaseId(purchaseId);
                inventorySaleRepository.save(sale);
                found = true;
            } else {
                purchase.setStatus(InventoryPurchase.purchaseStatus.cancelled);
            }
        }

        if (!found) {
            throw new CustomException(ErrorCode.PURCHASE_NOT_FOUND);
        }

        inventoryPurchaseRepository.saveAll(purchases);

    }

    public void rejectPurchase(Long purchaseId) {
        InventoryPurchase inventoryPurchase = inventoryPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new CustomException(ErrorCode.PURCHASE_NOT_FOUND));

        inventoryPurchase.setStatus(InventoryPurchase.purchaseStatus.cancelled);
        inventoryPurchaseRepository.save(inventoryPurchase);
    }

    public List<InventoryPurchaseDto.InventoryPurchaseResponseDto> getPurchasesBySaleId(Long saleId) {
        InventorySale sale = inventorySaleRepository.findById(saleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));

        return sale.getPurchaseList().stream()
                .filter(purchase -> purchase.getStatus() == InventoryPurchase.purchaseStatus.PENDING_APPROVAL) // 상태가 WAITING인 것만 필터링
                .map(purchase -> {
                    String buyerName = storeRepository.findById(purchase.getStore().getId())
                            .map(Store::getName)
                            .orElse("알 수 없음");
                    return new InventoryPurchaseDto.InventoryPurchaseResponseDto(purchase, buyerName);
                })
                .toList();
    }

    public List<InventorySaleDto.InventorySaleListDto> getNearbyAvailableSalesDto(List<Long> nearbyStoreIds) {
        List<InventorySale> sales = inventorySaleRepository
                .findByStore_IdInAndStatus(nearbyStoreIds, InventorySale.saleStatus.available);

        return convertToDtoList(sales);
    }

    public List<InventorySaleDto.InventorySaleListDto> convertToDtoList(List<InventorySale> sales) {

        return sales.stream()
                .map(sale -> new InventorySaleDto.InventorySaleListDto(

                        sale.getId(),
                        sale.getInventoryName(),
                        sale.getQuantity().toPlainString() + sale.getStoreInventory().getUnit(),  // BigDecimal → String
                        sale.getExpiryDate(),
                        sale.getPrice(),
                        sale.getCreatedAt().toLocalDateTime().toLocalDate(), // Timestamp → LocalDate
                        sale.getSellerStoreName()
                ))
                .collect(Collectors.toList());
    }
    public InventoryPurchase findPurchaseById(Long purchaseId) {
        return inventoryPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new CustomException(ErrorCode.PURCHASE_NOT_FOUND));
    }
    public void statusChange(Long purchaseId) {
        InventoryPurchase inventoryPurchase = inventoryPurchaseRepository.findById(purchaseId)
                .orElseThrow(()-> new CustomException(ErrorCode.PURCHASE_NOT_FOUND));
        inventoryPurchase.setStatus(InventoryPurchase.purchaseStatus.confirmDelivery);
        InventorySale inventorySale = inventoryPurchase.getInventorySale();
        inventorySale.setStatus(InventorySale.saleStatus.delivery);
        inventoryPurchaseRepository.save(inventoryPurchase);
        inventorySaleRepository.save(inventorySale);
    }
}