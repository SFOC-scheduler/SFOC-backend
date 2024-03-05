package com.project.sfoc.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> RefreshTokenException(BusinessException e) {
        log.error(e.error.getMessage());
        return new ResponseEntity<>(ErrorResponse.from(e.error), e.error.getHttpStatus());
    }
}
