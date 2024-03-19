package com.project.sfoc.exception;

public class IllegalDtoException extends BusinessException {

    public IllegalDtoException(Error error) {
        super(error);
    }

    public IllegalDtoException(String message, Error error) {
        super(message, error);
    }

}
