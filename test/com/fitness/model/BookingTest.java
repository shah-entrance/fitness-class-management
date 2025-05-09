package com.fitness.model;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class BookingTest {
    
    @Test
    public void testConstructor() {
        com.fitness.model.User user = new com.fitness.model.User("testuser", "password", com.fitness.model.UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        com.fitness.model.FitnessClass fitnessClass = new com.fitness.model.FitnessClass("Yoga", com.fitness.model.ClassType.YOGA, 10, startTime, 60);
        
        com.fitness.model.Booking booking = new com.fitness.model.Booking(user, fitnessClass);
        
        assertNotNull(booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(fitnessClass, booking.getFitnessClass());
        assertNotNull(booking.getBookingTime());
        assertFalse(booking.isCancelled());
    }
    
    @Test
    public void testCancellation() {
        com.fitness.model.User user = new com.fitness.model.User("testuser", "password", com.fitness.model.UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        com.fitness.model.FitnessClass fitnessClass = new com.fitness.model.FitnessClass("Yoga", com.fitness.model.ClassType.YOGA, 10, startTime, 60);
        
        com.fitness.model.Booking booking = new com.fitness.model.Booking(user, fitnessClass);
        assertFalse(booking.isCancelled());
        
        booking.setCancelled(true);
        assertTrue(booking.isCancelled());
        
        booking.setCancelled(false);
        assertFalse(booking.isCancelled());
    }
}