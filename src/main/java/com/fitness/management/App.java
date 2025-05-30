package com.fitness.management;

import com.fitness.management.service.AdminService;
import com.fitness.management.service.AuthService;
import com.fitness.management.service.BookingService;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.ClassType;
import com.fitness.management.repository.RepositoryFactory;
import com.fitness.management.service.impl.AdminServiceImpl;
import com.fitness.management.service.impl.AuthServiceImpl;
import com.fitness.management.service.impl.BookingServiceImpl;
import com.fitness.management.exception.BookingLimitExceededException;
import com.fitness.management.exception.ClassFullException;
import com.fitness.management.exception.TimeConflictException;
import com.fitness.management.util.DateTimeUtils;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        try {
            // Set the repository implementation type
            // By default, it uses IN_MEMORY implementation
            // To switch to MySQL, uncomment the following line:
            // RepositoryFactory.setDefaultType(RepositoryFactory.RepositoryType.MYSQL);
            
            runFitnessClassSystem();
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runFitnessClassSystem() {
        AuthService authService = new AuthServiceImpl();
        AdminService adminService = new AdminServiceImpl();
        BookingService bookingService = new BookingServiceImpl();
        
        System.out.println("=== Fitness Class Management System ===");
        
        // Register users
        User admin = authService.registerUser("admin", "admin123", UserType.ADMIN);
        User user1 = authService.registerUser("john", "pass123", UserType.PLATINUM);
        User user2 = authService.registerUser("alice", "pass456", UserType.GOLD);
        User user3 = authService.registerUser("bob", "pass789", UserType.SILVER);
        
        System.out.println("Users registered successfully!");
        
        // Create some classes using dynamic dates instead of hard-coded ones
        // Using today and tomorrow for the class dates
        LocalDateTime yogaTime1 = DateTimeUtils.getTodayAt(10, 0);  // Today at 10:00
        LocalDateTime yogaTime2 = DateTimeUtils.getTodayAt(14, 0);  // Today at 14:00
        LocalDateTime danceTime = DateTimeUtils.getTodayAt(16, 0);  // Today at 16:00
        LocalDateTime gymTime = DateTimeUtils.getRelativeDayAt(1, 9, 0);  // Tomorrow at 9:00
        
        FitnessClass yogaClass1 = adminService.createClass("Morning Yoga", ClassType.YOGA, 2, yogaTime1, 60);
        FitnessClass yogaClass2 = adminService.createClass("Afternoon Yoga", ClassType.YOGA, 3, yogaTime2, 60);
        FitnessClass danceClass = adminService.createClass("Hip Hop Dance", ClassType.DANCE, 2, danceTime, 45);
        FitnessClass gymClass = adminService.createClass("Strength Training", ClassType.GYM, 5, gymTime, 90);
        
        System.out.println("Classes created successfully!");
        
        // Book classes
        System.out.println("\n=== Booking Tests ===");
        
        try {
            bookingService.bookClass(user1, yogaClass1);
            System.out.println("User1 booked yoga class 1");
        } catch (Exception e) {
            System.out.println("Error booking yoga class 1 for user1: " + e.getMessage());
        }
        
        try {
            bookingService.bookClass(user2, yogaClass1);
            System.out.println("User2 booked yoga class 1");
        } catch (Exception e) {
            System.out.println("Error booking yoga class 1 for user2: " + e.getMessage());
        }
        
        try {
            bookingService.bookClass(user3, yogaClass1);
            System.out.println("User3 booked yoga class 1");
        } catch (ClassFullException e) {
            System.out.println("User3 couldn't book yoga class 1: " + e.getMessage());
            System.out.println("Adding User3 to waitlist for yoga class 1");
            try {
                bookingService.addToWaitlist(user3, yogaClass1);
            } catch (Exception ex) {
                System.out.println("Error adding user3 to waitlist: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error booking yoga class 1 for user3: " + e.getMessage());
        }
        
        try {
            bookingService.bookClass(user1, danceClass);
            System.out.println("User1 booked dance class");
        } catch (Exception e) {
            System.out.println("Error booking dance class for user1: " + e.getMessage());
        }
        
        // Test cancellation
        System.out.println("\n=== Cancellation Tests ===");
        try {
            bookingService.cancelBooking(user2, yogaClass1);
            System.out.println("User2 cancelled yoga class 1");
            System.out.println("Checking if User3 was moved from waitlist to booked");
        } catch (Exception e) {
            System.out.println("Error cancelling booking: " + e.getMessage());
        }
        
        // Test booking limit
        System.out.println("\n=== Booking Limit Tests ===");
        
        try {
            bookingService.bookClass(user3, yogaClass2);
            System.out.println("User3 booked yoga class 2");
        } catch (Exception e) {
            System.out.println("Error booking yoga class 2 for user3: " + e.getMessage());
        }
        
        try {
            bookingService.bookClass(user3, danceClass);
            System.out.println("User3 booked dance class");
        } catch (Exception e) {
            System.out.println("Error booking dance class for user3: " + e.getMessage());
        }
        
        // This should exceed the booking limit for Silver tier (3 classes)
        try {
            FitnessClass extraClass = adminService.createClass("Extra Class", ClassType.YOGA, 5, 
                    DateTimeUtils.getRelativeDayAt(2, 10, 0), 60);  // Two days from now at 10:00
            bookingService.bookClass(user3, extraClass);
            System.out.println("User3 booked extra class");
        } catch (BookingLimitExceededException e) {
            System.out.println("User3 reached booking limit: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error booking extra class for user3: " + e.getMessage());
        }
        
        // Test time conflict
        System.out.println("\n=== Time Conflict Tests ===");
        try {
            FitnessClass conflictClass = adminService.createClass("Conflict Class", ClassType.DANCE, 5, yogaTime1, 60);
            bookingService.bookClass(user1, conflictClass);
            System.out.println("User1 booked conflict class");
        } catch (TimeConflictException e) {
            System.out.println("Time conflict detected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error booking conflict class for user1: " + e.getMessage());
        }
        
        // Admin cancels a class
        System.out.println("\n=== Admin Cancellation Tests ===");
        try {
            adminService.cancelClass(yogaClass2);
            System.out.println("Admin cancelled yoga class 2");
        } catch (Exception e) {
            System.out.println("Error cancelling class: " + e.getMessage());
        }
        
        // Display all bookings for user1
        System.out.println("\n=== User1's Bookings ===");
        try {
            bookingService.getUserBookings(user1).forEach(booking -> 
                System.out.println(booking.getFitnessClass().getName() + " at " + 
                        DateTimeUtils.formatDateTime(booking.getFitnessClass().getStartTime())));
        } catch (Exception e) {
            System.out.println("Error getting user1's bookings: " + e.getMessage());
        }
        
        System.out.println("\n=== Fitness Class Management System Test Completed ===");
        
        // Demonstrate switching repository implementation at runtime
        System.out.println("\n=== Demonstrate MySQL Repository Implementation ===");
        try {
            // Switch to MySQL implementation
            RepositoryFactory.setDefaultType(RepositoryFactory.RepositoryType.MYSQL);
            
            // Try to use MySQL implementation (will throw exceptions)
            AuthService mySqlAuthService = new AuthServiceImpl();
            mySqlAuthService.registerUser("testuser", "password", UserType.GOLD);
        } catch (Exception e) {
            System.out.println("Expected error using MySQL implementation: " + e.getMessage());
        }
    }
}
