package com.fitness.management.util;

import com.fitness.management.model.FitnessClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyUtils {
    
    private static final Map<String, Lock> fitnessClassLocks = new ConcurrentHashMap<>();
    
    public static Lock getLockForFitnessClass(FitnessClass fitnessClass) {
        String classId = fitnessClass.getId();
        return fitnessClassLocks.computeIfAbsent(classId, k -> new ReentrantLock());
    }
    
    private ConcurrencyUtils() {
    }
}