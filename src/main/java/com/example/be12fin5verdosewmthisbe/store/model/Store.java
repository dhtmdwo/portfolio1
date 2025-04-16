package com.example.be12fin5verdosewmthisbe.store.model;

import com.example.be12fin5verdosewmthisbe.user.model.User;
import jakarta.persistence.*;
import lombok.*;

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

}
        