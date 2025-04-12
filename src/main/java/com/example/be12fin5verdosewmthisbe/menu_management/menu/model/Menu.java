package com.example.be12fin5verdosewmthisbe.menu_management.menu.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
