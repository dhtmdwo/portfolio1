package com.example.be12fin5verdosewmthisbe.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERROR_CODE(100,"왜 실패했는지 설명 어쩌구저쩌구"),
    INVAILD_REQUEST(5001,"카테고리 타입에 맞지 않는 잘못된 요청입니다."),
    CATEGORY_NOT_FOUND(5002,"해당 카테고리를 찾을 수 없습니다."),
    EMPTY(5003,"카테고리 목록이 비어있습니다."),
    CATEGORY_ALREADY_EXISTS(5004,"이미 존재하는 카테고리입니다."),
    ;





    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
