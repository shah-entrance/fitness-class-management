package com.fitness.repository.impl.memory;

import com.fitness.model.Booking;
import com.fitness.model.FitnessClass;
import com.fitness.model.User;
import com.fitness.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {
    // Using ConcurrentHashMap for thread safety
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    
    @Override
    public synchronized Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }
    
    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }
    
    @Override
    public List<Booking> findAll() {
        // Create a copy to avoid ConcurrentModificationException
        return new ArrayList<>(bookings.values());
    }
    
    @Override
    public List<Booking> findByUser(User user) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .filter(booking -> !booking.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findByFitnessClass(FitnessClass fitnessClass) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(booking -> !booking.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        // Create a copy of values first to avoid ConcurrentModificationException
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .filter(booking -> booking.getFitnessClass().getId().equals(fitnessClass.getId()))
                .findFirst();
    }
    
    @Override
    public synchronized void delete(String id) {
        bookings.remove(id);
    }
}