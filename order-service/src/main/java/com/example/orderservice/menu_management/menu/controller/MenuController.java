package com.example.orderservice.menu_management.menu.controller;

import com.example.common.BaseResponse;
import com.example.orderservice.menu_management.menu.model.Menu;
import com.example.orderservice.menu_management.menu.model.dto.*;
import com.example.orderservice.menu_management.menu.service.MenuService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Menu API", description = "메뉴 관련 API")
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록하고, 사용되는 재료 및 카테고리 정보를 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 등록 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Menu registered successfully\", \"data\": {\"id\": 1}}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "카테고리 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PostMapping("/register")
    public BaseResponse<String> createMenu(@RequestBody @Valid MenuRegisterDto.MenuCreateRequestDto requestDto, @RequestHeader("X-Store-Id") Long storeId) {
        menuService.registerMenu(requestDto, storeId);
        return BaseResponse.success("Menu registered successfully");
    }

    @Operation(summary = "메뉴 수정", description = "기존 메뉴의 이름, 가격, 카테고리 및 레시피 정보를 수정합니다. 요청에 없는 레시피는 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 수정 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Menu updated successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"잘못된 요청 형식입니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "3001", description = "메뉴 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 메뉴를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "5002", description = "카테고리 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 카테고리를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @PutMapping("/update")
    public BaseResponse<String> updateMenu(
            @Parameter(description = "수정할 메뉴 ID와 정보", required = true,
                    schema = @Schema(implementation = MenuUpdateDto.RequestDto.class))
            @RequestBody @Valid MenuUpdateDto.RequestDto updateDto, @RequestHeader("X-Store-Id") Long storeId) {
        menuService.updateMenu(updateDto.getMenuId(), updateDto, storeId);
        return BaseResponse.success("Menu updated successfully");
    }

    @GetMapping("/getPOSList")
    public BaseResponse<List<MenuDto.POSMenuListResponseDto>> getAllMenus(@RequestHeader("X-Store-Id") Long storeId) {
        List<MenuDto.POSMenuListResponseDto> menus = menuService.findAllPOSMenus(storeId);
        return BaseResponse.success(menus);
    }

    @Operation(summary = "특정 ID의 메뉴 조회", description = "주어진 ID에 해당하는 메뉴 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 조회 성공",
                    content = @Content(schema = @Schema(implementation = Menu.class))),
            @ApiResponse(responseCode = "3001", description = "메뉴 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 메뉴를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })

    @GetMapping("/{menuId}")
    public BaseResponse<MenuDto.MenuDetailResponseDto> getMenuById(
            @Parameter(description = "조회할 메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        return BaseResponse.success(menuService.getMenuDetail(menuId));
    }

    @Operation(summary = "전체 메뉴 목록 조회 (페이지네이션)", description = "등록된 전체 메뉴 목록을 페이지별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @GetMapping("/getList")
    public BaseResponse<Page<MenuDto.MenuListResponseDto>> getAllMenus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestHeader("X-Store-Id") Long storeId
    ) {
        Page<MenuDto.MenuListResponseDto> menuPage = menuService.findAllMenus(PageRequest.of(page,size),keyword,storeId);
        return BaseResponse.success(menuPage);
    }



    @Operation(summary = "특정 ID의 메뉴 삭제", description = "주어진 ID에 해당하는 메뉴 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": true, \"message\": \"Menu deleted successfully\", \"data\": null}"))),
            @ApiResponse(responseCode = "3001", description = "메뉴 정보 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"해당 ID의 메뉴를 찾을 수 없습니다.\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class, defaultValue = "{\"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null}")))
    })
    @DeleteMapping
    public BaseResponse<String> deleteMenus(
            @Parameter(description = "삭제할 메뉴 ID 리스트", required = true)
            @RequestBody List<Long> menuIds) {
        menuService.deleteMenus(menuIds);
        return BaseResponse.success("Menus deleted successfully");
    }

    @GetMapping("/menuList")
    public BaseResponse<List<MenuInfoDto.MenuResponse>> getmenuList(@RequestHeader("X-Store-Id") Long storeId) {

        List<MenuInfoDto.MenuResponse> menuList = menuService.getmenuList(storeId);
        return BaseResponse.success(menuList);
    }

    @PostMapping("/menuSale")
    public BaseResponse<List<MenuSaleDto.Response>> getSaleList(@RequestHeader("X-Store-Id") Long storeId, @RequestBody @Valid MenuSaleDto.DateRequest dto) {

        List<MenuSaleDto.Response> SaleList = menuService.getSaleList(storeId, dto);
        return BaseResponse.success(SaleList);
    }

}