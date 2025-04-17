package com.example.be12fin5verdosewmthisbe.store.model;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=200)
    private String name;

    //@Column(length=200, unique = true, nullable = false)
    private String address;

    //@Column(length=200, unique = true, nullable = false)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "store")
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Category> categoryList = new ArrayList<>();



}
        