package com.example.demo.Configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc  // Automatically configures MockMvc with Spring Security
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser  // Simulate an authenticated user
    public void testSecurityFilterChain() throws Exception {

        // If CSRF is disabled, test a POST request (usually a vulnerable endpoint)
        mockMvc.perform(MockMvcRequestBuilders.post("/some-endpoint"))  // Replace with an actual endpoint
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
