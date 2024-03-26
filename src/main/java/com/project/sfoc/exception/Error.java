package com.project.sfoc.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public enum Error {

    UNKNOWN_ERROR(100, HttpStatus.BAD_REQUEST, "서버 관리자에게 문의"),
    EXPIRED_ACCESS_TOKEN(101, HttpStatus.BAD_REQUEST, "만료된 access token"),
    EXPIRED_REFRESH_TOKEN(102, HttpStatus.BAD_REQUEST, "만료된 refresh token"),
    INVALID_TOKEN(103, HttpStatus.BAD_REQUEST, "유효하지 않은 token"),
    INVALID_DTO(104, HttpStatus.BAD_REQUEST, "잘못된 dto 형식 입니다."),
    DENIED_ACCESS(105, HttpStatus.BAD_REQUEST, "접근이 거부되었습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;


}
