package com.fitness.management.repository.impl.mysql;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.WaitlistEntry;
import com.fitness.management.repository.WaitlistRepository;

import java.util.List;
import java.util.Optional;

public class MySQLWaitlistRepository implements WaitlistRepository {
    
    @Override
    public WaitlistEntry save(WaitlistEntry waitlistEntry) {
        throw new MySQLNotImplementedException("Saving waitlist entry to MySQL is not implemented yet");
    }
    
    @Override
    public Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass) {
        throw new MySQLNotImplementedException("Finding first waitlist entry by fitness class in MySQL is not implemented yet");
    }
    
    @Override
    public Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        throw new MySQLNotImplementedException("Finding waitlist entry by user and fitness class in MySQL is not implemented yet");
    }
}