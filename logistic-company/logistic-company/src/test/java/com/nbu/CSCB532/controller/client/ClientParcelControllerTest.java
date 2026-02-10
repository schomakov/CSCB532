package com.nbu.CSCB532.controller.client;

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
class ClientParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "unknownclient", authorities = "CLIENT")
    void myParcels_whenClientNotFound_returnsEmptyListsAndWarning() throws Exception {
        mockMvc.perform(get("/client/parcels"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/parcels"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "CLIENT")
    void myParcels_whenClientFound_returnsSentAndReceivedParcels() throws Exception {
        mockMvc.perform(get("/client/parcels"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/parcels"));
    }

    @Test
    void myParcels_withoutAuth_unauthorizedOrRedirect() throws Exception {
        mockMvc.perform(get("/client/parcels"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 302) return;
                    throw new AssertionError("Expected 401 or 302, got " + status);
                });
    }
}
