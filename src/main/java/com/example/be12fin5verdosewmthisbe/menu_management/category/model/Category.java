package com.example.be12fin5verdosewmthisbe.menu_management.category.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Schema(description = "메뉴 카테고리 정보")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "카테고리 ID (자동 생성)", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "카테고리 이름", example = "메인 요리")
    private String name;


}