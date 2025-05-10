package com.fitness.management.service;

import com.fitness.management.model.Booking;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.WaitlistEntry;

import java.util.List;

public interface BookingService {
    Booking bookClass(User user, FitnessClass fitnessClass);
    void cancelBooking(User user, FitnessClass fitnessClass);
    List<Booking> getUserBookings(User user);
    List<Booking> getClassBookings(FitnessClass fitnessClass);
    WaitlistEntry addToWaitlist(User user, FitnessClass fitnessClass);
    List<WaitlistEntry> getWaitlist(FitnessClass fitnessClass);
}