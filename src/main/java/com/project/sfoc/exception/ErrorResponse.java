package com.project.sfoc.exception;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        int code,
        String message
) {
    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse from(Error error) {
        return ErrorResponse.of(error.getCode(), error.getMessage());
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity (Error error) {
        return ResponseEntity.status(error.getHttpStatus())
                .body(ErrorResponse.from(error));
    }
}