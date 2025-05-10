package com.fitness.management.service.impl;

import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.repository.RepositoryFactory;
import com.fitness.management.repository.UserRepository;
import com.fitness.management.service.AuthService;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    
    public AuthServiceImpl() {
        this.userRepository = RepositoryFactory.createUserRepository();
    }
    
    // For testing - allows injecting a mock repository
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User registerUser(String username, String password, UserType userType) {
        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        User user = new User(username, password, userType);
        return userRepository.save(user);
    }
    
    @Override
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt.get();
        }
        
        throw new IllegalArgumentException("Invalid username or password");
    }
    
    @Override
    public boolean isAdmin(User user) {
        return user.getUserType() == UserType.ADMIN;
    }
}