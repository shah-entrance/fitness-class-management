package com.fitness.management.service.impl;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.ClassType;
import com.fitness.management.repository.BookingRepository;
import com.fitness.management.repository.FitnessClassRepository;
import com.fitness.management.repository.RepositoryFactory;
import com.fitness.management.service.AdminService;
import com.fitness.management.util.ConcurrencyUtils;

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
        
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        
        FitnessClass fitnessClass = new FitnessClass(name, classType, capacity, startTime, durationMinutes);
        return fitnessClassRepository.save(fitnessClass);
    }
    
    @Override
    public void cancelClass(FitnessClass fitnessClass) {
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
}