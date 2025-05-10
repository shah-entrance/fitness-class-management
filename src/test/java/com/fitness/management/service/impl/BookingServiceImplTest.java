package com.fitness.management.service.impl;

import com.fitness.management.exception.BookingLimitExceededException;
import com.fitness.management.exception.ClassFullException;
import com.fitness.management.exception.TimeConflictException;
import com.fitness.management.model.Booking;
import com.fitness.management.model.ClassType;
import com.fitness.management.model.FitnessClass;
import com.fitness.management.model.User;
import com.fitness.management.model.UserType;
import com.fitness.management.model.WaitlistEntry;
import com.fitness.management.service.BookingService;
import com.fitness.management.service.mock.MockBookingRepository;
import com.fitness.management.service.mock.MockFitnessClassRepository;
import com.fitness.management.service.mock.MockWaitlistRepository;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingServiceImplTest {
    
    private BookingService bookingService;
    
    private MockBookingRepository mockBookingRepository;
    private MockFitnessClassRepository mockFitnessClassRepository;
    private MockWaitlistRepository mockWaitlistRepository;
    
    private User regularUser;
    private User premiumUser;
    private FitnessClass yogaClass;
    private FitnessClass spinClass;
    private FitnessClass boxingClass;
    
    @Before
    public void setUp() throws Exception {
        // Initialize mock repositories
        mockBookingRepository = new MockBookingRepository();
        mockFitnessClassRepository = new MockFitnessClassRepository();
        mockWaitlistRepository = new MockWaitlistRepository();
        
        // Create BookingServiceImpl instance
        bookingService = new BookingServiceImpl();
        
        // Set private repository fields using reflection
        Field bookingRepoField = BookingServiceImpl.class.getDeclaredField("bookingRepository");
        bookingRepoField.setAccessible(true);
        bookingRepoField.set(bookingService, mockBookingRepository);
        
        Field fitnessClassRepoField = BookingServiceImpl.class.getDeclaredField("fitnessClassRepository");
        fitnessClassRepoField.setAccessible(true);
        fitnessClassRepoField.set(bookingService, mockFitnessClassRepository);
        
        Field waitlistRepoField = BookingServiceImpl.class.getDeclaredField("waitlistRepository");
        waitlistRepoField.setAccessible(true);
        waitlistRepoField.set(bookingService, mockWaitlistRepository);
        
        // Clear any existing data
        mockBookingRepository.clear();
        mockFitnessClassRepository.clear();
        mockWaitlistRepository.clear();
        
        // Create test users - using GOLD and PLATINUM instead of STANDARD and PREMIUM
        regularUser = new User("regular_user", "password", UserType.GOLD);
        premiumUser = new User("premium_user", "password", UserType.PLATINUM);
        
        // Create test fitness classes - using CROSSFIT instead of STRENGTH
        LocalDateTime now = LocalDateTime.now();
        yogaClass = new FitnessClass("Yoga", ClassType.YOGA, 10, now.plusHours(1), 60);
        spinClass = new FitnessClass("Spin", ClassType.CARDIO, 5, now.plusHours(3), 45);
        boxingClass = new FitnessClass("Boxing", ClassType.CROSSFIT, 8, now.plusHours(2), 50);
        
        // Save the fitness classes to the repository
        mockFitnessClassRepository.save(yogaClass);
        mockFitnessClassRepository.save(spinClass);
        mockFitnessClassRepository.save(boxingClass);
    }
    
    @Test
    public void testBookClass_Success() {
        // Book a class successfully
        Booking booking = bookingService.bookClass(regularUser, yogaClass);
        
        // Verify booking was created
        assertNotNull(booking);
        assertEquals(regularUser.getId(), booking.getUser().getId());
        assertEquals(yogaClass.getId(), booking.getFitnessClass().getId());
        assertFalse(booking.isCancelled());
        
        // Verify fitness class attendance was updated
        assertEquals(1, yogaClass.getCurrentAttendance());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testBookClass_AlreadyBooked() {
        // Book a class
        bookingService.bookClass(regularUser, yogaClass);
        
        // Try to book the same class again
        bookingService.bookClass(regularUser, yogaClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testBookClass_ClassCancelled() {
        // Cancel the class
        yogaClass.setCancelled(true);
        mockFitnessClassRepository.save(yogaClass);
        
        // Try to book the cancelled class
        bookingService.bookClass(regularUser, yogaClass);
    }
    
    @Test(expected = BookingLimitExceededException.class)
    public void testBookClass_BookingLimitExceeded() {
        // First, let's check what's the actual booking limit of a SILVER user
        User silverUser = new User("silver_user", "password", UserType.SILVER);
        int bookingLimit = silverUser.getBookingLimit();
        System.out.println("SILVER user booking limit: " + bookingLimit);
        
        // Book classes until we reach the limit
        for (int i = 0; i < bookingLimit; i++) {
            LocalDateTime now = LocalDateTime.now();
            FitnessClass newClass = new FitnessClass(
                "Class" + i, 
                ClassType.YOGA, 
                10, 
                now.plusHours(i + 1), 
                45
            );
            mockFitnessClassRepository.save(newClass);
            bookingService.bookClass(silverUser, newClass);
        }
        
        // Now try to book one more class, which should exceed the limit
        LocalDateTime now = LocalDateTime.now();
        FitnessClass extraClass = new FitnessClass(
            "ExtraClass", 
            ClassType.DANCE, 
            10, 
            now.plusHours(bookingLimit + 1), 
            45
        );
        mockFitnessClassRepository.save(extraClass);
        
        // This should throw BookingLimitExceededException
        bookingService.bookClass(silverUser, extraClass);
    }
    
    @Test(expected = TimeConflictException.class)
    public void testBookClass_TimeConflict() {
        // Create two classes with overlapping times
        LocalDateTime now = LocalDateTime.now();
        FitnessClass class1 = new FitnessClass("Class1", ClassType.YOGA, 10, now.plusHours(1), 60);
        FitnessClass class2 = new FitnessClass("Class2", ClassType.CARDIO, 10, now.plusHours(1).plusMinutes(30), 60);
        
        mockFitnessClassRepository.save(class1);
        mockFitnessClassRepository.save(class2);
        
        // Book first class
        bookingService.bookClass(regularUser, class1);
        
        // Try to book second class with time conflict
        bookingService.bookClass(regularUser, class2);
    }
    
    @Test
    public void testBookClass_ClassFull_AddedToWaitlist() {
        // Set capacity to 1 and book with one user
        spinClass.setCapacity(1);
        mockFitnessClassRepository.save(spinClass);
        
        // Book the class with one user
        bookingService.bookClass(regularUser, spinClass);
        
        // Try to book with another user, should add to waitlist
        try {
            bookingService.bookClass(premiumUser, spinClass);
            fail("Should have thrown ClassFullException");
        } catch (ClassFullException e) {
            // Expected exception
            // Verify user is on waitlist
            Optional<WaitlistEntry> waitlistEntry = mockWaitlistRepository.findByUserAndFitnessClass(premiumUser, spinClass);
            assertTrue(waitlistEntry.isPresent());
            assertFalse(waitlistEntry.get().isProcessed());
        }
    }
    
    @Test
    public void testCancelBooking_Success() {
        // Book a class
        Booking booking = bookingService.bookClass(regularUser, yogaClass);
        
        // Cancel the booking
        bookingService.cancelBooking(regularUser, yogaClass);
        
        // Verify booking is cancelled
        Optional<Booking> updatedBooking = mockBookingRepository.findByUserAndFitnessClass(regularUser, yogaClass);
        assertTrue(updatedBooking.isPresent());
        assertTrue(updatedBooking.get().isCancelled());
        
        // Verify attendance is decremented
        assertEquals(0, yogaClass.getCurrentAttendance());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCancelBooking_NoBookingFound() {
        // Try to cancel a booking that doesn't exist
        bookingService.cancelBooking(regularUser, yogaClass);
    }
    
    @Test
    public void testCancelBooking_WithWaitlist_AutomaticBooking() {
        // Set capacity to 1
        spinClass.setCapacity(1);
        mockFitnessClassRepository.save(spinClass);
        
        // Book the class with regularUser
        bookingService.bookClass(regularUser, spinClass);
        
        // Add premiumUser to waitlist
        try {
            bookingService.bookClass(premiumUser, spinClass);
        } catch (ClassFullException e) {
            // Expected exception
        }
        
        // Cancel regularUser's booking
        bookingService.cancelBooking(regularUser, spinClass);
        
        // Verify waitlist entry is processed
        Optional<WaitlistEntry> waitlistEntry = mockWaitlistRepository.findByUserAndFitnessClass(premiumUser, spinClass);
        assertTrue(waitlistEntry.isPresent());
        assertTrue(waitlistEntry.get().isProcessed());
        
        // Verify premiumUser now has a booking
        Optional<Booking> premiumUserBooking = mockBookingRepository.findByUserAndFitnessClass(premiumUser, spinClass);
        assertTrue(premiumUserBooking.isPresent());
        assertFalse(premiumUserBooking.get().isCancelled());
    }
    
    @Test
    public void testGetUserBookings() {
        // Book multiple classes for the user
        bookingService.bookClass(regularUser, yogaClass);
        bookingService.bookClass(regularUser, spinClass);
        
        // Get user bookings
        List<Booking> userBookings = bookingService.getUserBookings(regularUser);
        
        // Verify the correct bookings are returned
        assertEquals(2, userBookings.size());
        assertTrue(userBookings.stream().anyMatch(b -> b.getFitnessClass().getId().equals(yogaClass.getId())));
        assertTrue(userBookings.stream().anyMatch(b -> b.getFitnessClass().getId().equals(spinClass.getId())));
    }
    
    @Test
    public void testGetClassBookings() {
        // Book the same class for multiple users
        bookingService.bookClass(regularUser, yogaClass);
        bookingService.bookClass(premiumUser, yogaClass);
        
        // Get class bookings
        List<Booking> classBookings = bookingService.getClassBookings(yogaClass);
        
        // Verify the correct bookings are returned
        assertEquals(2, classBookings.size());
        assertTrue(classBookings.stream().anyMatch(b -> b.getUser().getId().equals(regularUser.getId())));
        assertTrue(classBookings.stream().anyMatch(b -> b.getUser().getId().equals(premiumUser.getId())));
    }
    
    @Test
    public void testAddToWaitlist_Success() {
        // Add user to waitlist
        WaitlistEntry entry = bookingService.addToWaitlist(regularUser, yogaClass);
        
        // Verify entry was created
        assertNotNull(entry);
        assertEquals(regularUser.getId(), entry.getUser().getId());
        assertEquals(yogaClass.getId(), entry.getFitnessClass().getId());
        assertFalse(entry.isProcessed());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testAddToWaitlist_AlreadyOnWaitlist() {
        // Add user to waitlist
        bookingService.addToWaitlist(regularUser, yogaClass);
        
        // Try to add to waitlist again
        bookingService.addToWaitlist(regularUser, yogaClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testAddToWaitlist_AlreadyBooked() {
        // Book a class
        bookingService.bookClass(regularUser, yogaClass);
        
        // Try to add to waitlist for the same class
        bookingService.addToWaitlist(regularUser, yogaClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testAddToWaitlist_ClassCancelled() {
        // Cancel the class
        yogaClass.setCancelled(true);
        mockFitnessClassRepository.save(yogaClass);
        
        // Try to add to waitlist for the cancelled class
        bookingService.addToWaitlist(regularUser, yogaClass);
    }
    
    @Test
    public void testGetWaitlist() {
        // Add multiple users to waitlist
        bookingService.addToWaitlist(regularUser, spinClass);
        bookingService.addToWaitlist(premiumUser, spinClass);
        
        // Get waitlist
        List<WaitlistEntry> waitlist = bookingService.getWaitlist(spinClass);
        
        // Verify the correct entries are returned
        assertEquals(2, waitlist.size());
        assertTrue(waitlist.stream().anyMatch(e -> e.getUser().getId().equals(regularUser.getId())));
        assertTrue(waitlist.stream().anyMatch(e -> e.getUser().getId().equals(premiumUser.getId())));
    }
    
    @Test
    public void testAdminCancelClass_RestoresUserBookingLimit() {
        // Create a SILVER user with a booking limit of 3
        User silverUser = new User("silver_user", "password", UserType.SILVER);
        int bookingLimit = silverUser.getBookingLimit();
        assertEquals(3, bookingLimit); // Verify silver users have a limit of 3
        
        // Create an admin user
        User adminUser = new User("admin_user", "password", UserType.ADMIN);
        
        // Create classes for the test
        LocalDateTime now = LocalDateTime.now();
        FitnessClass class1 = new FitnessClass("Class1", ClassType.YOGA, 10, now.plusHours(1), 60);
        FitnessClass class2 = new FitnessClass("Class2", ClassType.CARDIO, 10, now.plusHours(2), 60);
        FitnessClass class3 = new FitnessClass("Class3", ClassType.DANCE, 10, now.plusHours(3), 60);
        FitnessClass class4 = new FitnessClass("Class4", ClassType.GYM, 10, now.plusHours(4), 60);
        
        // Save classes to repository
        mockFitnessClassRepository.save(class1);
        mockFitnessClassRepository.save(class2);
        mockFitnessClassRepository.save(class3);
        mockFitnessClassRepository.save(class4);
        
        // Book classes until we reach the limit
        bookingService.bookClass(silverUser, class1);
        bookingService.bookClass(silverUser, class2);
        bookingService.bookClass(silverUser, class3);
        
        // Verify active bookings count is at the limit
        assertEquals(bookingLimit, silverUser.getActiveBookingsCount());
        
        // Verify user cannot book another class
        try {
            bookingService.bookClass(silverUser, class4);
            fail("Should have thrown BookingLimitExceededException");
        } catch (BookingLimitExceededException e) {
            // Expected exception
        }
        
        // Now simulate admin cancelling one of the classes
        // First, we need to get a reference to the AdminService
        AdminServiceImpl adminService = new AdminServiceImpl();
        
        // Set the repositories in AdminService using reflection
        try {
            Field bookingRepoField = AdminServiceImpl.class.getDeclaredField("bookingRepository");
            bookingRepoField.setAccessible(true);
            bookingRepoField.set(adminService, mockBookingRepository);
            
            Field fitnessClassRepoField = AdminServiceImpl.class.getDeclaredField("fitnessClassRepository");
            fitnessClassRepoField.setAccessible(true);
            fitnessClassRepoField.set(adminService, mockFitnessClassRepository);
        } catch (Exception e) {
            fail("Failed to set up AdminService: " + e.getMessage());
        }
        
        // Admin cancels class1
        adminService.cancelClass(class1);
        
        // Verify the class is marked as cancelled
        assertTrue(class1.isCancelled());
        
        // Verify user's booking count was decreased
        assertEquals(bookingLimit - 1, silverUser.getActiveBookingsCount());
        
        // Verify user can now book another class
        Booking newBooking = bookingService.bookClass(silverUser, class4);
        assertNotNull(newBooking);
        assertFalse(newBooking.isCancelled());
        
        // Verify user is at the limit again
        assertEquals(bookingLimit, silverUser.getActiveBookingsCount());
    }
}