package com.example.be12fin5verdosewmthisbe.store.model.dto;


import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import lombok.Getter;


public class StoreDto {
    // Your code here

    @Getter
    public static class RegistRequest{
        private String name;
        private String address;
        private String phoneNumber;

        public Store toEntity(User user){
            Store store = Store.builder()
                    .name(name)
                    .address(address)
                    .phoneNumber(phoneNumber)
                    .user(user)
                    .build();
            return store;
        }

    }
}
        