package com.example.userservice.store.model;

import com.example.userservice.user.model.User;
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

    @Column(length=200, nullable = false)
    private String address;

    @Column(length=200, unique = true, nullable = false)
    private String phoneNumber;

    @Column(
            columnDefinition = """
        POINT
        GENERATED ALWAYS AS (
          ST_GeomFromText(
            CONCAT('POINT(', longitude, ' ', latitude, ')')
          )
        ) STORED
        """,
            nullable = true,      // Hibernate가 NOT NULL 자동 추가를 방지
            updatable = false,
            insertable = false
    )
    private byte[] location;


    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
}
        

