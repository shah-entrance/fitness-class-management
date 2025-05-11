package com.fitness.management.repository;

import com.fitness.management.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
}