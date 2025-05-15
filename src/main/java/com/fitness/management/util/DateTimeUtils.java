package com.fitness.management.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class DateTimeUtils {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private DateTimeUtils() {
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    public static LocalDateTime getTodayAt(int hour, int minute) {
        return LocalDate.now().atTime(hour, minute);
    }
    
    public static LocalDateTime getRelativeDayAt(int daysFromToday, int hour, int minute) {
        return LocalDate.now().plusDays(daysFromToday).atTime(hour, minute);
    }
}