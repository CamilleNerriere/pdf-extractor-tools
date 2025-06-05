package com.noesis.pdf_extractor_tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.noesis.pdf_extractor_tools.service.JwtService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldValidateValidToken() {
        UserDetails userDetails = User.builder().username("demo.user@example.com").password("superSecretPassword")
                .roles("USER").build();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        String token = "invalid.token";
        assertFalse(jwtService.validateToken(token));
    }

    @Test
    void extractCorrectlyEmail() {
        UserDetails userDetails = User.builder().username("demo.user@example.com").password("superSecretPassword")
                .roles("USER").build();
        String token = jwtService.generateToken(userDetails);
        String email = jwtService.extractEmail(token);
        assertEquals("demo.user@example.com", email);
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isUnauthorized());
    }

}
