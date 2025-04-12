package com.example.be12fin5verdosewmthisbe.menu_management.category.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Category {
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
        