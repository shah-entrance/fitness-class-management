package com.fitness.management.repository;

import com.fitness.management.model.FitnessClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FitnessClassRepository {
    FitnessClass save(FitnessClass fitnessClass);
    Optional<FitnessClass> findById(String id);
    List<FitnessClass> findAll();
    List<FitnessClass> findAllActive();
    List<FitnessClass> findAllActiveAfter(LocalDateTime time);
    void delete(String id);
}