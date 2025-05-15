package com.fitness.management.repository;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.WaitlistEntry;

import java.util.Optional;

public interface WaitlistRepository {
    WaitlistEntry save(WaitlistEntry waitlistEntry);
    Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass);
    Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass);
}