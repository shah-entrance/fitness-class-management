package com.fitness.management.repository;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    List<Booking> findByUser(User user);
    List<Booking> findByFitnessClass(FitnessClass fitnessClass);
    Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass);
    void delete(String id);
}