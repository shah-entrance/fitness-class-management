package com.fitness.repository;

import com.fitness.model.FitnessClass;
import com.fitness.model.User;
import com.fitness.model.WaitlistEntry;

import java.util.List;
import java.util.Optional;

public interface WaitlistRepository {
    WaitlistEntry save(WaitlistEntry waitlistEntry);
    Optional<WaitlistEntry> findById(String id);
    List<WaitlistEntry> findAll();
    List<WaitlistEntry> findByFitnessClass(FitnessClass fitnessClass);
    Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass);
    Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass);
    void delete(String id);
}