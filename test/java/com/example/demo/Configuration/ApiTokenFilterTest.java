package com.example.demo.Configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.demo.Controller.TestController;
import static org.mockito.Mockito.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootTest
public class ApiTokenFilterTest {

    private MockMvc mockMvc;

    @MockBean
    private ApiTokenFilter apiTokenFilter; // Mock the filter to check behavior

    @Value("${api.token}")
    private String apiToken;

    @BeforeEach
    void setUp() {
        // Setup MockMvc to include the TestController and the filter
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController()) // Test controller
                .addFilters(new ApiTokenFilter()) // Explicitly add the ApiTokenFilter
                .build();
    }

    @Test
    public void testInvalidApiToken() throws Exception {
        // Perform a GET request with an invalid Authorization header
        mockMvc.perform(MockMvcRequestBuilders.get("/test-endpoint")
                        .header("Authorization", "invalid-token"))  // Set invalid token in the header
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())  // Should return 401 Unauthorized
                .andExpect(MockMvcResultMatchers.content().string("401 Unauthorized: Invalid API Token")); // Optional: verify error message
    }

    @Test
    public void testNoApiToken() throws Exception {
        // Perform a GET request with no Authorization header
        mockMvc.perform(MockMvcRequestBuilders.get("/test-endpoint")
                        .header("Authorization", ""))  // No token in the header
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())  // Should return 401 Unauthorized
                .andExpect(MockMvcResultMatchers.content().string("401 Unauthorized: Invalid API Token")); // Optional: verify error message
    }
}
