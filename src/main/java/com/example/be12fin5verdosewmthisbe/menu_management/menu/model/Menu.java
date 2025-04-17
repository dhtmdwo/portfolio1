package com.example.be12fin5verdosewmthisbe.menu_management.menu.model;

import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 정보")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "메뉴 ID (자동 생성)", example = "1")
    private Long id;

    @Schema(description = "메뉴 이름", example = "김치찌개")
    private String name;

    @Schema(description = "메뉴 가격", example = "8000")
    private int price;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @OnDelete(action = OnDeleteAction.SET_NULL) // 선택 사항: DB에서도 ON DELETE SET NULL 하고 싶다면
    private Category category;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "메뉴 레시피 목록")
    private List<Recipe> recipes = new ArrayList<>();
}