package com.nbu.CSCB532.service.exceptions;

/**
 * Specific business exception for invalid parcel state transitions.
 * Example: marking a DELIVERED parcel as IN_TRANSIT again.
 */
public class ParcelStateException extends BusinessRuleViolationException {
    public ParcelStateException(String message) {
        super(message);
    }
}

