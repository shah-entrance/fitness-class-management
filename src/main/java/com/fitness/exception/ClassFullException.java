package com.fitness.exception;

public class ClassFullException extends RuntimeException {
    public ClassFullException(String message) {
        super(message);
    }
}