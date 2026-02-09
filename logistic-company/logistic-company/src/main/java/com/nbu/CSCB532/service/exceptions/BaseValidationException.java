package com.nbu.CSCB532.service.exceptions;

public class BaseValidationException extends RuntimeException {
    public BaseValidationException(String msg) {
        super(msg);
    }
}
