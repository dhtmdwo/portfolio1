package com.example.be12fin5verdosewmthisbe.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Payment {
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
        