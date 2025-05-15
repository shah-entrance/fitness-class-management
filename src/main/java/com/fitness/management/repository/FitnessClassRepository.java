package com.fitness.management.repository;

import com.fitness.management.model.FitnessClass;

import java.util.Optional;

public interface FitnessClassRepository {
    FitnessClass save(FitnessClass fitnessClass);
    Optional<FitnessClass> findById(String id);
}