package com.fitness.service;

import com.fitness.model.User;
import com.fitness.model.UserType;

public interface AuthService {
    User registerUser(String username, String password, UserType userType);
    User login(String username, String password);
    boolean isAdmin(User user);
}