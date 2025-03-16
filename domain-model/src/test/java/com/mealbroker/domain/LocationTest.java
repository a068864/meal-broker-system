package com.mealbroker.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testLocationConstructor() {
        Location location = new Location(43.6532, -79.3832);

        assertEquals(43.6532, location.getLatitude());
        assertEquals(-79.3832, location.getLongitude());
    }

    @Test
    void testSetters() {
        Location location = new Location();
        location.setLatitude(45.5017);
        location.setLongitude(-73.5673);

        assertEquals(45.5017, location.getLatitude());
        assertEquals(-73.5673, location.getLongitude());
    }

    @Test
    void testDistanceTo() {
        // Toronto (43.6532, -79.3832)
        Location toronto = new Location(43.6532, -79.3832);

        // Vancouver (49.2827, -123.1207)
        Location vancouver = new Location(49.2827, -123.1207);

        // Approximate distance between Toronto and Vancouver is around 3359 km
        double distance = toronto.distanceTo(vancouver);

        // Allow for some rounding difference, but the distance should be approximately correct
        assertTrue(distance > 3300 && distance < 3400);
    }

    @Test
    void testIsWithinRadius() {
        // Toronto
        Location toronto = new Location(43.6532, -79.3832);

        // Ottawa (45.4215, -75.6972) - about 352 km from Toronto
        Location ottawa = new Location(45.4215, -75.6972);

        // Montreal (45.5017, -73.5673) - about 504 km from Toronto
        Location montreal = new Location(45.5017, -73.5673);

        // Test with 400 km radius
        assertFalse(toronto.isWithinRadius(montreal, 400));
        assertTrue(toronto.isWithinRadius(ottawa, 400));
    }

    @Test
    void testToString() {
        Location location = new Location(43.6532, -79.3832);
        String toString = location.toString();

        assertTrue(toString.contains("43.6532"));
        assertTrue(toString.contains("-79.3832"));
    }

    @Test
    void testValidation() {
        // Valid location
        Location validLocation = new Location(43.6532, -79.3832);
        assertEquals(0, validator.validate(validLocation).size());

        // Invalid latitude (out of range)
        Location invalidLatitude = new Location(91.0, -79.3832);
        assertFalse(validator.validate(invalidLatitude).isEmpty());

        // Invalid longitude (out of range)
        Location invalidLongitude = new Location(43.6532, -181.0);
        assertFalse(validator.validate(invalidLongitude).isEmpty());

        // Null values
        Location nullValues = new Location(null, null);
        assertFalse(validator.validate(nullValues).isEmpty());
    }
}