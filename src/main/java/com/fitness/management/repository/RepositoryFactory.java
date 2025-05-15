package com.fitness.management.repository;

import com.fitness.management.repository.impl.memory.InMemoryBookingRepository;
import com.fitness.management.repository.impl.memory.InMemoryFitnessClassRepository;
import com.fitness.management.repository.impl.memory.InMemoryUserRepository;
import com.fitness.management.repository.impl.memory.InMemoryWaitlistRepository;
import com.fitness.management.repository.impl.mysql.MySQLBookingRepository;
import com.fitness.management.repository.impl.mysql.MySQLFitnessClassRepository;
import com.fitness.management.repository.impl.mysql.MySQLUserRepository;
import com.fitness.management.repository.impl.mysql.MySQLWaitlistRepository;

public class RepositoryFactory {
    
    // Enum for selecting repository implementation type
    public enum RepositoryType {
        IN_MEMORY,
        MYSQL
    }

    private static RepositoryType defaultType = RepositoryType.IN_MEMORY;

    public static void setDefaultType(RepositoryType type) {
        defaultType = type;
    }
    
    public static UserRepository createUserRepository() {
        return createUserRepository(defaultType);
    }
    
    public static UserRepository createUserRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryUserRepository();
            case MYSQL:
                return new MySQLUserRepository();
            default:
                throw new UnsupportedOperationException("Unknown repository type: " + type);
        }
    }
    
    public static FitnessClassRepository createFitnessClassRepository() {
        return createFitnessClassRepository(defaultType);
    }
    
    public static FitnessClassRepository createFitnessClassRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryFitnessClassRepository();
            case MYSQL:
                return new MySQLFitnessClassRepository();
            default:
                throw new IllegalArgumentException("Unknown repository type: " + type);
        }
    }
    
    public static BookingRepository createBookingRepository() {
        return createBookingRepository(defaultType);
    }
    
    public static BookingRepository createBookingRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryBookingRepository();
            case MYSQL:
                return new MySQLBookingRepository();
            default:
                throw new IllegalArgumentException("Unknown repository type: " + type);
        }
    }
    
    public static WaitlistRepository createWaitlistRepository() {
        return createWaitlistRepository(defaultType);
    }
    
    public static WaitlistRepository createWaitlistRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryWaitlistRepository();
            case MYSQL:
                return new MySQLWaitlistRepository();
            default:
                throw new UnsupportedOperationException("Unknown repository type: " + type);
        }
    }
}