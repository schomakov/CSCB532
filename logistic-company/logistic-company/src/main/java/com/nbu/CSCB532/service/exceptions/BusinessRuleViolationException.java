package com.nbu.CSCB532.service.exceptions;

/**
 * Generic 400 Bad Request for business rule violations.
 * Example: attempting an operation that contradicts domain invariants.
 */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}

