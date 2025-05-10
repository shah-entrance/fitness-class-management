package com.fitness.management.model;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class FitnessClassTest {
    
    @Test
    public void testConstructor() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        FitnessClass fitnessClass = new FitnessClass("Test Class", ClassType.YOGA, 10, startTime, 60);
        
        assertNotNull(fitnessClass.getId());
        assertEquals("Test Class", fitnessClass.getName());
        assertEquals(ClassType.YOGA, fitnessClass.getClassType());
        assertEquals(10, fitnessClass.getCapacity());
        assertEquals(0, fitnessClass.getCurrentAttendance());
        assertEquals(startTime, fitnessClass.getStartTime());
        assertEquals(60, fitnessClass.getDurationMinutes());
        assertEquals(startTime.plusMinutes(60), fitnessClass.getEndTime());
        assertFalse(fitnessClass.isCancelled());
    }
    
    @Test
    public void testSetters() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(3);
        FitnessClass fitnessClass = new FitnessClass("Original Name", ClassType.DANCE, 15, startTime, 45);
        
        // Test all the setters
        fitnessClass.setName("New Name");
        fitnessClass.setClassType(ClassType.CARDIO);
        fitnessClass.setCapacity(20);
        fitnessClass.setCurrentAttendance(5);
        LocalDateTime newTime = startTime.plusHours(1);
        fitnessClass.setStartTime(newTime);
        fitnessClass.setDurationMinutes(90);
        fitnessClass.setCancelled(true);
        
        // Verify all changes
        assertEquals("New Name", fitnessClass.getName());
        assertEquals(ClassType.CARDIO, fitnessClass.getClassType());
        assertEquals(20, fitnessClass.getCapacity());
        assertEquals(5, fitnessClass.getCurrentAttendance());
        assertEquals(newTime, fitnessClass.getStartTime());
        assertEquals(90, fitnessClass.getDurationMinutes());
        assertEquals(newTime.plusMinutes(90), fitnessClass.getEndTime());
        assertTrue(fitnessClass.isCancelled());
    }
    
    @Test
    public void testIncrementAndDecrementAttendance() {
        FitnessClass fitnessClass = new FitnessClass("Test Class", ClassType.YOGA, 10, 
                                                     LocalDateTime.now().plusHours(2), 60);
        assertEquals(0, fitnessClass.getCurrentAttendance());
        
        // Test increment
        fitnessClass.incrementCurrentAttendance();
        assertEquals(1, fitnessClass.getCurrentAttendance());
        
        fitnessClass.incrementCurrentAttendance();
        assertEquals(2, fitnessClass.getCurrentAttendance());
        
        // Test decrement
        fitnessClass.decrementCurrentAttendance();
        assertEquals(1, fitnessClass.getCurrentAttendance());
        
        fitnessClass.decrementCurrentAttendance();
        assertEquals(0, fitnessClass.getCurrentAttendance());
        
        // Test decrement below 0 shouldn't change the value
        fitnessClass.decrementCurrentAttendance();
        assertEquals(0, fitnessClass.getCurrentAttendance());
    }
    
    @Test
    public void testHasAvailableSpots() {
        FitnessClass fitnessClass = new FitnessClass("Test Class", ClassType.YOGA, 2, 
                                                     LocalDateTime.now().plusHours(2), 60);
        
        // Initially should have spots
        assertTrue(fitnessClass.hasAvailableSpots());
        
        // Add attendees
        fitnessClass.incrementCurrentAttendance();
        assertTrue(fitnessClass.hasAvailableSpots());
        
        fitnessClass.incrementCurrentAttendance();
        assertFalse(fitnessClass.hasAvailableSpots());
    }
    
    @Test
    public void testHasTimeConflict() {
        LocalDateTime baseTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0);
        
        // Class from 12:00 to 13:00
        FitnessClass class1 = new FitnessClass("Class 1", ClassType.YOGA, 10, baseTime, 60);
        
        // Class from 11:30 to 12:30 (overlaps with class1)
        FitnessClass class2 = new FitnessClass("Class 2", ClassType.DANCE, 10, baseTime.minusMinutes(30), 60);
        
        // Class from 12:30 to 13:30 (overlaps with class1)
        FitnessClass class3 = new FitnessClass("Class 3", ClassType.CARDIO, 10, baseTime.plusMinutes(30), 60);
        
        // Class from 13:00 to 14:00 (starts exactly when class1 ends, no overlap)
        FitnessClass class4 = new FitnessClass("Class 4", ClassType.CROSSFIT, 10, baseTime.plusMinutes(60), 60);
        
        // Class from 11:00 to 12:00 (ends exactly when class1 starts, no overlap)
        FitnessClass class5 = new FitnessClass("Class 5", ClassType.GYM, 10, baseTime.minusMinutes(60), 60);
        
        // Class from 10:00 to 11:00 (completely before class1)
        FitnessClass class6 = new FitnessClass("Class 6", ClassType.PILATES, 10, baseTime.minusMinutes(120), 60);
        
        // Verify conflicts
        assertTrue(class1.hasTimeConflict(class2));
        assertTrue(class2.hasTimeConflict(class1)); // Symmetric test
        
        assertTrue(class1.hasTimeConflict(class3));
        assertTrue(class3.hasTimeConflict(class1)); // Symmetric test
        
        assertFalse(class1.hasTimeConflict(class4));
        assertFalse(class4.hasTimeConflict(class1)); // Symmetric test
        
        assertFalse(class1.hasTimeConflict(class5));
        assertFalse(class5.hasTimeConflict(class1)); // Symmetric test
        
        assertFalse(class1.hasTimeConflict(class6));
        assertFalse(class6.hasTimeConflict(class1)); // Symmetric test
    }
}