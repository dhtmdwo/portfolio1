package com.example.userservice.store.service;

import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.userservice.store.model.Store;
import com.example.userservice.store.model.dto.StoreDto;
import com.example.userservice.store.repository.StoreRepository;
import com.example.userservice.user.model.User;
import com.example.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public Long registerStore(StoreDto.RegistRequest dto, String emailUrl) {
        User user = userRepository.findByEmail(emailUrl).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND) );
        Store registerStore = dto.toEntity(user,dto.getLatitude(),dto.getLongitude());
        return storeRepository.save(registerStore).getId();

    }

    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_EXIST));
    }

}
        