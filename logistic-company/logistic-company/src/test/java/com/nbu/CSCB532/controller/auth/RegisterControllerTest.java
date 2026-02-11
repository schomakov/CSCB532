package com.nbu.CSCB532.controller.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void showRegisterForm_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void processRegister_passwordsMismatch_returnsRegisterWithError() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "pass123")
                        .param("confirmPassword", "different")
                        .param("email", "new@test.com")
                        .param("firstName", "New")
                        .param("lastName", "User"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void processRegister_success_redirectsToLogin() throws Exception {
        String unique = "reg" + System.currentTimeMillis();
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", unique)
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", unique + "@test.com")
                        .param("firstName", "Test")
                        .param("lastName", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
