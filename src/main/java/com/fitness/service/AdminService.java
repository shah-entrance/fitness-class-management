package com.fitness.service;

import com.fitness.model.FitnessClass;
import com.fitness.model.ClassType;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    FitnessClass createClass(String name, ClassType classType, int capacity, LocalDateTime startTime, int durationMinutes);
    void cancelClass(FitnessClass fitnessClass);
    List<FitnessClass> getAllClasses();
    List<FitnessClass> getActiveClasses();
}