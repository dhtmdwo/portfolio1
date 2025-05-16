package com.example.marketservice.kafka;

import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.common.kafka.dto.InventoryRegisteredEvent;
import com.example.common.kafka.dto.StoreInventoryDeleteEvent;
import com.example.common.kafka.dto.StoreInventoryEvent;
import com.example.common.kafka.dto.StoreInventoryUpdatedEvent;
import com.example.marketservice.market.model.Store;
import com.example.marketservice.market.model.StoreInventory;
import com.example.marketservice.market.repository.StoreInventoryRepository;
import com.example.marketservice.market.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInventoryEventListener {
    private final StoreInventoryRepository repository;
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    @KafkaListener(
            topics = "store-inventory-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleStoreInventoryEvent(StoreInventoryEvent event) {
        log.info("Received StoreInventoryEvent: {}", event);
        StoreInventory lite = null;
        Store store = storeRepository.findById(event.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        if (repository.existsById(event.getId())) {
            lite = StoreInventory.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .store(store)
                    .quantity(event.getQuantity())
                    .expiryDate(event.getExpiryDate())
                    .minQuantity(event.getMinQuantity())
                    .unit(event.getUnit())
                    .build();
        } else {
            lite = StoreInventory.builder()
                    .name(event.getName())
                    .store(store)
                    .quantity(event.getQuantity())
                    .expiryDate(event.getExpiryDate())
                    .minQuantity(event.getMinQuantity())
                    .unit(event.getUnit())
                    .build();
        }

        repository.save(lite);
    }
    // 삭제 이벤트 처리
    @KafkaListener(
            topics = "store-inventory-delete-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDeleteEvent(StoreInventoryDeleteEvent event) {
        log.info("Received delete event: {}", event.getInventoryIds());
        repository.deleteAllById(event.getInventoryIds());
    }

    @KafkaListener(
            topics = "store-inventory-updated-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUpdatedEvent(StoreInventoryUpdatedEvent event) {
        log.info("Received updated event: {}", event);
        repository.findById(event.getId()).ifPresent(si -> {
            si.setQuantity(event.getRemainingQuantity());
            repository.save(si);
        });
    }
    @KafkaListener(
            topics = "inventory-registered-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleInventoryRegisteredEvent(InventoryRegisteredEvent event) {
        log.info("Received InventoryRegisteredEvent: {}", event);

        // 1) 매장 조회
        StoreInventory storeInventory = storeInventoryRepository.findById(event.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));



        // 2) market-service용 Inventory 엔티티 매핑 및 저장
        Inventory detail = Inventory.builder()
                .storeInventory(storeInventory)
                .quantity(event.getQuantity())
                .unitPrice(event.getUnitPrice())
                .occurredAt(event.getOccurredAt())
                .build();

        storeInventoryDetailRepository.save(detail);
    }
}
