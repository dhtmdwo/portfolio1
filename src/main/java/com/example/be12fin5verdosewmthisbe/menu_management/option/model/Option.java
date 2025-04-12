package com.example.be12fin5verdosewmthisbe.menu_management.option.model;


import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import jakarta.persistence.*;

@Entity
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

}