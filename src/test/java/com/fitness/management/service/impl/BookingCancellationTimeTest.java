package com.fitness.management.service.impl;

import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.model.*;
import com.fitness.management.service.BookingService;
import com.fitness.management.service.mock.MockBookingRepository;
import com.fitness.management.service.mock.MockFitnessClassRepository;
import com.fitness.management.service.mock.MockWaitlistRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Tests for the booking cancellation time restriction feature.
 * This verifies that users can only cancel bookings at least 30 minutes before the class starts,
 * while admins can cancel bookings at any time.
 */
public class BookingCancellationTimeTest {

    private BookingService bookingService;
    
    private MockBookingRepository mockBookingRepository;
    private MockFitnessClassRepository mockFitnessClassRepository;
    private MockWaitlistRepository mockWaitlistRepository;
    
    private User regularUser;
    private User adminUser;
    private FitnessClass upcomingClass;
    private FitnessClass startingSoonClass;
    private FitnessClass startingIn30MinClass;
    private FitnessClass startingIn31MinClass;
    
    @Before
    public void setUp() throws Exception {
        // Initialize mock repositories
        mockBookingRepository = new MockBookingRepository();
        mockFitnessClassRepository = new MockFitnessClassRepository();
        mockWaitlistRepository = new MockWaitlistRepository();
        
        // Create BookingServiceImpl instance
        bookingService = new BookingServiceImpl(
            mockBookingRepository,
            mockWaitlistRepository,
            mockFitnessClassRepository
        );
        
        // Clear any existing data
        mockBookingRepository.clear();
        mockFitnessClassRepository.clear();
        mockWaitlistRepository.clear();
        
        // Create test users
        regularUser = new User("regular_user", "password", UserType.GOLD);
        
        // Create an admin user (note: we need to modify this class to have a non-zero booking limit for tests)
        adminUser = new User("admin_user", "password", UserType.ADMIN) {
            @Override
            public int getBookingLimit() {
                return 10; // Override admin's booking limit just for testing purposes
            }
        };
        
        // Create test fitness classes with non-overlapping times
        LocalDateTime now = LocalDateTime.now();
        
        // Class starting in more than an hour - 2 hours from now
        upcomingClass = new FitnessClass("Upcoming Class", ClassType.YOGA, 10, now.plusHours(2), 60);
        
        // Class starting in less than 30 minutes (can't be cancelled by regular users) - 15 minutes from now
        startingSoonClass = new FitnessClass("Starting Soon Class", ClassType.CARDIO, 10, now.plusHours(4), 60);
        
        // Class starting in exactly 30 minutes (edge case - can't be cancelled by regular users)
        startingIn30MinClass = new FitnessClass("30min Class", ClassType.DANCE, 10, now.plusHours(6), 60);
        
        // Class starting in 31 minutes (should be cancellable by all users)
        startingIn31MinClass = new FitnessClass("31min Class", ClassType.CROSSFIT, 10, now.plusHours(8), 60);
        
        // Save the fitness classes to the repository
        mockFitnessClassRepository.save(upcomingClass);
        mockFitnessClassRepository.save(startingSoonClass);
        mockFitnessClassRepository.save(startingIn30MinClass);
        mockFitnessClassRepository.save(startingIn31MinClass);
        
        // Book all classes for the regular user
        bookingService.bookClass(regularUser, upcomingClass);
        bookingService.bookClass(regularUser, startingSoonClass);
        bookingService.bookClass(regularUser, startingIn30MinClass);
        bookingService.bookClass(regularUser, startingIn31MinClass);
        
        // For testing time-based cancellations, we need to override the actual start times
        // Since we can't actually wait for time to pass in a unit test
        upcomingClass.setStartTime(now.plusHours(2));
        startingSoonClass.setStartTime(now.plusMinutes(15));
        startingIn30MinClass.setStartTime(now.plusMinutes(30));
        startingIn31MinClass.setStartTime(now.plusMinutes(31));
    }
    
    @Test
    public void testRegularUserCanCancelBookingWellInAdvance() {
        // Regular user should be able to cancel a booking for a class starting in 2 hours
        bookingService.cancelBooking(regularUser, upcomingClass);
        
        // Verify booking is cancelled
        assertTrue(mockBookingRepository.findByUserAndFitnessClass(regularUser, upcomingClass).get().isCancelled());
    }
    
