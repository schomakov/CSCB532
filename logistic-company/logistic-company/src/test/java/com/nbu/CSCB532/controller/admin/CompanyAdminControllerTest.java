package com.nbu.CSCB532.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CompanyAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void list_returnsCompaniesView() throws Exception {
        mockMvc.perform(get("/admin/companies"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/companies"));
    }

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void save_redirectsToList() throws Exception {
        mockMvc.perform(post("/admin/companies")
                        .with(csrf())
                        .param("name", "New Co")
                        .param("registrationNumber", "123456789"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/companies"));
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void list_asClient_forbidden() throws Exception {
        mockMvc.perform(get("/admin/companies"))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_withoutAuth_unauthorizedOrRedirect() throws Exception {
        mockMvc.perform(get("/admin/companies"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 302) return;
                    throw new AssertionError("Expected 401 or 302, got " + status);
                });
    }
}
