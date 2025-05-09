package com.fitness.repository.impl.memory;

import com.fitness.model.FitnessClass;
import com.fitness.model.User;
import com.fitness.model.WaitlistEntry;
import com.fitness.repository.WaitlistRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryWaitlistRepository implements WaitlistRepository {
    private final Map<String, WaitlistEntry> waitlistEntries = new ConcurrentHashMap<>();
    
    @Override
    public synchronized WaitlistEntry save(WaitlistEntry waitlistEntry) {
        waitlistEntries.put(waitlistEntry.getId(), waitlistEntry);
        return waitlistEntry;
    }
    
    @Override
    public Optional<WaitlistEntry> findById(String id) {
        return Optional.ofNullable(waitlistEntries.get(id));
    }
    
    @Override
    public List<WaitlistEntry> findAll() {
        return new ArrayList<>(waitlistEntries.values());
    }
    
    @Override
    public List<WaitlistEntry> findByFitnessClass(FitnessClass fitnessClass) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(entry -> !entry.isProcessed())
                .sorted(Comparator.comparing(WaitlistEntry::getEntryTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(entry -> !entry.isProcessed())
                .min(Comparator.comparing(WaitlistEntry::getEntryTime));
    }
    
    @Override
    public Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getUser().getId().equals(user.getId()))
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .findFirst();
    }
    
    @Override
    public synchronized void delete(String id) {
        waitlistEntries.remove(id);
    }
}