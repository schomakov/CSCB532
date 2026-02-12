package com.nbu.CSCB532.web;

import com.nbu.CSCB532.service.exceptions.BusinessRuleViolationException;
import com.nbu.CSCB532.service.exceptions.NotFoundException;
import com.nbu.CSCB532.service.exceptions.ParcelStateException;
import com.nbu.CSCB532.service.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private Model model;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        model = new ConcurrentModel();
    }

    @Test
    void handleNotFound_returnsErrorViewAndSetsModel() {
        String view = handler.handleNotFound(new NotFoundException("Not found"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(model.getAttribute("title")).isEqualTo("Не е намерено");
    }

    @Test
    void handleBusiness_returnsErrorView() {
        String view = handler.handleBusiness(new IllegalArgumentException("Bad"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleBusiness_businessRuleViolation_setsTitle() {
        String view = handler.handleBusiness(new BusinessRuleViolationException("Rule"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("title")).isEqualTo("Невалидна операция");
    }

    @Test
    void handleBusiness_parcelState_setsTitle() {
        String view = handler.handleBusiness(new ParcelStateException("State"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleAccessDenied_returnsErrorView() {
        String view = handler.handleAccessDenied(new AccessDeniedException("Denied"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(model.getAttribute("title")).isEqualTo("Нямате права");
    }

    @Test
    void handleConflicts_returnsErrorView() {
        String view = handler.handleConflicts(new UserAlreadyExistException("Exists"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void handleValidation_returnsErrorViewAndErrorsList() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("parcel", "weightKg", "must be positive")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        String view = handler.handleValidation(ex, model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(model.getAttribute("title")).isEqualTo("Невалидни данни");
        assertThat(model.getAttribute("errors")).asList().isNotEmpty();
    }

    @Test
    void handleUnexpected_returnsErrorView() {
        String view = handler.handleUnexpected(new RuntimeException("Unexpected"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
