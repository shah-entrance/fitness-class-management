package com.fitness.management.service;

import com.fitness.management.exception.BookingLimitExceededException;
import com.fitness.management.exception.ClassFullException;
import com.fitness.management.exception.TimeConflictException;
import com.fitness.management.model.Booking;
import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.model.WaitlistEntry;
import com.fitness.management.service.impl.AdminServiceImpl;
import com.fitness.management.service.impl.AuthServiceImpl;
import com.fitness.management.service.impl.BookingServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for the fitness class management system.
 * Tests the interaction between different services.
 */
public class SystemIntegrationTest {
    
    private AuthService authService;
    private AdminService adminService;
    private BookingService bookingService;
    
    private User admin;
    private User platinumUser;
    private User goldUser;
    private User silverUser;
    
    @Before
    public void setUp() {
        // Initialize services with real implementations
        authService = new AuthServiceImpl();
        adminService = new AdminServiceImpl();
        bookingService = new BookingServiceImpl();
        
        // Create test users
        admin = authService.registerUser("admin_test", "admin123", UserType.ADMIN);
        platinumUser = authService.registerUser("platinum_test", "pass123", UserType.PLATINUM);
        goldUser = authService.registerUser("gold_test", "pass123", UserType.GOLD);
        silverUser = authService.registerUser("silver_test", "pass123", UserType.SILVER);
    }
    
    @Test
    public void testEndToEndBookingFlow() {
        // 1. Admin creates a class
        LocalDateTime yogaTime = LocalDateTime.now().plusHours(2);
        FitnessClass yogaClass = adminService.createClass("Yoga Flow", ClassType.YOGA, 2, yogaTime, 60);
        assertNotNull(yogaClass);
        
        // 2. Users book the class
        Booking platinumBooking = bookingService.bookClass(platinumUser, yogaClass);
        assertNotNull(platinumBooking);
        assertEquals(platinumUser.getId(), platinumBooking.getUser().getId());
        assertEquals(yogaClass.getId(), platinumBooking.getFitnessClass().getId());
        assertEquals(1, yogaClass.getCurrentAttendance());
        
        Booking goldBooking = bookingService.bookClass(goldUser, yogaClass);
        assertNotNull(goldBooking);
        assertEquals(2, yogaClass.getCurrentAttendance());
        
        // 3. Class is now full, next booking should fail and add to waitlist
        try {
            bookingService.bookClass(silverUser, yogaClass);
            fail("Should have thrown ClassFullException");
        } catch (ClassFullException e) {
            // Expected
        }
        
        // 4. Verify silver user is on waitlist
        List<WaitlistEntry> waitlist = bookingService.getWaitlist(yogaClass);
        assertEquals(1, waitlist.size());
        assertEquals(silverUser.getId(), waitlist.get(0).getUser().getId());
        
        // 5. Gold user cancels booking
        bookingService.cancelBooking(goldUser, yogaClass);
        
        // 6. Silver user should be automatically booked
        List<Booking> silverBookings = bookingService.getUserBookings(silverUser);
        assertEquals(1, silverBookings.size());
        assertEquals(yogaClass.getId(), silverBookings.get(0).getFitnessClass().getId());
        
        // 7. Waitlist should now be empty
        waitlist = bookingService.getWaitlist(yogaClass);
        assertTrue(waitlist.isEmpty());
        
        // 8. Admin cancels the class
        adminService.cancelClass(yogaClass);
        assertTrue(yogaClass.isCancelled());
    }
    
    @Test
    public void testBookingLimitRestriction() {
        // Create classes for Silver user (limit 3)
        LocalDateTime baseTime = LocalDateTime.now().plusHours(3);
        FitnessClass class1 = adminService.createClass("Class 1", ClassType.YOGA, 5, baseTime, 60);
        FitnessClass class2 = adminService.createClass("Class 2", ClassType.DANCE, 5, baseTime.plusHours(2), 45);
        FitnessClass class3 = adminService.createClass("Class 3", ClassType.YOGA, 5, baseTime.plusHours(4), 30);
        FitnessClass class4 = adminService.createClass("Class 4", ClassType.GYM, 5, baseTime.plusHours(6), 60);
        
        // Book up to the limit
        bookingService.bookClass(silverUser, class1);
        bookingService.bookClass(silverUser, class2);
        bookingService.bookClass(silverUser, class3);
        
        // Try to exceed the limit
        try {
            bookingService.bookClass(silverUser, class4);
            fail("Should have thrown BookingLimitExceededException");
        } catch (BookingLimitExceededException e) {
            // Expected
        }
        
        // Cancel a booking and try again - should work
        bookingService.cancelBooking(silverUser, class2);
        
        Booking newBooking = bookingService.bookClass(silverUser, class4);
        assertNotNull(newBooking);
    }
    
    @Test
    public void testTimeConflictPrevention() {
        // Create two classes with overlapping times
        LocalDateTime baseTime = LocalDateTime.now().plusHours(5);
        FitnessClass overlappingClass1 = adminService.createClass("Overlap 1", ClassType.YOGA, 5, baseTime, 60);
        FitnessClass overlappingClass2 = adminService.createClass("Overlap 2", ClassType.DANCE, 5, baseTime.plusMinutes(30), 60);
        
        // Book first class
        bookingService.bookClass(platinumUser, overlappingClass1);
        
        // Try to book the second class which overlaps
        try {
            bookingService.bookClass(platinumUser, overlappingClass2);
            fail("Should have thrown TimeConflictException");
        } catch (TimeConflictException e) {
            // Expected
        }
        
        // Create a non-overlapping class
        FitnessClass nonOverlappingClass = adminService.createClass("Non Overlap", ClassType.YOGA, 5, baseTime.plusHours(2), 60);
        
        // Should be able to book non-overlapping class
        Booking booking = bookingService.bookClass(platinumUser, nonOverlappingClass);
        assertNotNull(booking);
    }
}