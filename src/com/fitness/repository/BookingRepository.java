package com.fitness.repository;

import com.fitness.model.Booking;
import com.fitness.model.FitnessClass;
import com.fitness.model.User;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
    List<Booking> findByUser(User user);
    List<Booking> findByFitnessClass(FitnessClass fitnessClass);
    Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass);
    void delete(String id);
}