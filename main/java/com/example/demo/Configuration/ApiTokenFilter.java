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

@Slf4j
@Component
public class ApiTokenFilter extends OncePerRequestFilter {

    @Value("${api.token}")
    private String apiToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        log.info("Authorization Header: {}", token);
        log.info("Expected API Token: {}", apiToken);
        if (token == null || !token.equals(apiToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("401 Unauthorized: Invalid API Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

}