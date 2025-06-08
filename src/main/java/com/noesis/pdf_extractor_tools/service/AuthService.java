package com.noesis.pdf_extractor_tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.dto.user.UserInfosDto;
import com.noesis.pdf_extractor_tools.dto.user.UserLoginDto;
import com.noesis.pdf_extractor_tools.dto.user.UserRegisterDto;
import com.noesis.pdf_extractor_tools.exception.ConflictException;
import com.noesis.pdf_extractor_tools.exception.DatabaseException;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.pdf_extractor_tools.repository.UserRepository;

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

    public UserInfosDto registerUser(UserRegisterDto userDto) {
        if (usernameExists(userDto.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        User user = new User();
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        String pwd = passwordEncoder.encode(userDto.getPassword());
        System.out.println(pwd);
        user.setPassword(pwd);

        try {
            User userCreated = userRepository.save(user);
            return new UserInfosDto(userCreated.getFirstname(), userCreated.getLastname(), userCreated.getUsername(),
                    userCreated.getEmail());
        } catch (Exception e) {
            throw new DatabaseException("Unable to create new user", e);
        }

    }

    public String login(UserLoginDto userLogin) {
        UserDetails user = userService.validateCredentials(userLogin.email(), userLogin.password());
        return jwtService.generateToken(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
