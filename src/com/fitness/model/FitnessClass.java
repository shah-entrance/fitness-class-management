package com.fitness.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import com.fitness.util.ConcurrencyUtils;

public class FitnessClass {
    private final String id;
    private String name;
    private ClassType classType;
    private volatile int capacity;
    private volatile int currentAttendance; // Made volatile for visibility across threads
    private LocalDateTime startTime;
    private int durationMinutes;
    private boolean cancelled;
    
    public FitnessClass(String name, ClassType classType, int capacity, LocalDateTime startTime, int durationMinutes) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.classType = classType;
        this.capacity = capacity;
        this.currentAttendance = 0; // Initialize to zero
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.cancelled = false;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ClassType getClassType() {
        return classType;
    }
    
    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(this);
        lock.lock();
        try {
            this.capacity = capacity;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCurrentAttendance() {
        return currentAttendance;
    }
    
    public void setCurrentAttendance(int currentAttendance) {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(this);
        lock.lock();
        try {
            this.currentAttendance = currentAttendance;
        } finally {
            lock.unlock();
        }
    }
    
    public void incrementCurrentAttendance() {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(this);
        lock.lock();
        try {
            this.currentAttendance++;
        } finally {
            lock.unlock();
        }
    }
    
    public void decrementCurrentAttendance() {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(this);
        lock.lock();
        try {
            if (this.currentAttendance > 0) {
                this.currentAttendance--;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public boolean hasAvailableSpots() {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(this);
        lock.lock();
        try {
            return currentAttendance < capacity;
        } finally {
            lock.unlock();
        }
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean hasTimeConflict(FitnessClass other) {
        if (this.startTime.isBefore(other.getEndTime()) && 
            other.startTime.isBefore(this.getEndTime())) {
            return true;
        }
        return false;
    }
}