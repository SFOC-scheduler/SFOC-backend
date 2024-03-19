package com.project.sfoc.exception;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(Error error) {
        super(error);
    }

    public EntityNotFoundException(String message, Error error) {
        super(message, error);
    }

}
