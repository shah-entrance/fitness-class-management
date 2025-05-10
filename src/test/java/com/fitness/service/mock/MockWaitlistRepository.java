package com.fitness.service.mock;

import com.fitness.model.FitnessClass;
import com.fitness.model.User;
import com.fitness.model.WaitlistEntry;
import com.fitness.repository.WaitlistRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockWaitlistRepository implements WaitlistRepository {
    private List<WaitlistEntry> waitlistEntries = new ArrayList<>();

    @Override
    public WaitlistEntry save(WaitlistEntry entry) {
        // Remove existing entry if found
        waitlistEntries.removeIf(e -> 
            e.getUser().getId().equals(entry.getUser().getId()) && 
            e.getFitnessClass().getId().equals(entry.getFitnessClass().getId())
        );
        // Add new entry
        waitlistEntries.add(entry);
        return entry;
    }

    @Override
    public Optional<WaitlistEntry> findById(String id) {
        return waitlistEntries.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<WaitlistEntry> findAll() {
        return new ArrayList<>(waitlistEntries);
    }

    @Override
    public List<WaitlistEntry> findByFitnessClass(FitnessClass fitnessClass) {
        return waitlistEntries.stream()
                .filter(e -> e.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(e -> !e.isProcessed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WaitlistEntry> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        return waitlistEntries.stream()
                .filter(e -> 
                    e.getUser().getId().equals(user.getId()) && 
                    e.getFitnessClass().getId().equals(fitnessClass.getId())
                )
                .findFirst();
    }

    @Override
    public Optional<WaitlistEntry> findFirstByFitnessClass(FitnessClass fitnessClass) {
        return waitlistEntries.stream()
                .filter(e -> e.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(e -> !e.isProcessed())
                .sorted(Comparator.comparing(WaitlistEntry::getEntryTime))
                .findFirst();
    }

    @Override
    public void delete(String id) {
        waitlistEntries.removeIf(e -> e.getId().equals(id));
    }

    public void clear() {
        waitlistEntries.clear();
    }
}