package com.fitness.management.service;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.ClassType;

import java.time.LocalDateTime;

public interface AdminService {
    FitnessClass createClass(String name, ClassType classType, int capacity, LocalDateTime startTime, int durationMinutes);
    void cancelClass(FitnessClass fitnessClass);
}