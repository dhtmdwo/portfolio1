package com.example.common.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateEvent {

    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private byte[] location;

    private Double latitude;

    private Double longitude;
}
