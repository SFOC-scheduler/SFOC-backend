package com.project.sfoc.exception;

public class BusinessException extends RuntimeException {

    Error error;

    public BusinessException(Error error) {
        this.error = error;
    }

    public BusinessException(String message, Error error) {
        super(message);
        this.error = error;
    }
}
