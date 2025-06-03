package com.noesis.pdf_extractor_tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.dto.user.UserRegisterDto;
import com.noesis.pdf_extractor_tools.dto.user.UserUpdateDto;
import com.noesis.pdf_extractor_tools.exception.ConflictException;
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

    public User saveUser(UserRegisterDto userDto) {
        if (usernameExists(userDto.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        User user = new User();
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setUsername(userDto.getUsername());
        user.setMail(userDto.getMail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);

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

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
