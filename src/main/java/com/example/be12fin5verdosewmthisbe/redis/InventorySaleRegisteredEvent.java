package com.example.be12fin5verdosewmthisbe.redis;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventorySaleRegisteredEvent {
    private final Long storeId;
    private final InventorySaleDto.InventorySaleListDto inventorySale;
}