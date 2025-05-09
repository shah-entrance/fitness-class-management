package com.fitness.util;

import com.fitness.model.FitnessClass;
import com.fitness.model.User;

import java.time.LocalDateTime;

/**
 * Utility class for validation operations
 */
public class ValidationUtils {
    
    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validate username
     * 
     * @param username The username to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() >= 3;
    }
    
    /**
     * Validate password
     * 
     * @param password The password to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= Constants.PASSWORD_MIN_LENGTH;
    }
    
    /**
     * Validate booking parameters
     * 
     * @param user The user making the booking
     * @param fitnessClass The class being booked
     * @return True if valid, false otherwise
     */
    public static boolean isValidBooking(User user, FitnessClass fitnessClass) {
        return user != null && 
               fitnessClass != null && 
               !fitnessClass.isCancelled() && 
               fitnessClass.getStartTime().isAfter(LocalDateTime.now());
    }
    
    /**
     * Validate class capacity
     * 
     * @param capacity The capacity to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0;
    }
    
    /**
     * Validate class duration
     * 
     * @param durationMinutes The duration to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidDuration(int durationMinutes) {
        return durationMinutes >= Constants.MIN_BOOKING_TIME_MINUTES;
    }
}