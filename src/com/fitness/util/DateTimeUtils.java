package com.fitness.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtils {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private DateTimeUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Format a LocalDateTime using the default format pattern
     * 
     * @param dateTime The LocalDateTime to format
     * @return The formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    /**
     * Parse a date time string to LocalDateTime using the default format pattern
     * 
     * @param dateTimeStr The date time string to parse
     * @return The parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }
    
    /**
     * Check if two time periods overlap
     * 
     * @param start1 Start of first period
     * @param end1 End of first period
     * @param start2 Start of second period
     * @param end2 End of second period
     * @return True if periods overlap, false otherwise
     */
    public static boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1,
                                        LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}