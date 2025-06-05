package com.noesis.pdf_extractor_tools.web.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.noesis.pdf_extractor_tools.dto.auth.AuthCheckResult;
import com.noesis.pdf_extractor_tools.exception.ErrorResponse;
import com.noesis.pdf_extractor_tools.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 
     * @param request
     * @param jwtService
     * @return AuthCheckResult(boolean valid, String token, ResponseEntity response)
     */
    public static AuthCheckResult checkJwtAuth(HttpServletRequest request, JwtService jwtService) {
        String token = JwtUtils.extractToken(request);
        if (token == null) {
            return new AuthCheckResult(false, null,
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized.")));
        }

        if (!jwtService.validateToken(token)) {
            return new AuthCheckResult(false, token,
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid Token")));
        }

        return new AuthCheckResult(true, token, null);
    }
}
