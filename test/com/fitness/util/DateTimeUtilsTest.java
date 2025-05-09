package com.fitness.util;

import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class DateTimeUtilsTest {

    @Test
    public void testFormatDateTime() {
        // Test standard formatting
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 9, 14, 30);
        assertEquals("2025-05-09 14:30", DateTimeUtils.formatDateTime(dateTime));
        
        // Test null handling
        assertEquals("", DateTimeUtils.formatDateTime(null));
    }
    
    @Test
    public void testParseDateTime() {
        // Test standard parsing
        LocalDateTime expected = LocalDateTime.of(2025, 5, 9, 14, 30);
        LocalDateTime actual = DateTimeUtils.parseDateTime("2025-05-09 14:30");
        assertEquals(expected, actual);
    }
    
    @Test(expected = DateTimeParseException.class)
    public void testParseDateTime_InvalidFormat() {
        // Should throw an exception for invalid format
        DateTimeUtils.parseDateTime("05/09/2025 2:30PM");
    }
    
    @Test
    public void testIsTimeOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2025, 5, 9, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2025, 5, 9, 11, 0);
        
        // Case 1: Completely overlapping
        LocalDateTime start2 = LocalDateTime.of(2025, 5, 9, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2025, 5, 9, 11, 0);
        assertTrue(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 2: Second period starts during first period
        start2 = LocalDateTime.of(2025, 5, 9, 10, 30);
        end2 = LocalDateTime.of(2025, 5, 9, 11, 30);
        assertTrue(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 3: First period starts during second period
        start2 = LocalDateTime.of(2025, 5, 9, 9, 30);
        end2 = LocalDateTime.of(2025, 5, 9, 10, 30);
        assertTrue(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 4: Second period inside first period
        start2 = LocalDateTime.of(2025, 5, 9, 10, 15);
        end2 = LocalDateTime.of(2025, 5, 9, 10, 45);
        assertTrue(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 5: First period inside second period
        start2 = LocalDateTime.of(2025, 5, 9, 9, 30);
        end2 = LocalDateTime.of(2025, 5, 9, 11, 30);
        assertTrue(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 6: No overlap - second period starts when first ends
        start2 = LocalDateTime.of(2025, 5, 9, 11, 0);
        end2 = LocalDateTime.of(2025, 5, 9, 12, 0);
        assertFalse(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 7: No overlap - first period starts when second ends
        start2 = LocalDateTime.of(2025, 5, 9, 9, 0);
        end2 = LocalDateTime.of(2025, 5, 9, 10, 0);
        assertFalse(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
        
        // Case 8: No overlap - completely separate
        start2 = LocalDateTime.of(2025, 5, 9, 12, 0);
        end2 = LocalDateTime.of(2025, 5, 9, 13, 0);
        assertFalse(DateTimeUtils.isTimeOverlap(start1, end1, start2, end2));
    }
}