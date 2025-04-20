package com.example.be12fin5verdosewmthisbe.market_management.market.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
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

    public void saleRegister(InventorySaleDto.InventorySaleRequestDto dto,Long storeId) {

        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        InventorySale inventorySale = InventorySale.builder()
                .inventoryName(storeInventory.getName())
                .storeInventory(storeInventory)
                .sellerStoreId(storeId)
                .sellerStoreName(store.getName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventorySale.saleStatus.valueOf("available"))
                .content(dto.getContent())
                .imageList(new ArrayList<>())
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

        InventoryPurchase purchase = InventoryPurchase.builder()
                .inventoryName(dto.getInventoryName())
                .buyerStoreId(storeId)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .status(InventoryPurchase.purchaseStatus.valueOf("waiting"))
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
        return inventorySaleRepository.findBySellerStoreId(sellerStoreId);
    }
    public List<InventoryPurchase> findInventoryPurchaseByBuyerStoreId(Long buyerStoreId) {
        return inventoryPurchaseRepository.findInventoryPurchaseByBuyerStoreId(buyerStoreId);
    }
    @Transactional
    public List<TransactionDto> getAllTransactions(Long StoreId,String keyword) {
        List<InventorySale> inventorySaleList = findInventorySaleBySellerStoreId(StoreId);
        List<InventoryPurchase> inventoryPurchaseList = findInventoryPurchaseByBuyerStoreId(StoreId);

        List<TransactionDto> saleTransactionDtoList = new java.util.ArrayList<>(inventorySaleList.stream()
                .map(sale -> {
                    return TransactionDto.builder()
                            .inventorySaleId(sale.getId())
                            .name(sale.getInventoryName())
                            .price(sale.getPrice())
                            .type(true)
                            .quantity(sale.getQuantity())
                            .status(String.valueOf(sale.getStatus()))
                            .otherStoreName(sale.getBuyerStoreName())
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
                purchase.setStatus(InventoryPurchase.purchaseStatus.payment);
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

    /*public List<InventorySaleDto.InventorySaleResponseDto> getAvailableOrWaitingSales(Long storeId) {
        List<InventorySale> sales = inventorySaleRepository.findBySellerStoreIdAndStatusIn(
                storeId,
                Arrays.asList(InventorySale.saleStatus.available, InventorySale.saleStatus.waiting)
        );
        return sales.stream()
                .map(sale -> {
                    String inventoryName = inventoryRepository.findById(sale.getInventoryId())
                            .map(Inventory::getName)
                            .orElse("Unknown Inventory"); // 예외 처리

                    String sellerStoreName = storeRepository.findById(sale.getSellerStoreId())
                            .map(Store::getName)
                            .orElse("Unknown Store"); // 예외 처리

                    return new InventorySaleDto.InventorySaleResponseDto(
                            inventoryName,
                            sellerStoreName,
                            sale.getQuantity(),
                            sale.getPrice()
                    );
                })
                .toList();
    }*/

    /*public List<InventoryPurchaseDto.InventoryPurchaseResponseDto> getPurchasesBySaleId(Long saleId) {
        InventorySale sale = inventorySaleRepository.findById(saleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALE_NOT_FOUND));

        return sale.getPurchaseList().stream()
                .map(purchase -> {
                    String buyerName = storeRepository.findById(purchase.getBuyerStoreId())
                            .map(Store::getName)
                            .orElse("알 수 없음");
                    return new InventoryPurchaseDto.InventoryPurchaseResponseDto(purchase,buyerName);
                })
                .toList();
    }*/
    public List<InventorySaleDto.InventorySaleListDto> getNearbyAvailableSalesDto(List<Long> nearbyStoreIds) {
        List<InventorySale> sales = inventorySaleRepository
                .findBySellerStoreIdInAndStatus(nearbyStoreIds, InventorySale.saleStatus.available);

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
}