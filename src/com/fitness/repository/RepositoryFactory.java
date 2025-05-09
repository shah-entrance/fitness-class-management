package com.fitness.repository;

import com.fitness.repository.impl.memory.InMemoryBookingRepository;
import com.fitness.repository.impl.memory.InMemoryFitnessClassRepository;
import com.fitness.repository.impl.memory.InMemoryUserRepository;
import com.fitness.repository.impl.memory.InMemoryWaitlistRepository;
import com.fitness.repository.impl.mysql.MySQLBookingRepository;
import com.fitness.repository.impl.mysql.MySQLFitnessClassRepository;
import com.fitness.repository.impl.mysql.MySQLUserRepository;
import com.fitness.repository.impl.mysql.MySQLWaitlistRepository;

/**
 * Factory class for creating repository instances
 */
public class RepositoryFactory {
    
    // Enum for selecting repository implementation type
    public enum RepositoryType {
        IN_MEMORY,
        MYSQL
    }
    
    // Default repository type
    private static RepositoryType defaultType = RepositoryType.IN_MEMORY;
    
    /**
     * Set the default repository type
     * 
     * @param type The repository type
     */
    public static void setDefaultType(RepositoryType type) {
        defaultType = type;
    }
    
    /**
     * Get the default repository type
     * 
     * @return The default repository type
     */
    public static RepositoryType getDefaultType() {
        return defaultType;
    }
    
    /**
     * Create a UserRepository instance
     * 
     * @return A UserRepository instance
     */
    public static UserRepository createUserRepository() {
        return createUserRepository(defaultType);
    }
    
    /**
     * Create a UserRepository instance with a specific type
     * 
     * @param type The repository type
     * @return A UserRepository instance
     */
    public static UserRepository createUserRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryUserRepository();
            case MYSQL:
                return new MySQLUserRepository();
            default:
                throw new IllegalArgumentException("Unknown repository type: " + type);
        }
    }
    
    /**
     * Create a FitnessClassRepository instance
     * 
     * @return A FitnessClassRepository instance
     */
    public static FitnessClassRepository createFitnessClassRepository() {
        return createFitnessClassRepository(defaultType);
    }
    
    /**
     * Create a FitnessClassRepository instance with a specific type
     * 
     * @param type The repository type
     * @return A FitnessClassRepository instance
     */
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
    
    /**
     * Create a BookingRepository instance
     * 
     * @return A BookingRepository instance
     */
    public static BookingRepository createBookingRepository() {
        return createBookingRepository(defaultType);
    }
    
    /**
     * Create a BookingRepository instance with a specific type
     * 
     * @param type The repository type
     * @return A BookingRepository instance
     */
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
    
    /**
     * Create a WaitlistRepository instance
     * 
     * @return A WaitlistRepository instance
     */
    public static WaitlistRepository createWaitlistRepository() {
        return createWaitlistRepository(defaultType);
    }
    
    /**
     * Create a WaitlistRepository instance with a specific type
     * 
     * @param type The repository type
     * @return A WaitlistRepository instance
     */
    public static WaitlistRepository createWaitlistRepository(RepositoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryWaitlistRepository();
            case MYSQL:
                return new MySQLWaitlistRepository();
            default:
                throw new IllegalArgumentException("Unknown repository type: " + type);
        }
    }
}