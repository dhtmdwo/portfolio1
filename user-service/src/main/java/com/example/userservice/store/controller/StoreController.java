package com.example.userservice.store.controller;

import com.example.common.BaseResponse;
import com.example.userservice.store.model.Store;
import com.example.userservice.store.model.dto.StoreDto;
import com.example.userservice.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/register")
    public BaseResponse<String> registerStore(@RequestHeader("X-Email-Url") String emailUrl, @RequestBody StoreDto.RegistRequest dto) {
        storeService.registerStore(dto, emailUrl);
        return BaseResponse.success("가게등록에 성공했습니다.");
    }

    @GetMapping("/getAddress")
    public BaseResponse<StoreDto.response> getAddress(@RequestHeader("X-Store-Id") String storeId) {
        Store store = storeService.getStoreById(Long.parseLong(storeId));
        StoreDto.response response = StoreDto.response.builder()
                .address(store.getAddress())
                .name(store.getName())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .phoneNumber(store.getPhoneNumber())
                .build();
        return BaseResponse.success(response);
    }

}
        