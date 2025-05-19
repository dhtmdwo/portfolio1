package com.example.inventoryservice.kafka;

import com.example.common.common.config.KafkaTopic;
import com.example.common.kafka.dto.*;
import com.example.inventoryservice.inventory.model.StoreInventory;
import com.example.inventoryservice.inventory.model.dto.InventoryDto;
import com.example.inventoryservice.inventory.repository.StoreInventoryRepository;
import com.example.inventoryservice.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final StoreInventoryRepository storeInventoryRepository;


    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = KafkaTopic.INVENTORY_LIST_CONSUME_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryListConsume(InventoryConsumeEvent event) {
        // 실제 재고 차감 로직 호출
        inventoryService.consumeInventories(event.getUsedInventoryQty());
    }

    @KafkaListener(
            topics = KafkaTopic.INVENTORY_CONSUME_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryConsume(ConsumeEvent event) {
        // 실제 재고 차감 로직 호출
        inventoryService.consumeInventory(event.getStoreInventoryId(),event.getQuantity());
    }

    /**
     * 시장 거래로 인한 재고 등록/추가 이벤트 처리
     */
    @KafkaListener(
            topics = KafkaTopic.MARKET_INVENTORY_CREATE_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryRegister(InventoryRegisterEvent event) {
        inventoryService.registerInventory(InventoryDto.InventoryRegisterDto.builder()
                .storeInventoryId(event.getStoreInventoryId())
                .price(event.getPrice())
                .quantity(event.getQuantity())
                .build());
    }

    @KafkaListener(
            topics=KafkaTopic.MARKET_STORE_INVENTORY_CREATE_TOPIC,
            containerFactory="kafkaListenerContainerFactory"
    )
    public void onCreate(StoreInventoryCreateEvent evt) {
        StoreInventory si = StoreInventory.builder()
                .storeId(evt.getStoreId())
                .unit(evt.getUnit())
                .name(evt.getName())
                .minQuantity(evt.getMinQuantity())
                .quantity(evt.getInitialQuantity())
                .expiryDate(evt.getExpiryDate())
                .build();
        StoreInventory saved = storeInventoryRepository.save(si);

        StoreInventoryEvent event = new StoreInventoryEvent(
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getMinQuantity(),
                saved.getExpiryDate(),
                saved.getUnit(),
                saved.getStoreId()
        );
        kafkaTemplate.send(KafkaTopic.STORE_INVENTORY_CREATE_TOPIC,
                si.getStoreId().toString(), event
        );
        inventoryService.registerInventory(InventoryDto.InventoryRegisterDto.builder()
                .price(evt.getPrice())
                .quantity(evt.getQuantity())
                .storeInventoryId(saved.getId())
                .build());
    }
}
