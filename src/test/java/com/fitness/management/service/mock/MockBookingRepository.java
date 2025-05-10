package com.fitness.management.service.mock;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockBookingRepository implements BookingRepository {
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public Booking save(Booking booking) {
        // Remove existing booking if found
        bookings.removeIf(b -> 
            b.getUser().getId().equals(booking.getUser().getId()) && 
            b.getFitnessClass().getId().equals(booking.getFitnessClass().getId())
        );
        // Add new booking
        bookings.add(booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return bookings.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }

    @Override
    public List<Booking> findByUser(User user) {
        return bookings.stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByFitnessClass(FitnessClass fitnessClass) {
        return bookings.stream()
                .filter(b -> b.getFitnessClass().getId().equals(fitnessClass.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        return bookings.stream()
                .filter(b -> 
                    b.getUser().getId().equals(user.getId()) && 
                    b.getFitnessClass().getId().equals(fitnessClass.getId())
                )
                .findFirst();
    }

    @Override
    public void delete(String id) {
        bookings.removeIf(b -> b.getId().equals(id));
    }

    public void clear() {
        bookings.clear();
    }
}