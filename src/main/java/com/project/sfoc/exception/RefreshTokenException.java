package com.project.sfoc.exception;

public class RefreshTokenException extends BusinessException {

    public RefreshTokenException(Error error) {
        super(error);
    }

    public RefreshTokenException(String message, Error error) {
        super(message, error);
    }

}
