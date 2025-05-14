package com.example.inventoryservice.kafka;

import com.example.common.dto.InventoryConsumeEvent;
import com.example.inventoryservice.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventListener {

    private final InventoryService inventoryService;

    public InventoryEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "inventory.consume",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryConsume(InventoryConsumeEvent event) {
        // 실제 재고 차감 로직 호출
        inventoryService.consumeInventories(event.getUsedInventoryQty());
    }
}
