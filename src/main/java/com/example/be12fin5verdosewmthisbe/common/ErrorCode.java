package com.example.be12fin5verdosewmthisbe.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERROR_CODE(100,"왜 실패했는지 설명 어쩌구저쩌구"),
    INVAILD_REQUEST(5001,"카테고리 타입에 맞지 않는 잘못된 요청입니다."),
    NO_EXIST_NAME(5002,"해당 이름을 가진 카테고리가 존재하지 않습니다."),
    EMPTY(5003,"카테고리 목록이 비어있습니다.")
    ;





    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
