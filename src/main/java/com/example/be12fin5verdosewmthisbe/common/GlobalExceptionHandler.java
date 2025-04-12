package com.example.be12fin5verdosewmthisbe.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public BaseResponse<String> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return BaseResponse.error(errorCode);
    }
    @ExceptionHandler(Exception.class)
    public BaseResponse<String> handleException(Exception ex) {
        // 로그 남기기
        log.error(ex.getMessage());

        return BaseResponse.error(ErrorCode.ERROR_CODE);
    }
}