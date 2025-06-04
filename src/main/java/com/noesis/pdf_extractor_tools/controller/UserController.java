package com.noesis.pdf_extractor_tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

import com.noesis.pdf_extractor_tools.dto.user.UserRegisterDto;
import com.noesis.pdf_extractor_tools.exception.ErrorResponse;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.pdf_extractor_tools.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final boolean IS_DEMO = true;

    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(UserRegisterDto user) {
        
        User newUser = userService.saveUser(user);

        if(newUser != null){
            return ResponseEntity.ok(newUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Failed to register new user."));
        }
    }

    public boolean verify(String rawPassword, String hashed) {
        return passwordEncoder.matches(rawPassword, hashed);
    }

}
