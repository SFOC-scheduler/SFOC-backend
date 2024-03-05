package com.project.sfoc.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Error {

    EXPIRED_ACCESS_TOKEN(101, HttpStatus.BAD_REQUEST, "만료된 access token"),
    EXPIRED_REFRESH_TOKEN(102, HttpStatus.BAD_REQUEST, "만료된 refresh token"),
    INVALID_TOKEN(103, HttpStatus.BAD_REQUEST, "유효하지 않은 token");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
