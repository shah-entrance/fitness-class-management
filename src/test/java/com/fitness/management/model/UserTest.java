package com.fitness.management.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {
    
    @Test
    public void testConstructor() {
        User user = new User("testuser", "password123", UserType.GOLD);
        
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(UserType.GOLD, user.getUserType());
        assertEquals(5, user.getBookingLimit());  // GOLD tier has 5 booking limit
    }
    
    @Test
    public void testSetters() {
        User user = new User("originalname", "originalpass", UserType.SILVER);
        
        user.setUsername("newname");
        user.setPassword("newpass");
        user.setUserType(UserType.PLATINUM);
        
        assertEquals("newname", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals(UserType.PLATINUM, user.getUserType());
        assertEquals(10, user.getBookingLimit());  // PLATINUM tier has 10 booking limit
    }
    
    @Test
    public void testBookingLimits() {
        User silverUser = new User("silver", "pass", UserType.SILVER);
        User goldUser = new User("gold", "pass", UserType.GOLD);
        User platinumUser = new User("platinum", "pass", UserType.PLATINUM);
        User adminUser = new User("admin", "pass", UserType.ADMIN);
        
        assertEquals(3, silverUser.getBookingLimit());    // SILVER tier has 3 booking limit
        assertEquals(5, goldUser.getBookingLimit());      // GOLD tier has 5 booking limit
        assertEquals(10, platinumUser.getBookingLimit()); // PLATINUM tier has 10 booking limit
        assertEquals(0, adminUser.getBookingLimit());     // ADMIN has no booking limit (0)
    }
}