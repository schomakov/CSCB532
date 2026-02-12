package com.nbu.CSCB532.controller.employee;

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
class EmployeeParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void listAndForm_returnsParcelsView() throws Exception {
        mockMvc.perform(get("/employee/parcels"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/parcels"));
    }

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void listAndForm_asAdmin_returnsParcelsView() throws Exception {
        mockMvc.perform(get("/employee/parcels"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/parcels"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void markReceived_redirectsToList() throws Exception {
        mockMvc.perform(post("/employee/parcels/1/receive").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/parcels"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void markArrivedAtOffice_redirectsToList() throws Exception {
        mockMvc.perform(post("/employee/parcels/1/arrived-at-office").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/parcels"));
    }

    @Test
    void employeeParcels_withoutAuth_unauthorized() throws Exception {
        mockMvc.perform(get("/employee/parcels"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 401 && status != 302) throw new AssertionError("Expected 401 or 302, got " + status);
                });
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void track_withoutCode_returnsTrackView() throws Exception {
        mockMvc.perform(get("/employee/track"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/track"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void track_withCode_returnsTrackView() throws Exception {
        mockMvc.perform(get("/employee/track").param("code", "TRK-REG001"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/track"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void createParcel_redirectsToList() throws Exception {
        mockMvc.perform(post("/employee/parcels")
                        .with(csrf())
                        .param("sender.id", "23")
                        .param("recipientName", "Test Recipient")
                        .param("recipientPhone", "+359888111222")
                        .param("deliveryType", "TO_OFFICE")
                        .param("fromOffice.id", "1")
                        .param("toOffice.id", "2")
                        .param("weightKg", "1.5")
                        .param("paymentType", "SENDER_PAYS")
                        .param("status", "REGISTERED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/parcels"));
    }
}
