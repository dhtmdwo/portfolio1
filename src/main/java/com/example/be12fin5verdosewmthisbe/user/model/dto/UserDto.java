package com.example.be12fin5verdosewmthisbe.user.model.dto;


import com.example.be12fin5verdosewmthisbe.user.model.User;
import lombok.Getter;

public class UserDto {
    // Your code here
    @Getter
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
        