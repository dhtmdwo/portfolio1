package com.example.orderservice.kafka;

import com.example.common.kafka.dto.StoreInventoryEvent;
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

        StoreInventory lite = StoreInventory.builder()
                .id(event.getId())
                .name(event.getName())
                .storeId(event.getStoreId())
                .unit(event.getUnit())
                .build();
        repository.save(lite);
    }
    @KafkaListener(
            topics = "store-inventory-test-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleStoreInventoryTestEvent(String text) {
        log.info("text: {}", text);

    }
}
