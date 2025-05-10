package com.fitness.management.service.impl;

import com.fitness.management.exception.ClassFullException;
import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.repository.RepositoryFactory;
import com.fitness.management.service.AdminService;
import com.fitness.management.service.AuthService;
import com.fitness.management.service.BookingService;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests that specifically focus on extreme race conditions in the booking system
 */
public class RaceConditionBookingTest {
    
    private BookingService bookingService;
    private AdminService adminService;
    private AuthService authService;
    
    private List<FitnessClass> fitnessClasses;
    private List<User> users;
    private Random random;
    private User adminUser; // Admin user for operations that require admin privileges
    
    @Before
    public void setUp() {
        // Set to in-memory implementation for tests
        RepositoryFactory.setDefaultType(RepositoryFactory.RepositoryType.IN_MEMORY);
        
        // Initialize services
        bookingService = new BookingServiceImpl();
        adminService = new AdminServiceImpl();
        authService = new AuthServiceImpl();
        
        random = new Random();
        
        // Create multiple fitness classes - ensure classes start far enough in the future
        // to accommodate the 30-minute cancellation policy
        fitnessClasses = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().plusHours(3);
        
        for (int i = 0; i < 2; i++) { // Just 2 classes for simplicity
            // Create classes with small capacities to increase chance of contention
            FitnessClass fitnessClass = adminService.createClass(
                    "Class " + i,
                    ClassType.values()[random.nextInt(ClassType.values().length)],
                    2,  // Very small capacity to ensure contention
                    baseTime.plusMinutes(i * 90),
                    60);
            fitnessClasses.add(fitnessClass);
        }
        
        // Create test users - more than total capacity but reduced for simplicity
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // Just 5 users
            String username = "raceUser" + i;
            User user = authService.registerUser(username, "password", UserType.PLATINUM);
            users.add(user);
        }
        
        // Create an admin user for operations that require admin privileges
        adminUser = authService.registerUser("adminRaceUser", "password", UserType.ADMIN);
    }
    
    /**
     * Test booking system under race conditions where users are trying to
     * book the same class at the same time
     */
    @Test
    public void testConcurrentBooking() throws InterruptedException {
        final int THREAD_COUNT = 5; // One thread per user
        
        // Single class that everyone will try to book
        FitnessClass targetClass = fitnessClasses.get(0);
        
        // Track successful bookings
        AtomicInteger successfulBookings = new AtomicInteger(0);
        
        // Use CountDownLatch to coordinate test
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        
        // Submit tasks for each user
        for (int i = 0; i < THREAD_COUNT; i++) {
            final User user = users.get(i);
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    
                    try {
                        // Try to book the class
                        bookingService.bookClass(user, targetClass);
                        successfulBookings.incrementAndGet();
                        System.out.println("User " + user.getUsername() + " successfully booked the class");
                    } catch (ClassFullException e) {
                        // Expected when class is full
                        System.out.println("Class full for user " + user.getUsername());
                    } catch (Exception e) {
                        System.err.println("Error during booking: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Thread execution error: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // Start all threads
        startLatch.countDown();
        
        // Wait for all threads to complete with a reasonable timeout
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        assertTrue("Test timed out", completed);
        
        executorService.shutdown();
        
        // Verify the class was not overbooked
        int capacity = targetClass.getCapacity();
        int attendance = targetClass.getCurrentAttendance();
        
        System.out.println("Class capacity: " + capacity);
        System.out.println("Actual attendance: " + attendance);
        System.out.println("Recorded successful bookings: " + successfulBookings.get());
        
        assertTrue("Class should not be overbooked", attendance <= capacity);
        assertEquals("Successful bookings should match attendance", successfulBookings.get(), attendance);
    }
    
    /**
     * Test waitlist processing after cancellation
     */
    @Test
    public void testWaitlistProcessing() throws InterruptedException {
        // Create a class with very low capacity
        FitnessClass testClass = adminService.createClass(
            "Waitlist Test Class",
            ClassType.YOGA,
            1, // Just 1 spot
            LocalDateTime.now().plusHours(5),
            60
        );
        
        // Book the one spot with user 0
        User bookedUser = users.get(0);
        bookingService.bookClass(bookedUser, testClass);
        assertEquals("Class should be at capacity", 1, testClass.getCurrentAttendance());
        
        // Add a user to the waitlist
        User waitlistedUser = users.get(1);
        try {
            bookingService.bookClass(waitlistedUser, testClass);
            fail("Should have thrown ClassFullException");
        } catch (ClassFullException e) {
            // Expected - verify user was added to waitlist
            assertTrue("User should be on waitlist", 
                    bookingService.getWaitlist(testClass).size() == 1);
        }
        
        // Cancel the booking using the original user (not admin)
        // The class is scheduled far enough in the future to allow cancellation
        bookingService.cancelBooking(bookedUser, testClass);
        
        // Allow time for waitlist processing
        Thread.sleep(500);
        
        // Verify the waitlisted user was moved into the class
        assertEquals("Class should still be at capacity after waitlist processing",
                1, testClass.getCurrentAttendance());
        assertEquals("Waitlist should be empty", 0, bookingService.getWaitlist(testClass).size());
        
        // Verify the waitlisted user now has a booking
        assertTrue("User from waitlist should now have a booking",
                bookingService.getUserBookings(waitlistedUser).stream()
                        .anyMatch(b -> b.getFitnessClass().getId().equals(testClass.getId()) && !b.isCancelled()));
    }
}
