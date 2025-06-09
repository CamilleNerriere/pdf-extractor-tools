package com.noesis.pdf_extractor_tools.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.dto.user.UserInfosDto;
import com.noesis.pdf_extractor_tools.dto.user.UserUpdateDto;
import com.noesis.pdf_extractor_tools.exception.ConflictException;
import com.noesis.pdf_extractor_tools.exception.DatabaseException;
import com.noesis.pdf_extractor_tools.exception.InvalidCredentialsException;
import com.noesis.pdf_extractor_tools.exception.NotFoundException;
import com.noesis.pdf_extractor_tools.model.User;
import com.noesis.pdf_extractor_tools.repository.UserRepository;

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

    public UserInfosDto getUser(final String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found"));

        return new UserInfosDto(user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail());
    }

    public UserUpdateDto updateUser(UserUpdateDto userDto, String email) {
        User userToUpdate = userRepository.findByEmail(email)
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

        try {
            userRepository.save(userToUpdate);

        } catch (Exception e) {
            throw new DatabaseException("Failed to update user", e);
        }

        return new UserUpdateDto(userToUpdate.getFirstname(), userToUpdate.getLastname(), userToUpdate.getUsername());

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

        System.out.println("DEBUG - Authentification réussie pour " + user.getEmail());
        System.out.println("DEBUG - User trouvé : " + user);

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
