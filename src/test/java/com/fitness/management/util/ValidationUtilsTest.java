package com.fitness.management.util;

import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ValidationUtilsTest {

    @Test
    public void testIsValidUsername() {
        // Valid usernames
        assertTrue(ValidationUtils.isValidUsername("user123"));
        assertTrue(ValidationUtils.isValidUsername("validUser"));
        assertTrue(ValidationUtils.isValidUsername("test_user"));
        
        // Invalid usernames
        assertFalse(ValidationUtils.isValidUsername(null));
        assertFalse(ValidationUtils.isValidUsername(""));
        assertFalse(ValidationUtils.isValidUsername("  "));
        assertFalse(ValidationUtils.isValidUsername("ab")); // Too short, min is 3
    }
    
    @Test
    public void testIsValidPassword() {
        // Valid passwords
        assertTrue(ValidationUtils.isValidPassword("password123"));
        assertTrue(ValidationUtils.isValidPassword("validPass"));
        
        // Invalid passwords
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword("short")); // Assuming PASSWORD_MIN_LENGTH is 6
    }
    
    @Test
    public void testIsValidBooking() {
        User validUser = new User("user", "password", UserType.GOLD);
        LocalDateTime futureTime = LocalDateTime.now().plusHours(2);
        FitnessClass validClass = new FitnessClass("Yoga", ClassType.YOGA, 10, futureTime, 60);
        FitnessClass cancelledClass = new FitnessClass("Cancelled", ClassType.DANCE, 10, futureTime, 60);
        cancelledClass.setCancelled(true);
        FitnessClass pastClass = new FitnessClass("Past", ClassType.YOGA, 10, LocalDateTime.now().minusHours(1), 60);
        
        // Valid booking
        assertTrue(ValidationUtils.isValidBooking(validUser, validClass));
        
        // Invalid bookings
        assertFalse(ValidationUtils.isValidBooking(null, validClass));
        assertFalse(ValidationUtils.isValidBooking(validUser, null));
        assertFalse(ValidationUtils.isValidBooking(validUser, cancelledClass));
        assertFalse(ValidationUtils.isValidBooking(validUser, pastClass));
    }
    
    @Test
    public void testIsValidCapacity() {
        // Valid capacities
        assertTrue(ValidationUtils.isValidCapacity(1));
        assertTrue(ValidationUtils.isValidCapacity(10));
        assertTrue(ValidationUtils.isValidCapacity(100));
        
        // Invalid capacities
        assertFalse(ValidationUtils.isValidCapacity(0));
        assertFalse(ValidationUtils.isValidCapacity(-1));
        assertFalse(ValidationUtils.isValidCapacity(-10));
    }
    
    @Test
    public void testIsValidDuration() {
        // Valid durations (assuming MIN_BOOKING_TIME_MINUTES is 30)
        assertTrue(ValidationUtils.isValidDuration(30));
        assertTrue(ValidationUtils.isValidDuration(45));
        assertTrue(ValidationUtils.isValidDuration(60));
        assertTrue(ValidationUtils.isValidDuration(90));
        
        // Invalid durations
        assertFalse(ValidationUtils.isValidDuration(0));
        assertFalse(ValidationUtils.isValidDuration(15)); // Assuming MIN_BOOKING_TIME_MINUTES is 30
        assertFalse(ValidationUtils.isValidDuration(-10));
    }
}