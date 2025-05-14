package com.example.be12fin5verdosewmthisbe.redis;

import com.example.be12fin5verdosewmthisbe.market_management.market.model.dto.InventorySaleDto;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.model.dto.StoreDto;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventorySaleRegisteredEventListener {

    private final StoreRepository storeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener
    public void handleInventorySaleRegisteredEvent(InventorySaleRegisteredEvent event) {
        Long storeId = event.getStoreId();
        InventorySaleDto.InventorySaleListDto inventorySaleDto = event.getInventorySale();

        // 3km 이내 가게 조회
        List<Store> nearbyStores = storeRepository.findNearbyStoresByStoreId(storeId, 3000.0);

        for (Store nearbyStore : nearbyStores) {
            try {
                String redisKey = "nearbyStores:" + nearbyStore.getId();
                String json = redisTemplate.opsForValue().get(redisKey);

                if (json == null) continue;

                List<StoreDto.response> storeList = objectMapper.readValue(json, new TypeReference<>() {});
                boolean updated = false;

                for (StoreDto.response storeDto : storeList) {
                    if (storeDto.getStoreId().equals(storeId)) {
                        storeDto.getBoardList().add(inventorySaleDto);  // boardList에 추가
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    Store store = storeRepository.findById(storeId).orElse(null);
                    if (store == null) continue;

                    List<InventorySaleDto.InventorySaleListDto> boardList = new ArrayList<>();
                    boardList.add(inventorySaleDto);

                    StoreDto.response newStoreDto = StoreDto.response.from(store, boardList);
                    storeList.add(newStoreDto); // 신규 storeDto 추가
                }

                String updatedJson = objectMapper.writeValueAsString(storeList);
                redisTemplate.opsForValue().set(redisKey, updatedJson);

                log.info("Redis 캐시 [{}]에 storeId {} 정보 갱신 완료", redisKey, storeId);

            } catch (Exception e) {
                log.error("Redis 캐시 업데이트 중 오류 발생", e);
            }
        }
    }
}
