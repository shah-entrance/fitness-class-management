package com.fitness.management.repository.impl.memory;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.repository.FitnessClassRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryFitnessClassRepository implements FitnessClassRepository {
    private final Map<String, FitnessClass> fitnessClasses = new HashMap<>();
    
    @Override
    public FitnessClass save(FitnessClass fitnessClass) {
        fitnessClasses.put(fitnessClass.getId(), fitnessClass);
        return fitnessClass;
    }
    
    @Override
    public Optional<FitnessClass> findById(String id) {
        return Optional.ofNullable(fitnessClasses.get(id));
    }
}