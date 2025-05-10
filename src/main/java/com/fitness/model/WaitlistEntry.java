package com.fitness.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class WaitlistEntry {
    private final String id;
    private User user;
    private FitnessClass fitnessClass;
    private LocalDateTime entryTime;
    private boolean processed;
    
    public WaitlistEntry(User user, FitnessClass fitnessClass) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.fitnessClass = fitnessClass;
        this.entryTime = LocalDateTime.now();
        this.processed = false;
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
    
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    
    public boolean isProcessed() {
        return processed;
    }
    
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}