# Fitness Class Management System

A Java application for managing fitness class bookings, scheduling, and user management with different membership tiers.

## Features

- **User Management:** Register and login users with different membership tiers (Platinum, Gold, Silver)
- **Class Management:** Create, schedule, and cancel fitness classes
- **Booking System:** Book classes with capacity management and waitlisting
- **Concurrency Support:** Thread-safe implementation for simultaneous booking requests
- **Multiple Repository Options:** In-memory storage with MySQL implementation placeholder

## System Requirements

- Java JDK 8 or higher
- JUnit for running tests

## Project Structure

- `src/` - Source code
  - `com.fitness.model` - Domain model classes
  - `com.fitness.service` - Business logic and service implementations
  - `com.fitness.repository` - Data access layer with repository pattern
  - `com.fitness.exception` - Custom exceptions
  - `com.fitness.util` - Utility classes
- `test/` - Unit and integration tests
- `lib/` - External dependencies

## Running the Application

Compile and run the application using the following commands:

```bash
# Compile
javac -d bin -cp "lib/*" $(find src -name "*.java")

# Run
java -cp "bin:lib/*" com.fitness.management.App
```

## Testing

The project includes JUnit tests for all major components. Run the tests from your IDE or using the command line.

## Design Considerations

- Thread safety for concurrent booking operations
- Factory pattern for repository implementation switching
- Service-oriented architecture for business logic
- Extensive validation for all operations
