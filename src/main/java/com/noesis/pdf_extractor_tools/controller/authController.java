package com.noesis.pdf_extractor_tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noesis.pdf_extractor_tools.dto.auth.TokenResponse;
import com.noesis.pdf_extractor_tools.dto.user.UserLoginDto;
import com.noesis.pdf_extractor_tools.dto.user.UserRegisterDto;
import com.noesis.pdf_extractor_tools.exception.ErrorResponse;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.pdf_extractor_tools.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class authController {

    @Autowired    AuthService authService;

    private final boolean IS_DEMO = true;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto user) {
        if(IS_DEMO){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("This is a demo. Registrations are desabled"));
        }

        User newUser = authService.registerUser(user);

        if (newUser != null) {
            return ResponseEntity.ok(newUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to register new user."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto user) {
        String token = authService.login(user);
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid Credentials"));
        }

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(new TokenResponse(token));

    }

    /**
     * TODO: Implement logout endpoint for production.
     * In production, use a token blacklist
     * to store and check revoked tokens.
     */
}
