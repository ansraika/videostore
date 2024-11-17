package com.example.demo.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A custom filter for verifying the API token passed in the Authorization header
 * of HTTP requests. This filter checks if the token matches the expected token
 * configured in the application properties.
 * <p>
 * The filter is executed once per request and ensures that only requests with a
 * valid API token can access the protected resources. If the token is invalid or
 * missing, the filter responds with an HTTP 401 Unauthorized status.
 * </p>
 */
@Slf4j
@Component
public class ApiTokenFilter extends OncePerRequestFilter {

    /**
     * The expected API token configured in the application properties.
     */
    @Value("${api.token}")
    private String apiToken;

    /**
     * Filters incoming HTTP requests to check for the presence and validity of the API token.
     * <p>
     * If the request contains a valid API token in the "Authorization" header, the request
     * is allowed to proceed. If the token is missing or invalid, an HTTP 401 Unauthorized status
     * is returned with an error message.
     * </p>
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response to send back to the client
     * @param filterChain the filter chain to pass the request and response to the next filter
     *                    or the target resource
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the token from the Authorization header
        String token = request.getHeader("Authorization");

        // Log the incoming token and the expected API token
        log.info("Authorization Header: {}", token);
        log.info("Expected API Token: {}", apiToken);

        // Validate the token
        if (token == null || !token.equals(apiToken)) {
            // If the token is invalid, send an Unauthorized response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("401 Unauthorized: Invalid API Token");
            return;
        }

        // If the token is valid, proceed with the next filter or resource
        filterChain.doFilter(request, response);
    }
}
