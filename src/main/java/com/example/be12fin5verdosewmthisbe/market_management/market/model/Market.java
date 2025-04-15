package com.example.be12fin5verdosewmthisbe.market_management.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Market {
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
        