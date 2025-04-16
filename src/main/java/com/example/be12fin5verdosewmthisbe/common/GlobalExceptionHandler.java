package com.example.be12fin5verdosewmthisbe.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BaseResponse<String>> handleException(Exception ex) {
        // 로그 남기기
        log.error(ex.getMessage());
        ErrorCode code = ErrorCode.ERROR_CODE; // 혹은 원하는 에러 코드

        return new ResponseEntity<>(
                BaseResponse.error(code),
                HttpStatus.valueOf(code.getStatus() / 100 * 100)  // 예: 500
        );
    }
}