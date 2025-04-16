package com.example.be12fin5verdosewmthisbe.store.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.store.model.dto.StoreDto;
import com.example.be12fin5verdosewmthisbe.store.service.StoreService;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserDto;
import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    // Your code here
    private final StoreService storeService;

    @PostMapping("/register")
    public BaseResponse<String> registerStore(@AuthenticationPrincipal User user, @RequestBody StoreDto.RegistRequest dto) {
        String emailUrl = user.getEmail();
        storeService.registerStore(dto, emailUrl);
        return BaseResponse.success("가게등록에 성공했습니다.");
    }

}
        