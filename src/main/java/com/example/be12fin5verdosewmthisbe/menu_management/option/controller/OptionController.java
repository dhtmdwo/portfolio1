package com.example.be12fin5verdosewmthisbe.menu_management.option.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.service.CategoryService;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto.OptionDto;
import com.example.be12fin5verdosewmthisbe.menu_management.option.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Option API", description = "메뉴 옵션 관련 API")
@RestController
@RequestMapping("/api/option")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;
    private final CategoryService categoryService;

    @Operation(summary = "옵션 등록", description = "새로운 메뉴 옵션을 등록하고, 각 재고별 사용 수량을 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "옵션 등록 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Option registered successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "카테고리 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PostMapping("/register")
    public BaseResponse<String> registerOption(
            @Parameter(description = "등록할 옵션 정보 및 재고별 사용 수량 리스트", required = true,
                    schema = @Schema(implementation = OptionDto.RequestDto.class))
            @RequestBody OptionDto.RequestDto requestDto) {

        Category category = categoryService.findById(requestDto.getCategoryId());

        Option option = Option.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .category(category)
                .build();

        Option registeredOption = optionService.register(option);

        List<OptionValue> optionValues = requestDto.getInventoryQuantities().stream()
                .map(iq -> OptionValue.builder()
                        .option(registeredOption)
                        .inventoryId(iq.getInventoryId())
                        .quantity(iq.getQuantity())
                        .build())
                .collect(Collectors.toList());

        optionService.registerOptionValues(optionValues);

        return BaseResponse.success("Option registered successfully");
    }
}