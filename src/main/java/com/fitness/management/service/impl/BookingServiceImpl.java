package com.fitness.management.service.impl;

import com.fitness.management.exception.BookingLimitExceededException;
import com.fitness.management.exception.ClassFullException;
import com.fitness.management.exception.TimeConflictException;
import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.model.WaitlistEntry;
import com.fitness.management.repository.BookingRepository;
import com.fitness.management.repository.FitnessClassRepository;
import com.fitness.management.repository.RepositoryFactory;
import com.fitness.management.repository.WaitlistRepository;
import com.fitness.management.service.BookingService;
import com.fitness.management.util.ConcurrencyUtils;
import com.fitness.management.util.ValidationUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final FitnessClassRepository fitnessClassRepository;
    
    public BookingServiceImpl() {
        this.bookingRepository = RepositoryFactory.createBookingRepository();
        this.waitlistRepository = RepositoryFactory.createWaitlistRepository();
        this.fitnessClassRepository = RepositoryFactory.createFitnessClassRepository();
    }
    
    @Override
    public Booking bookClass(User user, FitnessClass fitnessClass) {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(fitnessClass);
        
        try {
            lock.lock();
            
            // Check if class is cancelled
            if (fitnessClass.isCancelled()) {
                throw new IllegalStateException("Cannot book a cancelled class");
            }
            
            // Check if user has already booked this class
            Optional<Booking> existingBooking = bookingRepository.findByUserAndFitnessClass(user, fitnessClass);
            if (existingBooking.isPresent() && !existingBooking.get().isCancelled()) {
                throw new IllegalStateException("User has already booked this class");
            }
            
            // Check booking limit for user - using the new hasReachedBookingLimit method
            if (user.hasReachedBookingLimit()) {
                throw new BookingLimitExceededException("User has reached the booking limit of " + user.getBookingLimit());
            }
            
            // Check for time conflicts
            List<Booking> userBookings = bookingRepository.findByUser(user);
            for (Booking booking : userBookings) {
                if (!booking.isCancelled() && booking.getFitnessClass().hasTimeConflict(fitnessClass)) {
                    throw new TimeConflictException("Time conflict with existing booking: " + booking.getFitnessClass().getName());
                }
            }
            
            // Reload fitness class to get the most up-to-date state
            Optional<FitnessClass> refreshedClassOpt = fitnessClassRepository.findById(fitnessClass.getId());
            FitnessClass refreshedClass = refreshedClassOpt.orElse(fitnessClass);
            
            // Check if class is full - using the refreshed class data
            if (refreshedClass.getCurrentAttendance() >= refreshedClass.getCapacity()) {
                WaitlistEntry waitlistEntry = addToWaitlist(user, refreshedClass);
                System.out.println("Class is full. User added to waitlist position: " + waitlistEntry.getId());
                throw new ClassFullException("Class is full: " + refreshedClass.getName() + ". You have been added to the waitlist.");
            }
            
            // Create and save booking
            Booking booking = new Booking(user, refreshedClass);
            Booking savedBooking = bookingRepository.save(booking);
            
            // Update the fitness class in the repository
            refreshedClass.incrementCurrentAttendance();
            fitnessClassRepository.save(refreshedClass);
            
            // Increment user's active bookings count
            user.incrementActiveBookings();
            
            return savedBooking;
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void cancelBooking(User user, FitnessClass fitnessClass) {
        Lock lock = ConcurrencyUtils.getLockForFitnessClass(fitnessClass);
        
        try {
            lock.lock();
            
            // Find the user's booking for this class
            Optional<Booking> bookingOpt = bookingRepository.findByUserAndFitnessClass(user, fitnessClass);
            
            if (bookingOpt.isEmpty()) {
                throw new IllegalStateException("No booking found for user and class");
            }
            
            Booking booking = bookingOpt.get();
            
            // If booking is already cancelled, notify and return
            if (booking.isCancelled()) {
                System.out.println("Booking was already cancelled");
                return;
            }
            
            // Check if the user is an admin or if it's more than 30 minutes before the class starts
            boolean isAdmin = user.getUserType() == UserType.ADMIN;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime classStartTime = fitnessClass.getStartTime();
            long minutesUntilClass = now.until(classStartTime, ChronoUnit.MINUTES);
            
            if (!isAdmin && minutesUntilClass < 30) {
                throw new IllegalStateException("Bookings can only be cancelled at least 30 minutes before the class starts");
            }
            
            // Mark booking as cancelled
            booking.setCancelled(true);
            bookingRepository.save(booking);
            
            Optional<FitnessClass> refreshedClassOpt = fitnessClassRepository.findById(fitnessClass.getId());
            FitnessClass refreshedClass = refreshedClassOpt.orElse(fitnessClass);
            
            refreshedClass.decrementCurrentAttendance();
            fitnessClassRepository.save(refreshedClass);
            
            User bookingUser = booking.getUser();
            
            bookingUser.decrementActiveBookings();
            
            Optional<WaitlistEntry> nextInLine = waitlistRepository.findFirstByFitnessClass(refreshedClass);
            if (nextInLine.isPresent()) {
                WaitlistEntry entry = nextInLine.get();
                entry.setProcessed(true);
                waitlistRepository.save(entry);
                
                try {
                    lock.unlock();
                    bookClass(entry.getUser(), refreshedClass);
                    System.out.println("Successfully booked class for waitlisted user: " + entry.getUser().getUsername());
                } catch (Exception e) {
                    System.out.println("Failed to automatically book for waitlisted user: " + e.getMessage());
                    lock.lock();
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }
    
    @Override
    public WaitlistEntry addToWaitlist(User user, FitnessClass fitnessClass) {
        ValidationUtils.validateCapacity(fitnessClass.getCapacity());

        // Check if class is cancelled
        if (fitnessClass.isCancelled()) {
            throw new IllegalStateException("Cannot add to waitlist for a cancelled class");
        }
        
        // Check if user is already on the waitlist
        Optional<WaitlistEntry> existingEntry = waitlistRepository.findByUserAndFitnessClass(user, fitnessClass);
        if (existingEntry.isPresent() && !existingEntry.get().isProcessed()) {
            throw new IllegalStateException("User is already on the waitlist for this class");
        }
        
        // Check if user is already booked
        Optional<Booking> existingBooking = bookingRepository.findByUserAndFitnessClass(user, fitnessClass);
        if (existingBooking.isPresent() && !existingBooking.get().isCancelled()) {
            throw new IllegalStateException("User is already booked for this class");
        }
        
        // Create and save waitlist entry
        WaitlistEntry entry = new WaitlistEntry(user, fitnessClass);
        return waitlistRepository.save(entry);
    }
}