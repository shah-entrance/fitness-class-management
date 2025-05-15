package com.fitness.management.repository.impl.mysql;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.repository.FitnessClassRepository;

import java.util.Optional;

public class MySQLFitnessClassRepository implements FitnessClassRepository {
    
    @Override
    public FitnessClass save(FitnessClass fitnessClass) {
        throw new MySQLNotImplementedException("Saving fitness class to MySQL is not implemented yet");
    }
    
    @Override
    public Optional<FitnessClass> findById(String id) {
        throw new MySQLNotImplementedException("Finding fitness class by ID in MySQL is not implemented yet");
    }
}