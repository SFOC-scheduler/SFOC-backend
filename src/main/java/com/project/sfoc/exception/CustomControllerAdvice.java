package com.project.sfoc.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class CustomControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> RefreshTokenException(BusinessException e) {
        log.error(e.error.getMessage());
        return ErrorResponse.toResponseEntity(e.error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> DtoValidException(MethodArgumentNotValidException e) {

        e.getBindingResult()
                .getFieldErrors().forEach(x -> log.error(x.getDefaultMessage()));

        return ErrorResponse.toResponseEntity(Error.INVALID_DTO);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> EnumValidException(HttpMessageNotReadableException e) {
        log.error("적절하지 않은 형식입니다.");
        return ErrorResponse.toResponseEntity(Error.INVALID_DTO);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> missingRequestCookieException(MissingRequestCookieException e) {
        log.error("refresh token이 전달되지 않았습니다.");
        return ErrorResponse.toResponseEntity(Error.INVALID_TOKEN);
    }

}
