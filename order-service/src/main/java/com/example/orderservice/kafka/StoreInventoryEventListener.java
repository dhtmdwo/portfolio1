package com.example.orderservice.kafka;

import com.example.common.kafka.dto.StoreInventoryDeleteEvent;
import com.example.common.kafka.dto.StoreInventoryEvent;
import com.example.common.kafka.dto.StoreInventoryUpdatedEvent;
import com.example.orderservice.inventory.model.StoreInventory;
import com.example.orderservice.inventory.repository.StoreInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInventoryEventListener {
    private final StoreInventoryRepository repository;

    @KafkaListener(
            topics = "store-inventory-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleStoreInventoryEvent(StoreInventoryEvent event) {
        log.info("Received StoreInventoryEvent: {}", event);
        StoreInventory lite = null;
        if (repository.existsById(event.getId())) {
            lite = StoreInventory.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .storeId(event.getStoreId())
                    .quantity(event.getQuantity())
                    .unit(event.getUnit())
                    .build();
        } else {
            lite = StoreInventory.builder()
                    .name(event.getName())
                    .storeId(event.getStoreId())
                    .quantity(event.getQuantity())
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
}
