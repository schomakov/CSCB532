package com.nbu.CSCB532.service.exceptions;

public class UserAlreadyExistException extends BaseValidationException {
    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}
