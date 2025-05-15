package com.fitness.management.util;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;

import java.time.LocalDateTime;

public class ValidationUtils {
    
    private ValidationUtils() {
    }
    
    public static void validateUsername(String username) {
        boolean valid = username != null && !username.trim().isEmpty();
        if (!valid) {
            throw new IllegalArgumentException("Invalid username");
        }
    }

    public static void validatePassword(String password) {
        boolean valid = password != null && password.length() >= Constants.PASSWORD_MIN_LENGTH;
        if (!valid) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
    
    public static void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
    }
}