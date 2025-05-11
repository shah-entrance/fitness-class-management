package com.fitness.management.service.impl;

import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.service.AdminService;
import com.fitness.management.service.mock.MockFitnessClassRepository;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class AdminServiceImplTest {

    private AdminService adminService;
    private MockFitnessClassRepository mockFitnessClassRepository;
    
    @Before
    public void setUp() throws Exception {
        // Create AdminServiceImpl instance
        adminService = new AdminServiceImpl();
        
        // Create mock repository
        mockFitnessClassRepository = new MockFitnessClassRepository();
        
        // Set private repository field using reflection
        Field fitnessClassRepoField = AdminServiceImpl.class.getDeclaredField("fitnessClassRepository");
        fitnessClassRepoField.setAccessible(true);
        fitnessClassRepoField.set(adminService, mockFitnessClassRepository);
        
        // Clear any existing data
        mockFitnessClassRepository.clear();
    }
    
    @Test
    public void testCreateClass_Success() {
        // Get a future time for class
        LocalDateTime futureTime = LocalDateTime.now().plusHours(2);
        
        // Create a class
        FitnessClass fitnessClass = adminService.createClass(
            "Test Yoga", ClassType.YOGA, 15, futureTime, 60);
        
        // Verify class was created with correct data
        assertNotNull(fitnessClass);
        assertEquals("Test Yoga", fitnessClass.getName());
        assertEquals(ClassType.YOGA, fitnessClass.getClassType());
        assertEquals(15, fitnessClass.getCapacity());
        assertEquals(futureTime, fitnessClass.getStartTime());
        assertEquals(60, fitnessClass.getDurationMinutes());
        assertFalse(fitnessClass.isCancelled());
        assertEquals(0, fitnessClass.getCurrentAttendance());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateClass_InvalidCapacity() {
        // Try to create a class with invalid capacity
        adminService.createClass(
            "Test Class", ClassType.DANCE, 0, LocalDateTime.now().plusHours(3), 45);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateClass_InvalidDuration() {
        // Try to create a class with invalid duration
        adminService.createClass(
            "Test Class", ClassType.GYM, 10, LocalDateTime.now().plusHours(2), 0);
    }
    
    @Test
    public void testCancelClass() {
        // Create a class
        LocalDateTime futureTime = LocalDateTime.now().plusHours(5);
        FitnessClass fitnessClass = adminService.createClass(
            "To Cancel", ClassType.DANCE, 8, futureTime, 50);
        
        // Verify not cancelled initially
        assertFalse(fitnessClass.isCancelled());
        
        // Cancel the class
        adminService.cancelClass(fitnessClass);
        
        // Verify class was cancelled
        assertTrue(fitnessClass.isCancelled());
    }
}
