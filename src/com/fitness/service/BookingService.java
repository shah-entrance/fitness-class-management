package com.fitness.service;

import com.fitness.model.Booking;
import com.fitness.model.FitnessClass;
import com.fitness.model.User;
import com.fitness.model.WaitlistEntry;

import java.util.List;

public interface BookingService {
    Booking bookClass(User user, FitnessClass fitnessClass);
    void cancelBooking(User user, FitnessClass fitnessClass);
    List<Booking> getUserBookings(User user);
    List<Booking> getClassBookings(FitnessClass fitnessClass);
    WaitlistEntry addToWaitlist(User user, FitnessClass fitnessClass);
    List<WaitlistEntry> getWaitlist(FitnessClass fitnessClass);
}