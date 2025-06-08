package com.noesis.pdf_extractor_tools.middleware;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.noesis.pdf_extractor_tools.service.JwtService;
import com.noesis.pdf_extractor_tools.web.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    public JwtAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws IOException {

        System.out.println("=== JwtAuthInterceptor ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Origin: " + request.getHeader("Origin"));

        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = JwtUtils.extractToken(request);
        if (token == null || !jwtService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or missing token\"}");
            return false;
        }

        return true;
    }
}
