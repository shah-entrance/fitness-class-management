package com.fitness.repository.impl.mysql;

public class MySQLNotImplementedException extends RuntimeException {
    public MySQLNotImplementedException(String message) {
        super(message);
    }
    
    public MySQLNotImplementedException() {
        super("MySQL implementation is not available yet");
    }
}