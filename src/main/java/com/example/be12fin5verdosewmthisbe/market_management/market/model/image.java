package com.example.be12fin5verdosewmthisbe.market_management.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String url;
}
