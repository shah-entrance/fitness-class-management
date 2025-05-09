package com.fitness.util;

import com.fitness.model.FitnessClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class for managing concurrency in the booking system.
 * Provides locks to ensure thread-safe operations on fitness classes.
 */
public class ConcurrencyUtils {
    
    // Store a lock for each fitness class to prevent race conditions
    private static final Map<String, Lock> fitnessClassLocks = new ConcurrentHashMap<>();
    
    /**
     * Gets a lock for a specific fitness class.
     * This ensures that operations on the same fitness class are thread-safe.
     * 
     * @param fitnessClass The fitness class to get a lock for
     * @return A lock specific to the given fitness class
     */
    public static Lock getLockForFitnessClass(FitnessClass fitnessClass) {
        String classId = fitnessClass.getId();
        // Atomically get or create a lock for this fitness class
        return fitnessClassLocks.computeIfAbsent(classId, k -> new ReentrantLock());
    }
    
    /**
     * Clears all locks. Useful for testing.
     */
    public static void clearLocks() {
        fitnessClassLocks.clear();
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ConcurrencyUtils() {
        // Utility class should not be instantiated
    }
}