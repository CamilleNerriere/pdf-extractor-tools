package com.noesis.pdf_extractor_tools.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.dto.user.UserUpdateDto;
import com.noesis.pdf_extractor_tools.exception.ConflictException;
import com.noesis.pdf_extractor_tools.exception.InvalidCredentialsException;
import com.noesis.pdf_extractor_tools.exception.NotFoundException;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.repository.UserRepository;

import lombok.Data;

@Data
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    public User updateUser(UserUpdateDto userDto, Long id) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        if (userDto.getFirstname() != null) {
            userToUpdate.setFirstname(userDto.getFirstname());
        }
        if (userDto.getLastname() != null) {
            userToUpdate.setLastname(userDto.getLastname());
        }

        if (userDto.getUsername() != null) {

            String newUsername = userDto.getUsername();
            String currentUsername = userToUpdate.getUsername();

            if (!newUsername.equals(currentUsername)) {
                if (usernameExists(newUsername)) {
                    throw new ConflictException("Username already exists");
                }
                userToUpdate.setUsername(newUsername);
            }
        }

        return userRepository.save(userToUpdate);
    }

    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }

    public UserDetails validateCredentials(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of() // ou les rôles
        );

    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
