package com.nbu.CSCB532.controller.courier;

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
class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "emp.courier", authorities = "EMPLOYEE")
    void myDeliveries_returnsDeliveriesView() throws Exception {
        mockMvc.perform(get("/courier"))
                .andExpect(status().isOk())
                .andExpect(view().name("courier/deliveries"));
    }

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void myDeliveries_asAdmin_returnsDeliveriesView() throws Exception {
        mockMvc.perform(get("/courier"))
                .andExpect(status().isOk())
                .andExpect(view().name("courier/deliveries"));
    }

    @Test
    void myDeliveries_withoutAuth_redirectsOrUnauthorized() throws Exception {
        mockMvc.perform(get("/courier"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 401 && status != 302) throw new AssertionError("Expected 401 or 302, got " + status);
                });
    }

    @Test
    @WithMockUser(username = "emp.courier", authorities = "EMPLOYEE")
    void markArrivedAtOffice_redirectsToCourier() throws Exception {
        mockMvc.perform(post("/courier/parcels/1/arrived-at-office").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courier"));
    }

    @Test
    @WithMockUser(username = "emp.courier", authorities = "EMPLOYEE")
    void markDelivered_redirectsToCourier() throws Exception {
        mockMvc.perform(post("/courier/parcels/1/delivered").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courier"));
    }

    @Test
    @WithMockUser(username = "emp.courier", authorities = "EMPLOYEE")
    void markDelivered_invalidId_redirectsWithError() throws Exception {
        mockMvc.perform(post("/courier/parcels/99999/delivered").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courier"))
                .andExpect(flash().attributeExists("error"));
    }
}
