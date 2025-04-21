package com.example.be12fin5verdosewmthisbe.store.model.dto;


import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import jakarta.persistence.Column;
import lombok.Getter;


public class StoreDto {
    // Your code here

    @Getter
    public static class RegistRequest{
        private String name;
        private String address;
        private String phoneNumber;
        private String latitude;
        private String longitude;

        public Store toEntity(User user,String latitude, String longitude){
            return Store.builder()
                    .name(name)
                    .address(address)
                    .phoneNumber(phoneNumber)
                    .latitude(Double.parseDouble(latitude))
                    .longitude(Double.parseDouble(longitude))
                    .user(user)
                    .build();
        }

    }
}
        