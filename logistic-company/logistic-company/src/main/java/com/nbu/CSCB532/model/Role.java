package com.nbu.CSCB532.model;

public enum Role {
    ADMINISTRATOR,
    EMPLOYEE, // Логистична компания: служител (офис/куриер)
    CLIENT,   // Логистична компания: клиент
    NONE // This role is set when the user is registered
}