    @Test
    public void testRegularUserCanCancelBookingJustOverThreshold() {
        // Regular user should be able to cancel a booking for a class starting in 31 minutes
        bookingService.cancelBooking(regularUser, startingIn31MinClass);
        
        // Verify booking is cancelled
        assertTrue(mockBookingRepository.findByUserAndFitnessClass(regularUser, startingIn31MinClass).get().isCancelled());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRegularUserCannotCancelBookingAtThreshold() {
        // Regular user should NOT be able to cancel a booking for a class starting in exactly 30 minutes
        bookingService.cancelBooking(regularUser, startingIn30MinClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRegularUserCannotCancelBookingTooClose() {
        // Regular user should NOT be able to cancel a booking for a class starting soon
        bookingService.cancelBooking(regularUser, startingSoonClass);
    }
    
    @Test
    public void testAdminCanCancelBookingAnytime() {
        // Book a class for the admin user that starts very soon
        FitnessClass adminClass = new FitnessClass("Admin Class", ClassType.YOGA, 10, 
                                                 LocalDateTime.now().plusHours(20), 60);
        mockFitnessClassRepository.save(adminClass);
        bookingService.bookClass(adminUser, adminClass);
        
        // Set the class to start very soon
        adminClass.setStartTime(LocalDateTime.now().plusMinutes(10));
        
        // Admin should be able to cancel a booking for a class starting soon
        bookingService.cancelBooking(adminUser, adminClass);
        
        // Verify booking is cancelled
        assertTrue(mockBookingRepository.findByUserAndFitnessClass(adminUser, adminClass).get().isCancelled());
    }
    
    @Test
    public void testCancellationTiming() {
        // Create classes with specific timings for more precise testing
        LocalDateTime now = LocalDateTime.now();
        
        // Test cases that should succeed (more than 30 minutes)
        FitnessClass testClass35min = new FitnessClass("Test Class 35", ClassType.YOGA, 10, now.plusHours(10), 60);
        mockFitnessClassRepository.save(testClass35min);
        bookingService.bookClass(regularUser, testClass35min);
        testClass35min.setStartTime(now.plusMinutes(35));
        
        // First verify the success case - should be able to cancel with 35 minutes remaining
        bookingService.cancelBooking(regularUser, testClass35min);
        assertTrue("Booking should be cancelled when class starts in 35 minutes",
                 mockBookingRepository.findByUserAndFitnessClass(regularUser, testClass35min).get().isCancelled());
                 
        // Now create a class that's too close to cancel
        FitnessClass testClass25min = new FitnessClass("Test Class 25", ClassType.YOGA, 10, now.plusHours(12), 60);
        mockFitnessClassRepository.save(testClass25min);
        bookingService.bookClass(regularUser, testClass25min);
        testClass25min.setStartTime(now.plusMinutes(25));
        
        // Now verify the failure case - should not be able to cancel with 25 minutes remaining
        try {
            bookingService.cancelBooking(regularUser, testClass25min);
            fail("Should not be able to cancel a class starting in 25 minutes");
        } catch (IllegalStateException e) {
            // Expected exception - verify message mentions 30 minutes
            assertTrue("Exception should mention 30-minute cancellation policy", 
                    e.getMessage().contains("30 minutes"));
        }
    }
    
    @Test
    public void testCancelledBookingStatusIsCorrect() {
        // Book and cancel a class that's eligible for cancellation
        bookingService.cancelBooking(regularUser, upcomingClass);
        
        // Verify the booking is marked as cancelled in the repository
        assertTrue("Booking should be marked as cancelled",
                 mockBookingRepository.findByUserAndFitnessClass(regularUser, upcomingClass).get().isCancelled());
    }
    
    @Test
    public void testWaitlistProcessingAfterCancellation() {
        // Create a class with capacity 1
        FitnessClass limitedClass = new FitnessClass("Limited Class", ClassType.YOGA, 1, 
                                                   LocalDateTime.now().plusHours(10), 60);
        mockFitnessClassRepository.save(limitedClass);
        
        // Book the class with regular user
        bookingService.bookClass(regularUser, limitedClass);
        
        // Set up a second user on the waitlist
        User waitlistedUser = new User("waitlisted_user", "password", UserType.GOLD);
        
        try {
            // This should fail due to capacity and add to waitlist
            bookingService.bookClass(waitlistedUser, limitedClass);
            fail("Should have thrown ClassFullException");
        } catch (Exception e) {
            // Expected exception
        }
        
        // Verify user is on waitlist
        assertFalse(mockBookingRepository.findByUserAndFitnessClass(waitlistedUser, limitedClass).isPresent());
        assertTrue(mockWaitlistRepository.findByUserAndFitnessClass(waitlistedUser, limitedClass).isPresent());
        
        // Update the class time to ensure it's far in the future (above 30 min threshold)
        limitedClass.setStartTime(LocalDateTime.now().plusHours(2)); 
        
        // Now cancel the booking
        bookingService.cancelBooking(regularUser, limitedClass);
        
        // Give a little time for automatic processing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Verify waitlisted user now has a booking
        assertTrue(mockBookingRepository.findByUserAndFitnessClass(waitlistedUser, limitedClass).isPresent());
        
        // Verify waitlist entry is processed
        assertTrue(mockWaitlistRepository.findByUserAndFitnessClass(waitlistedUser, limitedClass).get().isProcessed());
    }
    
    @Test
    public void testUserTypesDifferentCancellationRules() {
        // Create a special admin for this test with a non-zero booking limit
        User testAdmin = new User("test_admin", "password", UserType.ADMIN) {
            @Override
            public int getBookingLimit() {
                return 10; // Override for testing
            }
        };
        
        // Create different users with different types
        User silverUser = new User("silver_test", "password", UserType.SILVER);
        User goldUser = new User("gold_test", "password", UserType.GOLD);
        User platinumUser = new User("platinum_test", "password", UserType.PLATINUM);
        
        // Create different classes for each user to avoid time conflicts
        LocalDateTime now = LocalDateTime.now();
        
        FitnessClass silverClass = new FitnessClass("Silver Class", ClassType.YOGA, 10, now.plusHours(20), 60);
        FitnessClass goldClass = new FitnessClass("Gold Class", ClassType.CARDIO, 10, now.plusHours(22), 60);
        FitnessClass platinumClass = new FitnessClass("Platinum Class", ClassType.DANCE, 10, now.plusHours(24), 60);
        FitnessClass adminClass = new FitnessClass("Admin Class", ClassType.GYM, 10, now.plusHours(26), 60);
        
        mockFitnessClassRepository.save(silverClass);
        mockFitnessClassRepository.save(goldClass);
        mockFitnessClassRepository.save(platinumClass);
        mockFitnessClassRepository.save(adminClass);
        
        // Book classes
        bookingService.bookClass(silverUser, silverClass);
        bookingService.bookClass(goldUser, goldClass);
        bookingService.bookClass(platinumUser, platinumClass);
        bookingService.bookClass(testAdmin, adminClass);
        
        // Set all classes to start soon (15 minutes from now)
        silverClass.setStartTime(now.plusMinutes(15));
        goldClass.setStartTime(now.plusMinutes(15));
        platinumClass.setStartTime(now.plusMinutes(15));
        adminClass.setStartTime(now.plusMinutes(15));
        
        // All regular user types should be subject to the same cancellation restrictions
        try {
            bookingService.cancelBooking(silverUser, silverClass);
            fail("Silver user should not be able to cancel class starting soon");
        } catch (IllegalStateException e) {
            // Expected exception - verify message mentions 30 minutes
            assertTrue(e.getMessage().contains("30 minutes"));
        }
        
        try {
            bookingService.cancelBooking(goldUser, goldClass);
            fail("Gold user should not be able to cancel class starting soon");
        } catch (IllegalStateException e) {
            // Expected exception - verify message mentions 30 minutes
            assertTrue(e.getMessage().contains("30 minutes"));
        }
        
        try {
            bookingService.cancelBooking(platinumUser, platinumClass);
            fail("Platinum user should not be able to cancel class starting soon");
        } catch (IllegalStateException e) {
            // Expected exception - verify message mentions 30 minutes
            assertTrue(e.getMessage().contains("30 minutes"));
        }
        
        // Only admin should be able to cancel without time restrictions
        try {
            bookingService.cancelBooking(testAdmin, adminClass);
            // Verify admin booking was cancelled
            assertTrue(mockBookingRepository.findByUserAndFitnessClass(testAdmin, adminClass).get().isCancelled());
        } catch (IllegalStateException e) {
            fail("Admin should be able to cancel class starting soon");
        }
    }
}
