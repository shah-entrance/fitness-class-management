package com.fitness.management.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final String id;
    private String username;
    private String password;
    private UserType userType;
    private AtomicInteger activeBookingsCount;
    
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

    public int getActiveBookingsCount() {
        return activeBookingsCount.get();
    }

    public int getRemainingBookings() {
        return Math.max(0, getBookingLimit() - activeBookingsCount.get());
    }
    
    public int incrementActiveBookings() {
        return activeBookingsCount.incrementAndGet();
    }
    
    public int decrementActiveBookings() {
        return activeBookingsCount.updateAndGet(val -> Math.max(0, val - 1));
    }

    public boolean hasReachedBookingLimit() {
        return activeBookingsCount.get() >= getBookingLimit();
    }
}