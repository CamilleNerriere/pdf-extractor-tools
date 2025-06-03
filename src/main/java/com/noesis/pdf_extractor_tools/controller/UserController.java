package com.noesis.pdf_extractor_tools.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final PasswordEncoder passwordEncoder;

    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    

    public void register(String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Mot de passe haché : " + hashedPassword);
    }

    public boolean verify(String rawPassword, String hashed) {
        return passwordEncoder.matches(rawPassword, hashed);
    }

}
