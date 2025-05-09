package com.fitness.service.impl;

import com.fitness.model.User;
import com.fitness.model.UserType;
import com.fitness.service.AuthService;
import com.fitness.service.impl.AuthServiceImpl;
import com.fitness.service.mock.MockUserRepository;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AuthServiceImplTest {

    private AuthService authService;
    private MockUserRepository mockUserRepository;

    @Before
    public void setUp() throws Exception {
        // Create AuthServiceImpl instance
        authService = new AuthServiceImpl();
        
        // Create mock repository
        mockUserRepository = new MockUserRepository();
        
        // Set private repository field using reflection
        Field userRepoField = AuthServiceImpl.class.getDeclaredField("userRepository");
        userRepoField.setAccessible(true);
        userRepoField.set(authService, mockUserRepository);
        
        // Clear any existing data
        mockUserRepository.clear();
    }

    @Test
    public void testRegisterUser_Success() {
        // Register a new user
        User user = authService.registerUser("testuser", "password123", UserType.GOLD);
        
        // Verify user was created with correct data
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(UserType.GOLD, user.getUserType());
        assertEquals(5, user.getBookingLimit()); // GOLD tier has 5 booking limit
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_DuplicateUsername() {
        // Register a user
        authService.registerUser("duplicate", "password123", UserType.SILVER);
        
        // Try to register again with same username
        authService.registerUser("duplicate", "different", UserType.PLATINUM);
    }
    
    @Test
    public void testLogin_Success() {
        // Register a user
        authService.registerUser("logintest", "correctpass", UserType.PLATINUM);
        
        // Attempt to login
        User loggedInUser = authService.login("logintest", "correctpass");
        
        // Verify login successful
        assertNotNull(loggedInUser);
        assertEquals("logintest", loggedInUser.getUsername());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLogin_WrongPassword() {
        // Register a user
        authService.registerUser("passwordtest", "correctpass", UserType.SILVER);
        
        // Attempt to login with wrong password
        authService.login("passwordtest", "wrongpass");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLogin_UserNotFound() {
        // Attempt to login with non-existent username
        authService.login("nonexistent", "anypassword");
    }
    
    @Test
    public void testIsAdmin() {
        // Register admin and regular users
        User adminUser = authService.registerUser("admin", "adminpass", UserType.ADMIN);
        User regularUser = authService.registerUser("regular", "regularpass", UserType.GOLD);
        
        // Verify admin check works correctly
        assertTrue(authService.isAdmin(adminUser));
        assertFalse(authService.isAdmin(regularUser));
    }
}