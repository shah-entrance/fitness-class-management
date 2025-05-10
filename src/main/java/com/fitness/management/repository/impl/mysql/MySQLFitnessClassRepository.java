package com.fitness.management.repository.impl.mysql;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.repository.FitnessClassRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    
    @Override
    public List<FitnessClass> findAll() {
        throw new MySQLNotImplementedException("Finding all fitness classes in MySQL is not implemented yet");
    }

    @Override
    public List<FitnessClass> findAllActiveAfter(LocalDateTime time) {
        throw new MySQLNotImplementedException("Finding all active fitness classes after time in MySQL is not implemented yet");
    }
    
    @Override
    public void delete(String id) {
        throw new MySQLNotImplementedException("Deleting fitness class in MySQL is not implemented yet");
    }
}