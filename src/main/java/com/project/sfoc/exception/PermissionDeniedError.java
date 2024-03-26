package com.project.sfoc.exception;

public class PermissionDeniedError extends BusinessException{
    public PermissionDeniedError(Error error) {
        super(error);
    }

    public PermissionDeniedError(String message, Error error) {
        super(message, error);
    }
}
