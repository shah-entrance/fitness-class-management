package com.fitness.service.impl;

import com.fitness.model.Booking;
import com.fitness.model.FitnessClass;
import com.fitness.model.ClassType;
import com.fitness.repository.BookingRepository;
import com.fitness.repository.FitnessClassRepository;
import com.fitness.repository.RepositoryFactory;
import com.fitness.service.AdminService;
import com.fitness.util.ConcurrencyUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class AdminServiceImpl implements AdminService {
    private final FitnessClassRepository fitnessClassRepository;
    private final BookingRepository bookingRepository;
    
    public AdminServiceImpl() {
        this.fitnessClassRepository = RepositoryFactory.createFitnessClassRepository();
        this.bookingRepository = RepositoryFactory.createBookingRepository();
    }
    
    // For testing - allows injecting mock repositories
    public AdminServiceImpl(FitnessClassRepository fitnessClassRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.bookingRepository = RepositoryFactory.createBookingRepository();
    }
    
    // For testing - allows injecting both mock repositories
    public AdminServiceImpl(FitnessClassRepository fitnessClassRepository, BookingRepository bookingRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public FitnessClass createClass(String name, ClassType classType, int capacity, LocalDateTime startTime, int durationMinutes) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }
        
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        
        FitnessClass fitnessClass = new FitnessClass(name, classType, capacity, startTime, durationMinutes);
        return fitnessClassRepository.save(fitnessClass);
    }
    
    @Override
    public void cancelClass(FitnessClass fitnessClass) {
        // Get lock for this fitness class to ensure thread safety
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(fitnessClass);
        
        try {
            lock.lock();
            
            // Get all active bookings for this class
            List<Booking> classBookings = bookingRepository.findByFitnessClass(fitnessClass);
            
            // Restore booking limits for all users with active bookings
            for (Booking booking : classBookings) {
                if (!booking.isCancelled()) {
                    // Mark the booking as cancelled
                    booking.setCancelled(true);
                    bookingRepository.save(booking);
                    
                    // Restore the user's booking limit by decrementing their active bookings count
                    booking.getUser().decrementActiveBookings();
                    
                    System.out.println("Restored booking limit for user: " + booking.getUser().getUsername());
                }
            }
            
            // Mark the class as cancelled
            fitnessClass.setCancelled(true);
            fitnessClassRepository.save(fitnessClass);
            
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public List<FitnessClass> getAllClasses() {
        return fitnessClassRepository.findAll();
    }
    
    @Override
    public List<FitnessClass> getActiveClasses() {
        return fitnessClassRepository.findAllActiveAfter(LocalDateTime.now());
    }
}