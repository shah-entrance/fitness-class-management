package com.fitness.management.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {
    private final String id;
    private User user;
    private FitnessClass fitnessClass;
    private LocalDateTime bookingTime;
    private boolean cancelled;
    
    public Booking(User user, FitnessClass fitnessClass) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.fitnessClass = fitnessClass;
        this.bookingTime = LocalDateTime.now();
        this.cancelled = false;
    }
    
    public String getId() {
        return id;
    }
    
    public User getUser() {
        return user;
    }
    
    public FitnessClass getFitnessClass() {
        return fitnessClass;
    }
    
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}