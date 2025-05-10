package com.fitness.management.repository.impl.mysql;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.repository.BookingRepository;

import java.util.List;
import java.util.Optional;

public class MySQLBookingRepository implements BookingRepository {
    
    @Override
    public Booking save(Booking booking) {
        throw new MySQLNotImplementedException("Saving booking to MySQL is not implemented yet");
    }
    
    @Override
    public Optional<Booking> findById(String id) {
        throw new MySQLNotImplementedException("Finding booking by ID in MySQL is not implemented yet");
    }
    
    @Override
    public List<Booking> findAll() {
        throw new MySQLNotImplementedException("Finding all bookings in MySQL is not implemented yet");
    }
    
    @Override
    public List<Booking> findByUser(User user) {
        throw new MySQLNotImplementedException("Finding bookings by user in MySQL is not implemented yet");
    }
    
    @Override
    public List<Booking> findByFitnessClass(FitnessClass fitnessClass) {
        throw new MySQLNotImplementedException("Finding bookings by fitness class in MySQL is not implemented yet");
    }
    
    @Override
    public Optional<Booking> findByUserAndFitnessClass(User user, FitnessClass fitnessClass) {
        throw new MySQLNotImplementedException("Finding booking by user and fitness class in MySQL is not implemented yet");
    }
    
    @Override
    public void delete(String id) {
        throw new MySQLNotImplementedException("Deleting booking in MySQL is not implemented yet");
    }
}
