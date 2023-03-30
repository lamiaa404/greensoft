package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimestampIdTest {

    @Test
    // die @EqualsAndHashCode annotation in TimestampId hat laut jacoco
    // viele untested instructions, deswegen dieser test
    public void testEqualsAndHashcode() {
        TimestampId t1 = new TimestampId(1, 100);
        TimestampId t2 = new TimestampId(1, 100);

        assertEquals(t1, t2);
        assertTrue(t1.hashCode() == t2.hashCode());
    }
}
