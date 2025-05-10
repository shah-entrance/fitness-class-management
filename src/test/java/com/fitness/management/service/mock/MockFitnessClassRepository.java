package com.fitness.management.service.mock;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.repository.FitnessClassRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockFitnessClassRepository implements FitnessClassRepository {
    private List<FitnessClass> fitnessClasses = new ArrayList<>();

    @Override
    public FitnessClass save(FitnessClass fitnessClass) {
        // Remove existing class if found
        fitnessClasses.removeIf(fc -> fc.getId().equals(fitnessClass.getId()));
        // Add new class
        fitnessClasses.add(fitnessClass);
        return fitnessClass;
    }

    @Override
    public Optional<FitnessClass> findById(String id) {
        return fitnessClasses.stream()
                .filter(fc -> fc.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public List<FitnessClass> findAll() {
        return new ArrayList<>(fitnessClasses);
    }
    
    @Override
    public List<FitnessClass> findAllActive() {
        return fitnessClasses.stream()
                .filter(fitnessClass -> !fitnessClass.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FitnessClass> findAllActiveAfter(LocalDateTime time) {
        return fitnessClasses.stream()
                .filter(fitnessClass -> !fitnessClass.isCancelled())
                .filter(fitnessClass -> fitnessClass.getStartTime().isAfter(time))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        fitnessClasses.removeIf(fc -> fc.getId().equals(id));
    }

    public void clear() {
        fitnessClasses.clear();
    }
}