package com.project.sfoc.exception;

public class AccessTokenException extends BusinessException {

    public AccessTokenException(Error error) {
        super(error);
    }

    public AccessTokenException(String message, Error error) {
        super(message, error);
    }

}
