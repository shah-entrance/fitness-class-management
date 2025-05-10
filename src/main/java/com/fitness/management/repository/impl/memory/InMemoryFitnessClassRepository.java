package com.fitness.management.repository.impl.memory;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.repository.FitnessClassRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    @Override
    public List<FitnessClass> findAll() {
        return new ArrayList<>(fitnessClasses.values());
    }
    
    @Override
    public List<FitnessClass> findAllActive() {
        return fitnessClasses.values().stream()
                .filter(fitnessClass -> !fitnessClass.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FitnessClass> findAllActiveAfter(LocalDateTime time) {
        return fitnessClasses.values().stream()
                .filter(fitnessClass -> !fitnessClass.isCancelled())
                .filter(fitnessClass -> fitnessClass.getStartTime().isAfter(time))
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(String id) {
        fitnessClasses.remove(id);
    }
}