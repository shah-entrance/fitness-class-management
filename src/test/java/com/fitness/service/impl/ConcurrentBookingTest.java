package com.fitness.service.impl;

import com.fitness.exception.ClassFullException;
import com.fitness.model.FitnessClass;
import com.fitness.model.ClassType;
import com.fitness.model.User;
import com.fitness.model.UserType;
import com.fitness.model.Booking;
import com.fitness.model.WaitlistEntry;
import com.fitness.repository.RepositoryFactory;
import com.fitness.service.BookingService;
import com.fitness.service.AdminService;
import com.fitness.service.AuthService;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for concurrent booking operations to validate thread safety
 */
public class ConcurrentBookingTest {
    
    private BookingService bookingService;
    private AdminService adminService;
    private AuthService authService;
    
    private FitnessClass fitnessClass;
    private List<User> users;
    
    @Before
    public void setUp() {
        // Set to in-memory implementation for tests
        RepositoryFactory.setDefaultType(RepositoryFactory.RepositoryType.IN_MEMORY);
        
        // Initialize services
        bookingService = new BookingServiceImpl();
        adminService = new AdminServiceImpl();
        authService = new AuthServiceImpl();
        
        // Create test fitness class with limited capacity
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        fitnessClass = adminService.createClass("Concurrent Test Class", ClassType.YOGA, 5, startTime, 60);
        
        // Create test users
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String username = "user" + i;
            User user = authService.registerUser(username, "password", UserType.PLATINUM);
            users.add(user);
        }
    }
    
    /**
     * Test concurrent booking requests where class capacity is limited
     * This verifies that our concurrency control prevents overbooking
     */
    @Test
    public void testConcurrentBookingWithLimitedCapacity() throws InterruptedException {
        final int CAPACITY = fitnessClass.getCapacity();
        final int THREAD_COUNT = 10; // More users than capacity
        
        // Create a countdown latch to ensure all threads start at the same time
        CountDownLatch startLatch = new CountDownLatch(1);
        
        // Create a latch to wait for all threads to finish
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        
        // Keep track of successful and failed bookings
        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);
        
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        
        // Submit booking tasks
        for (int i = 0; i < THREAD_COUNT; i++) {
            final User user = users.get(i);
            executorService.submit(() -> {
                try {
                    // Wait for signal to start
                    startLatch.await();
                    
                    // Try to book the class
                    try {
                        Booking booking = bookingService.bookClass(user, fitnessClass);
                        // If we get here, booking was successful
                        successfulBookings.incrementAndGet();
                    } catch (ClassFullException e) {
                        // Expected exception when class is full
                        failedBookings.incrementAndGet();
                    } catch (Exception e) {
                        // Log unexpected exceptions
                        System.err.println("Unexpected exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Signal that this thread is done
                    endLatch.countDown();
                }
            });
        }
        
        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all threads to complete
        boolean allDone = endLatch.await(10, TimeUnit.SECONDS);
        assertTrue("Timed out waiting for concurrent booking operations", allDone);
        
        // Shut down the executor service
        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);
        
        // Verify results
        System.out.println("Successful bookings: " + successfulBookings.get());
        System.out.println("Failed bookings due to class full: " + failedBookings.get());
        
        // Check that we didn't overbook the class
        assertEquals("Class should be booked to exactly its capacity", 
                CAPACITY, successfulBookings.get());
        
        // Check that the class attendance matches the successful bookings
        assertEquals("Class attendance should match successful bookings", 
                CAPACITY, fitnessClass.getCurrentAttendance());
        
        // Check that the right number of booking attempts failed
        assertEquals("Number of failed bookings should be correct", 
                THREAD_COUNT - CAPACITY, failedBookings.get());
    }
    
    /**
     * Test that slots can be freed and rebooked
     */
    @Test
    public void testConcurrentBookingAndCancellation() throws InterruptedException {
        // Reset to a clean state first - this helps with potential test interactions
        setUp();
        
        final int CAPACITY = fitnessClass.getCapacity();
        
        // Book the class to capacity with users 0-4
        for (int i = 0; i < CAPACITY; i++) {
            bookingService.bookClass(users.get(i), fitnessClass);
        }
        
        // Verify class is full
        assertEquals(CAPACITY, fitnessClass.getCurrentAttendance());
        
        // Cancel bookings for users 0-1
        for (int i = 0; i < 2; i++) {
            bookingService.cancelBooking(users.get(i), fitnessClass);
        }
        
        // Book with new users 5-6
        for (int i = 5; i < 7; i++) {
            bookingService.bookClass(users.get(i), fitnessClass);
        }
        
        // Verify class is still at capacity
        assertEquals(CAPACITY, fitnessClass.getCurrentAttendance());
        
        // Check if user 0 has a booking by using the returned list
        List<Booking> bookingsForUser0 = bookingService.getUserBookings(users.get(0));
        boolean user0HasBookingForClass = false;
        
        for (Booking booking : bookingsForUser0) {
            if (booking.getFitnessClass().getId().equals(fitnessClass.getId())) {
                user0HasBookingForClass = true;
                break;
            }
        }
        
        assertFalse("User 0 should not have a booking for this class", user0HasBookingForClass);
        
        // Verify users 2-4 still have bookings
        for (int i = 2; i < 5; i++) {
            List<Booking> bookings = bookingService.getUserBookings(users.get(i));
            boolean foundBooking = false;
            
            for (Booking booking : bookings) {
                if (booking.getFitnessClass().getId().equals(fitnessClass.getId())) {
                    foundBooking = true;
                    break;
                }
            }
            
            assertTrue("User " + i + " should have a booking for this class", foundBooking);
        }
        
        // Verify users 5-6 now have bookings
        for (int i = 5; i < 7; i++) {
            List<Booking> bookings = bookingService.getUserBookings(users.get(i));
            boolean foundBooking = false;
            
            for (Booking booking : bookings) {
                if (booking.getFitnessClass().getId().equals(fitnessClass.getId())) {
                    foundBooking = true;
                    break;
                }
            }
            
            assertTrue("User " + i + " should have a booking for this class", foundBooking);
        }
        
        System.out.println("Booking and cancellation test passed successfully");
    }
    
    /**
     * Test the waitlist functionality
     * When a user cancels, a user from the waitlist should be automatically booked
     */
    @Test
    public void testConcurrentBookingWithWaitlist() throws InterruptedException {
        // Reset to a clean state first - this helps with potential test interactions
        setUp();
        
        final int CAPACITY = fitnessClass.getCapacity();
        final int WAITLIST_SIZE = 3; // Number of users to add to waitlist
        
        // Book the class to capacity with users 0-4
        for (int i = 0; i < CAPACITY; i++) {
            bookingService.bookClass(users.get(i), fitnessClass);
        }
        
        // Add users 5-7 to waitlist
        for (int i = CAPACITY; i < CAPACITY + WAITLIST_SIZE; i++) {
            User user = users.get(i);
            
            try {
                bookingService.bookClass(user, fitnessClass);
                fail("Should have thrown ClassFullException");
            } catch (ClassFullException e) {
                // Expected - class is full
                // User should be added to waitlist automatically
            }
        }
        
        // Verify waitlist has 3 entries
        List<WaitlistEntry> waitlist = bookingService.getWaitlist(fitnessClass);
        assertEquals("Waitlist should have 3 entries initially", WAITLIST_SIZE, waitlist.size());
        
        // We'll only cancel 2 bookings - users 0 and 1
        for (int i = 0; i < 2; i++) {
            bookingService.cancelBooking(users.get(i), fitnessClass);
            // Wait for waitlist processing
            Thread.sleep(200);
        }
        
        // Wait a bit longer for processing to complete
        Thread.sleep(500);
        
        // Get the updated waitlist
        waitlist = bookingService.getWaitlist(fitnessClass);
        
        // Verify that exactly 1 user remains on the waitlist
        assertEquals("Waitlist should have exactly 1 user remaining", 1, waitlist.size());
        
        // Verify class is still at capacity
        assertEquals("Class should still be at capacity", CAPACITY, fitnessClass.getCurrentAttendance());
        
        // Find out which user is still on the waitlist
        User remainingWaitlistedUser = null;
        if (!waitlist.isEmpty()) {
            remainingWaitlistedUser = waitlist.get(0).getUser();
        }
        
        assertNotNull("There should be a user remaining on the waitlist", remainingWaitlistedUser);
        
        // Check if users 5 and 6 were moved from waitlist by looking for bookings
        boolean user5HasBooking = false;
        boolean user6HasBooking = false;
        boolean user7HasBooking = false;
        
        for (int i = 5; i < CAPACITY + WAITLIST_SIZE; i++) {
            List<Booking> bookings = bookingService.getUserBookings(users.get(i));
            boolean hasBookingForClass = false;
            
            for (Booking booking : bookings) {
                if (booking.getFitnessClass().getId().equals(fitnessClass.getId())) {
                    hasBookingForClass = true;
                    break;
                }
            }
            
            if (i == 5) user5HasBooking = hasBookingForClass;
            if (i == 6) user6HasBooking = hasBookingForClass;
            if (i == 7) user7HasBooking = hasBookingForClass;
        }
        
        assertTrue("User 5 should have been moved from waitlist to booked", user5HasBooking);
        assertTrue("User 6 should have been moved from waitlist to booked", user6HasBooking);
        assertFalse("User 7 should still be on waitlist", user7HasBooking);
        
        // Verify that the remaining waitlisted user is user 7
        assertEquals("The remaining waitlisted user should be user 7", 
                     users.get(7).getId(), remainingWaitlistedUser.getId());
    }
}
