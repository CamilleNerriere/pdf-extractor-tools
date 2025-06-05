package com.noesis.pdf_extractor_tools;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.noesis.pdf_extractor_tools.service.JwtService;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldValidateValidToken(){
        UserDetails userDetails = User.builder().username("demo.user@example.com").password("superSecretPassword").roles("USER").build();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldRejectInvalidToken(){
        String token = "invalid.token";
        assertFalse(jwtService.validateToken(token));
    }
}
