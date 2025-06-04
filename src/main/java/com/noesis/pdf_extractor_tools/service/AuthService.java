package com.noesis.pdf_extractor_tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.dto.user.UserLoginDto;
import com.noesis.pdf_extractor_tools.dto.user.UserRegisterDto;
import com.noesis.pdf_extractor_tools.exception.ConflictException;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegisterDto userDto) {
        if (usernameExists(userDto.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        User user = new User();
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);

    }

    public String login(UserLoginDto userLogin){
        UserDetails user = userService.validateCredentials(userLogin.getEmail(), userLogin.getPassword());
        return jwtService.generateToken(user);
    }

        public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
