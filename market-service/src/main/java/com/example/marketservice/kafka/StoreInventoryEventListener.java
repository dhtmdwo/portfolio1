package com.example.marketservice.kafka;

import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.common.common.config.KafkaTopic;
import com.example.common.kafka.dto.*;
import com.example.marketservice.market.model.Store;
import com.example.marketservice.market.model.Inventory;
import com.example.marketservice.market.model.StoreInventory;
import com.example.marketservice.market.repository.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;
    @KafkaListener(
            topics = KafkaTopic.STORE_INVENTORY_CREATE_TOPIC
    )
    public void handleStoreInventoryEvent(StoreInventoryEvent event) {
        log.info("Received StoreInventoryEvent: {}", event);
        Store store = storeRepository.findById(event.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        StoreInventory lite = StoreInventory.builder()
                .id(event.getId())
                .name(event.getName())
                .store(store)
                .quantity(event.getQuantity())
                .expiryDate(event.getExpiryDate())
                .minQuantity(event.getMinQuantity())
                .unit(event.getUnit())
                .build();

        repository.save(lite);
    }
    // 삭제 이벤트 처리
    @KafkaListener(
            topics = KafkaTopic.STORE_INVENTORY_DELETE_TOPIC
    )
    public void handleDeleteEvent(StoreInventoryDeleteEvent event) {
        log.info("Received delete event: {}", event.getInventoryIds());
        repository.deleteAllById(event.getInventoryIds());
    }

    @KafkaListener(
            topics = KafkaTopic.STORE_INVENTORY_UPDATE_TOPIC
    )
    public void handleUpdateEvent(StoreInventoryUpdatedEvent event) {
        log.info("Received updated event: {}", event);
        repository.findById(event.getId()).ifPresent(si -> {
            si.setQuantity(event.getRemainingQuantity());
            repository.save(si);
        });
    }
    @KafkaListener(
            topics = KafkaTopic.INVENTORY_CREATE_TOPIC
    )
    public void handleInventoryRegisteredEvent(InventoryRegisteredEvent event) {
        log.info("Received InventoryRegisteredEvent: {}", event);

        StoreInventory storeInventory = repository.findById(event.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));

        Inventory detail = Inventory.builder()
                .id(event.getId())
                .storeInventory(storeInventory)
                .quantity(event.getQuantity())
                .purchaseDate(event.getPurchaseDate())
                .expiryDate(event.getExpiryDate())
                .build();

        inventoryRepository.save(detail);
    }

    @KafkaListener(
            topics = KafkaTopic.INVENTORY_UPDATE_TOPIC
    )
    public void handleUpdatedEvent(InventoryUpdateEvent event) {
        log.info("Received updated event: {}", event);
        log.info(event.getId().toString());
        log.info(event.getQuantity().toString());
        inventoryRepository.findById(event.getId()).ifPresent(i -> {
            i.setQuantity(event.getQuantity());
            i.setExpiryDate(event.getExpiryDate());
            inventoryRepository.save(i);
        });
    }


    @KafkaListener(
            topics = KafkaTopic.INVENTORY_DELETE_TOPIC
    )
    public void handleDeleteEvent(InventoryDeleteEvent event) {
        inventoryRepository.deleteAllById(event.getInventoryIds());
    }

    @KafkaListener(
            topics = KafkaTopic.STORE_CREATE_TOPIC
    )
    public void handleStoreCreateEvent(StoreCreateEvent event) {
        log.info("Received StoreCreateEvent: {}", event);
        storeRepository.save(Store.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .address(event.getAddress())
                        .phoneNumber(event.getPhoneNumber())
                        .location(event.getLocation())
                        .latitude(event.getLatitude())
                        .longitude(event.getLongitude())
                        .build());
    }
}
