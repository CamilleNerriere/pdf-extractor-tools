package com.noesis.pdf_extractor_tools.web.utils;

import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
