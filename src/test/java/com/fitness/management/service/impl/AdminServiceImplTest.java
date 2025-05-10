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
    
    @Test
    public void testGetAllClasses() {
        // Create multiple classes
        LocalDateTime futureTime1 = LocalDateTime.now().plusHours(3);
        LocalDateTime futureTime2 = LocalDateTime.now().plusHours(5);
        LocalDateTime futureTime3 = LocalDateTime.now().plusHours(7);
        
        FitnessClass class1 = adminService.createClass("Class 1", ClassType.YOGA, 10, futureTime1, 60);
        FitnessClass class2 = adminService.createClass("Class 2", ClassType.DANCE, 15, futureTime2, 45);
        FitnessClass class3 = adminService.createClass("Class 3", ClassType.YOGA, 8, futureTime3, 30);
        
        // Cancel one class
        adminService.cancelClass(class2);
        
        // Get all classes
        List<FitnessClass> allClasses = adminService.getAllClasses();
        
        // Verify all classes are returned, including cancelled ones
        assertEquals(3, allClasses.size());
        assertTrue(allClasses.stream().anyMatch(c -> c.getId().equals(class1.getId())));
        assertTrue(allClasses.stream().anyMatch(c -> c.getId().equals(class2.getId())));
        assertTrue(allClasses.stream().anyMatch(c -> c.getId().equals(class3.getId())));
    }
    
    @Test
    public void testGetActiveClasses() {
        // Create multiple classes
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        LocalDateTime futureTime1 = LocalDateTime.now().plusHours(3);
        LocalDateTime futureTime2 = LocalDateTime.now().plusHours(5);
        
        // This would normally throw an exception, but we'll directly create the object for testing
        FitnessClass pastClass = new FitnessClass("Past Class", ClassType.YOGA, 10, pastTime, 60);
        mockFitnessClassRepository.save(pastClass);
        
        FitnessClass activeClass1 = adminService.createClass("Active 1", ClassType.DANCE, 15, futureTime1, 45);
        FitnessClass activeClass2 = adminService.createClass("Active 2", ClassType.YOGA, 8, futureTime2, 30);
        
        // Cancel one active class
        adminService.cancelClass(activeClass1);
        
        // Get active classes
        List<FitnessClass> activeClasses = adminService.getActiveClasses();
        
        // Verify only future and non-cancelled classes are returned
        assertEquals(1, activeClasses.size());
        assertTrue(activeClasses.stream().anyMatch(c -> c.getId().equals(activeClass2.getId())));
        assertFalse(activeClasses.stream().anyMatch(c -> c.getId().equals(activeClass1.getId())));
        assertFalse(activeClasses.stream().anyMatch(c -> c.getId().equals(pastClass.getId())));
    }
}
