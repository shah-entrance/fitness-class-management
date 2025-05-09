package com.fitness.repository.impl.mysql;

import com.fitness.model.User;
import com.fitness.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class MySQLUserRepository implements UserRepository {
    
    @Override
    public User save(User user) {
        throw new MySQLNotImplementedException("Saving user to MySQL is not implemented yet");
    }
    
    @Override
    public Optional<User> findById(String id) {
        throw new MySQLNotImplementedException("Finding user by ID in MySQL is not implemented yet");
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        throw new MySQLNotImplementedException("Finding user by username in MySQL is not implemented yet");
    }
    
    @Override
    public List<User> findAll() {
        throw new MySQLNotImplementedException("Finding all users in MySQL is not implemented yet");
    }
    
    @Override
    public void delete(String id) {
        throw new MySQLNotImplementedException("Deleting user in MySQL is not implemented yet");
    }
}