package com.fitness.management.model;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class BookingTest {
    
    @Test
    public void testConstructor() {
        User user = new User("testuser", "password", UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        FitnessClass fitnessClass = new FitnessClass("Yoga", ClassType.YOGA, 10, startTime, 60);
        
        Booking booking = new Booking(user, fitnessClass);
        
        assertNotNull(booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(fitnessClass, booking.getFitnessClass());
        assertNotNull(booking.getBookingTime());
        assertFalse(booking.isCancelled());
    }
    
    @Test
    public void testCancellation() {
        User user = new User("testuser", "password", UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        FitnessClass fitnessClass = new FitnessClass("Yoga", ClassType.YOGA, 10, startTime, 60);
        
        Booking booking = new Booking(user, fitnessClass);
        assertFalse(booking.isCancelled());
        
        booking.setCancelled(true);
        assertTrue(booking.isCancelled());
        
        booking.setCancelled(false);
        assertFalse(booking.isCancelled());
    }
}