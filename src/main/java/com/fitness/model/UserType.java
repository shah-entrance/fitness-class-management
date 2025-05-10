package com.fitness.model;

public enum UserType {
    ADMIN,
    PLATINUM(10),
    GOLD(5),
    SILVER(3);

    private final int bookingLimit;
    
    UserType() {
        this.bookingLimit = 0; // Admin has no booking limit
    }
    
    UserType(int bookingLimit) {
        this.bookingLimit = bookingLimit;
    }
    
    public int getBookingLimit() {
        return bookingLimit;
    }
}