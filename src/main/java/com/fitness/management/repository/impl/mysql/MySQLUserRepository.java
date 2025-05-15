package com.fitness.management.repository.impl.mysql;

import com.fitness.management.model.User;
import com.fitness.management.repository.UserRepository;

import java.util.Optional;

public class MySQLUserRepository implements UserRepository {
    
    @Override
    public User save(User user) {
        throw new MySQLNotImplementedException("Saving user to MySQL is not implemented yet");
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        throw new MySQLNotImplementedException("Finding user by username in MySQL is not implemented yet");
    }
}