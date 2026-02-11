package com.nbu.CSCB532.web;

import com.nbu.CSCB532.service.exceptions.NotFoundException;
import com.nbu.CSCB532.service.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

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
    void handleConflicts_returnsErrorView() {
        String view = handler.handleConflicts(new UserAlreadyExistException("Exists"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void handleUnexpected_returnsErrorView() {
        String view = handler.handleUnexpected(new RuntimeException("Unexpected"), model);
        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("status")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
