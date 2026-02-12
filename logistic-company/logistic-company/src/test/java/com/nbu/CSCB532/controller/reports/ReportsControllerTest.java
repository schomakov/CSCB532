package com.nbu.CSCB532.controller.reports;

import com.nbu.CSCB532.service.logistics.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void employees_report_returnsEmployeesView() throws Exception {
        mockMvc.perform(get("/reports/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/employees"));
    }

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void clients_report_returnsClientsView() throws Exception {
        mockMvc.perform(get("/reports/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/clients"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void parcelsAll_returnsParcelsView() throws Exception {
        mockMvc.perform(get("/reports/parcels/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/parcels"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void parcelsSelect_returnsSelectViewWithEmployeesAndClients() throws Exception {
        mockMvc.perform(get("/reports/parcels/select"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/parcels-select"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void parcelsByEmployee_returnsParcelsView() throws Exception {
        mockMvc.perform(get("/reports/parcels/by-employee").param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/parcels"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void parcelsNotReceived_returnsParcelsView() throws Exception {
        mockMvc.perform(get("/reports/parcels/not-received"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/parcels"));
    }

    @Test
    @WithMockUser(authorities = "ADMINISTRATOR")
    void revenue_withFromTo_returnsRevenueView() throws Exception {
        mockMvc.perform(get("/reports/revenue")
                        .param("from", "2026-01-01")
                        .param("to", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/revenue"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void revenue_asEmployee_forbidden() throws Exception {
        mockMvc.perform(get("/reports/revenue")
                        .param("from", "2026-01-01")
                        .param("to", "2026-12-31"))
                .andExpect(status().isForbidden());
    }

    @Test
    void reports_withoutAuth_unauthorizedOrRedirect() throws Exception {
        mockMvc.perform(get("/reports/employees"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 302) return;
                    throw new AssertionError("Expected 401 or 302, got " + status);
                });
    }
}
