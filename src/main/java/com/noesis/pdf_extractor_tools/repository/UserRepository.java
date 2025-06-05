package com.noesis.pdf_extractor_tools.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.noesis.pdf_extractor_tools.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
