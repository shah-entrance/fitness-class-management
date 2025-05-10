package com.fitness.management.util;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;

import java.time.LocalDateTime;

public class ValidationUtils {
    
    private ValidationUtils() {
    }
    
    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() >= 3;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= Constants.PASSWORD_MIN_LENGTH;
    }
    
    public static boolean isValidBooking(User user, FitnessClass fitnessClass) {
        return user != null && 
               fitnessClass != null && 
               !fitnessClass.isCancelled() && 
               fitnessClass.getStartTime().isAfter(LocalDateTime.now());
    }
    
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0;
    }
    
    public static boolean isValidDuration(int durationMinutes) {
        return durationMinutes >= Constants.MIN_BOOKING_TIME_MINUTES;
    }
}