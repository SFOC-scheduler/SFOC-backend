package com.project.sfoc.exception;

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
}