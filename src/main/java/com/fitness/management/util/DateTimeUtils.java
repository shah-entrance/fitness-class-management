package com.fitness.management.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;

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
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }

    public static boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1,
                                        LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    public static LocalDateTime getTodayAt(int hour, int minute) {
        return LocalDate.now().atTime(hour, minute);
    }
    
    public static LocalDateTime getRelativeDayAt(int daysFromToday, int hour, int minute) {
        return LocalDate.now().plusDays(daysFromToday).atTime(hour, minute);
    }
}