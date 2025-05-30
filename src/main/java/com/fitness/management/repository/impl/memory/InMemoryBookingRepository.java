package com.fitness.management.repository.impl.memory;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    
    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }
    
    @Override
    public List<Booking> findByUser(User user) {
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .filter(booking -> !booking.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findByFitnessClass(FitnessClass fitnessClass) {
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getFitnessClass().getId().equals(fitnessClass.getId()))
                .filter(booking -> !booking.isCancelled())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        return new ArrayList<>(bookings.values()).stream()
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .filter(booking -> booking.getFitnessClass().getId().equals(fitnessClass.getId()))
                .findFirst();
    }
}