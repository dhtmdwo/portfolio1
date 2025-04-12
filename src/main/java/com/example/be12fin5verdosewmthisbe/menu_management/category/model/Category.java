package com.example.be12fin5verdosewmthisbe.menu_management.category.model;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 카테고리 정보")
// TODO: 가게 ID 추가해야함
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "카테고리 ID (자동 생성)", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "카테고리 이름", example = "메인 요리")
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

}