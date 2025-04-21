package com.example.be12fin5verdosewmthisbe.store.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.model.dto.StoreDto;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreService {
    // Your code here
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public String registerStore(StoreDto.RegistRequest dto, String emailUrl) {
        User user = userRepository.findByEmail(emailUrl).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND) );
        Store registerStore = dto.toEntity(user);
        Store store =  storeRepository.save(registerStore);
        return  String.valueOf(store.getId());
    }

}
        