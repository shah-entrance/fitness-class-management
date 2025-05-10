package com.fitness.management.model;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class WaitlistEntryTest {
    
    @Test
    public void testConstructor() {
        User user = new User("testuser", "password", UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        FitnessClass fitnessClass = new FitnessClass("Yoga", ClassType.YOGA, 10, startTime, 60);
        
        WaitlistEntry entry = new WaitlistEntry(user, fitnessClass);
        
        assertNotNull(entry.getId());
        assertEquals(user, entry.getUser());
        assertEquals(fitnessClass, entry.getFitnessClass());
        assertNotNull(entry.getEntryTime());
        assertFalse(entry.isProcessed());
    }
    
    @Test
    public void testProcessed() {
        User user = new User("testuser", "password", UserType.GOLD);
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        FitnessClass fitnessClass = new FitnessClass("Yoga", ClassType.YOGA, 10, startTime, 60);
        
        WaitlistEntry entry = new WaitlistEntry(user, fitnessClass);
        assertFalse(entry.isProcessed());
        
        entry.setProcessed(true);
        assertTrue(entry.isProcessed());
        
        entry.setProcessed(false);
        assertFalse(entry.isProcessed());
    }
}