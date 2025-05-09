package com.fitness.util;

/**
 * Constants used throughout the fitness class management system
 */
public class Constants {
    // Booking related constants
    public static final int DEFAULT_CAPACITY = 10;
    public static final int MIN_BOOKING_TIME_MINUTES = 30;
    public static final int DEFAULT_CLASS_DURATION_MINUTES = 60;
    
    // User related constants
    public static final int PASSWORD_MIN_LENGTH = 6;
    
    // Application messages
    public static final String MSG_BOOKING_SUCCESS = "Booking successful";
    public static final String MSG_BOOKING_CANCELLED = "Booking cancelled successfully";
    public static final String MSG_WAITLIST_ADDED = "Added to waitlist successfully";
    
    // Error messages
    public static final String ERR_CLASS_FULL = "Class is at full capacity";
    public static final String ERR_CLASS_CANCELLED = "This class has been cancelled";
    public static final String ERR_TIME_CONFLICT = "Time conflict with another booking";
    public static final String ERR_BOOKING_LIMIT = "Booking limit reached for your membership tier";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}