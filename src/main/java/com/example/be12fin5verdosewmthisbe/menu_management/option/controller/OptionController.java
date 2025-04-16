package com.example.be12fin5verdosewmthisbe.menu_management.option.controller;

import com.example.be12fin5verdosewmthisbe.common.BaseResponse;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.service.CategoryService;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto.OptionDto;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto.OptionUpdateDto;
import com.example.be12fin5verdosewmthisbe.menu_management.option.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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


        Option option = Option.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
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

    @Operation(summary = "옵션 수정", description = "기존 메뉴 옵션의 정보 (이름, 가격, 카테고리) 및 재고별 사용 수량을 수정합니다. 요청에 없는 재고 ID의 사용 수량 정보는 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "옵션 수정 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Option updated successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "카테고리 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "4001", description = "옵션 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 옵션을 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PutMapping("/update")
    public BaseResponse<String> updateOption(
            @Parameter(description = "수정할 옵션 ID와 정보 및 재고별 사용 수량 리스트", required = true,
                    schema = @Schema(implementation = OptionUpdateDto.RequestDto.class))
            @RequestBody OptionUpdateDto.RequestDto updateDto) {
        optionService.updateOptionWithValues(updateDto.getOptionId(), updateDto);
        return BaseResponse.success("Option updated successfully");
    }


    @Operation(summary = "옵션 삭제", description = "주어진 ID의 옵션을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "옵션 삭제 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Option deleted successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "4001", description = "옵션 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 옵션을 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @DeleteMapping("/{optionId}")
    public BaseResponse<String> deleteOption(
            @Parameter(description = "삭제할 옵션 ID", required = true, example = "1")
            @PathVariable Long optionId) {
        optionService.deleteOption(optionId);
        return BaseResponse.success("Option deleted successfully");
    }

    @Operation(summary = "옵션 목록 조회 (페이지네이션)", description = "등록된 옵션 목록을 페이지별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "옵션 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @GetMapping("/list")
    public BaseResponse<Page<OptionDto.ResponseDto>> getOptionList(
            @Parameter(description = "페이지 정보 (기본: page=0, size=10, sort=name,asc)", schema = @Schema(implementation = Pageable.class))
            @PageableDefault(page = 0, size = 10, sort = "name", direction = org.springframework.data.domain.Sort.Direction.ASC)
            Pageable pageable) {
        Page<Option> optionPage = optionService.findAllOptions(pageable);
        Page<OptionDto.ResponseDto> dtoPage = optionPage.map(option -> new OptionDto.ResponseDto(
                option.getId(),
                option.getName()
        ));

        return BaseResponse.success(dtoPage);
    }


    @Operation(summary = "이름으로 옵션 검색 (페이지네이션)", description = "주어진 이름으로 메뉴 옵션을 검색하여 페이지별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "옵션 검색 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @GetMapping("/search/name")
    public BaseResponse<Page<Option>> searchOptionsByName(
            @Parameter(description = "검색할 옵션 이름 키워드", required = true, example = "사이즈")
            @RequestParam String keyword,
            @Parameter(description = "페이지 정보 (기본: page=0, size=10, sort=name,asc)", schema = @Schema(implementation = Pageable.class))
            @PageableDefault(page = 0, size = 10, sort = "name", direction = org.springframework.data.domain.Sort.Direction.ASC)
            Pageable pageable) {
        Page<Option> optionPage = optionService.searchOptionsByName(keyword, pageable);
        return BaseResponse.success(optionPage);
    }
}