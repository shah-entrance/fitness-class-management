package com.fitness.management.service;

import com.fitness.management.model.User;
import com.fitness.management.model.UserType;

public interface AuthService {
    User registerUser(String username, String password, UserType userType);
    User login(String username, String password);
    boolean isAdmin(User user);
}