package com.fitness.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final String id;
    private String username;
    private String password;
    private UserType userType;
    private AtomicInteger activeBookingsCount; // Track number of active bookings
    
    public User(String username, String password, UserType userType) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.activeBookingsCount = new AtomicInteger(0);
    }
    
    public String getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public int getBookingLimit() {
        return userType.getBookingLimit();
    }
    
    /**
     * Get the number of active bookings for this user
     * @return Current number of active bookings
     */
    public int getActiveBookingsCount() {
        return activeBookingsCount.get();
    }
    
    /**
     * Get the number of remaining bookings available to this user
     * @return Number of remaining bookings
     */
    public int getRemainingBookings() {
        return Math.max(0, getBookingLimit() - activeBookingsCount.get());
    }
    
    /**
     * Increment the active bookings count for this user
     * @return The updated active bookings count
     */
    public int incrementActiveBookings() {
        return activeBookingsCount.incrementAndGet();
    }
    
    /**
     * Decrement the active bookings count for this user
     * @return The updated active bookings count
     */
    public int decrementActiveBookings() {
        return activeBookingsCount.updateAndGet(val -> Math.max(0, val - 1));
    }
    
    /**
     * Check if the user has reached their booking limit
     * @return true if the user has reached their booking limit, false otherwise
     */
    public boolean hasReachedBookingLimit() {
        return activeBookingsCount.get() >= getBookingLimit();
    }
}