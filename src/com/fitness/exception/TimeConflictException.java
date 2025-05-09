package com.fitness.exception;

public class TimeConflictException extends RuntimeException {
    public TimeConflictException(String message) {
        super(message);
    }
}