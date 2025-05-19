package com.example.userservice.store.service;

import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.common.common.config.KafkaTopic;
import com.example.common.kafka.dto.StoreCreateEvent;
import com.example.userservice.store.model.Store;
import com.example.userservice.store.model.dto.StoreDto;
import com.example.userservice.store.repository.StoreRepository;
import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Long registerStore(StoreDto.RegistRequest dto, String emailUrl) {
        User user = userRepository.findByEmail(emailUrl).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND) );
        Store store = dto.toEntity(user,dto.getLatitude(),dto.getLongitude());

        Store registerStore =  storeRepository.save(store);

        StoreCreateEvent storeCreateEvent = new StoreCreateEvent(
                registerStore.getId(),
                registerStore.getName(),
                registerStore.getAddress(),
                registerStore.getPhoneNumber(),
                registerStore.getLocation(),
                registerStore.getLatitude(),
                registerStore.getLongitude()
        );
        kafkaTemplate.send(KafkaTopic.STORE_CREATE_TOPIC, storeCreateEvent);

        return registerStore.getId();
    }

    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));
    }

}
        