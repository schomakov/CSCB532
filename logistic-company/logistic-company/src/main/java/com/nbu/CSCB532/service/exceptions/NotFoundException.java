package com.nbu.CSCB532.service.exceptions;

/**
 * Generic 404 Not Found exception for domain objects.
 * Throw this from the service layer when a requested entity is missing.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

