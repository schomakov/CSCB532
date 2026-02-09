package com.nbu.CSCB532.web;

import com.nbu.CSCB532.service.exceptions.BusinessRuleViolationException;
import com.nbu.CSCB532.service.exceptions.NotFoundException;
import com.nbu.CSCB532.service.exceptions.ParcelStateException;
import com.nbu.CSCB532.service.exceptions.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized MVC exception handler.
 * Maps known exceptions to user-friendly error pages with appropriate HTTP semantics.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 404 - not found (domain entity missing).
     */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("title", "Не е намерено");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * 400 - invalid request due to business rules or arguments.
     */
    @ExceptionHandler({BusinessRuleViolationException.class, ParcelStateException.class, IllegalArgumentException.class})
    public String handleBusiness(RuntimeException ex, Model model) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Невалидна операция");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * 400 - Bean Validation errors (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, Model model) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "Невалидни данни");
        model.addAttribute("message", "Моля, коригирайте отбелязаните полета.");
        model.addAttribute("errors", errors);
        model.addAttribute("error", String.join("; ", errors));
        return "error";
    }

    /**
     * 403 - access denied.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("title", "Нямате права");
        model.addAttribute("message", "Нямате права за тази операция.");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * 409 - conflicts such as unique constraint violations or duplicates.
     */
    @ExceptionHandler({DataIntegrityViolationException.class, UserAlreadyExistException.class})
    public String handleConflicts(Exception ex, Model model) {
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("title", "Конфликт на данни");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * 500 - any unhandled exception.
     */
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("title", "Вътрешна грешка");
        model.addAttribute("message", "Възникна неочаквана грешка. Моля, опитайте отново по-късно.");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    private String formatFieldError(FieldError fe) {
        return String.format("%s: %s", fe.getField(), fe.getDefaultMessage());
    }
}

