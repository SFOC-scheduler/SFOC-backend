package com.project.sfoc.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomControllerAdvice {

    private final DiscordFeignClient discordFeignClient;

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
    public ResponseEntity<ErrorResponse> MissingRequestCookieException(MissingRequestCookieException e) {
        log.error("refresh token이 전달되지 않았습니다.");
        return ErrorResponse.toResponseEntity(Error.INVALID_TOKEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        discordFeignClient.sendMessage(createMessage(e, request));
        return ErrorResponse.toResponseEntity(Error.UNKNOWN_ERROR);
    }

    private DiscordMessage createMessage(Exception e, HttpServletRequest request) {
        log.info("에러 메시지 생성");
        return DiscordMessage.of(
                "# 에러 발생",
                List.of(DiscordMessage.Embed.of(
                        "에러 정보",
                        String.format(
                                """
                                ### 🕖 발생 시간
                                %s
                                
                                ### 🔗 요청 URI
                                %s
                                
                                ### 📨 Error message
                                %s
                                """, LocalDateTime.now(), request.getRequestURI(), e
                        )
                ))
        );
    }

}
