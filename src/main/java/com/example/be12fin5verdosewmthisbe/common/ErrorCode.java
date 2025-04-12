package com.example.be12fin5verdosewmthisbe.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERROR_CODE(100,"왜 실패했는지 설명 어쩌구저쩌구"),
    
    // 카테고리 관련 에러코드
    INVAILD_REQUEST(5001,"카테고리 타입에 맞지 않는 잘못된 요청입니다."),
    CATEGORY_NOT_FOUND(5002,"해당 카테고리를 찾을 수 없습니다."),
    EMPTY(5003,"카테고리 목록이 비어있습니다."),
    CATEGORY_ALREADY_EXISTS(5004,"이미 존재하는 카테고리입니다."),
    
    // 옵션 관련 에러코드
    OPTION_NOT_FOUND(4001,"옵션을 찾을 수 없습니다." ),

    // 메뉴 관련 에러코드
    MENU_NOT_FOUND(3001, "메뉴를 찾을 수 없습니다." ),
    RECIPE_DUPLICATED_INVENTORY(3002, "중복된 재료를 추가할 수 없습니다."),
    RECIPE_QUANTITY_INVALID(3003, "재료의 수량은 0보다 같거나 작을 수 없습니다." );





    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
