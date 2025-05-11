package com.fitness.management.repository.impl.memory;

import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.WaitlistEntry;
import com.fitness.management.repository.WaitlistRepository;

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
    public WaitlistEntry save(WaitlistEntry waitlistEntry) {
        waitlistEntries.put(waitlistEntry.getId(), waitlistEntry);
        return waitlistEntry;
    }
    
    @Override
    public List<WaitlistEntry> findByFitnessClass(FitnessClass fitnessClass) {
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(entry -> !entry.isProcessed())
                .sorted(Comparator.comparing(WaitlistEntry::getEntryTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass) {
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(entry -> !entry.isProcessed())
                .min(Comparator.comparing(WaitlistEntry::getEntryTime));
    }
    
    @Override
    public Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        return new ArrayList<>(waitlistEntries.values()).stream()
                .filter(entry -> entry.getUser().getId().equals(user.getId()))
                .filter(entry -> entry.getFitnessClass().getId().equals(fitnessClass.getId()))
                .findFirst();
    }
}