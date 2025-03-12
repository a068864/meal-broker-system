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
        Location location = new Location(40.7128, -74.0060);

        assertEquals(40.7128, location.getLatitude());
        assertEquals(-74.0060, location.getLongitude());
    }

    @Test
    void testSetters() {
        Location location = new Location();
        location.setLatitude(51.5074);
        location.setLongitude(-0.1278);

        assertEquals(51.5074, location.getLatitude());
        assertEquals(-0.1278, location.getLongitude());
    }

    @Test
    void testDistanceTo() {
        // New York City (40.7128, -74.0060)
        Location nyc = new Location(40.7128, -74.0060);

        // Los Angeles (34.0522, -118.2437)
        Location la = new Location(34.0522, -118.2437);

        // Approximate distance between NYC and LA is around 3936 km
        double distance = nyc.distanceTo(la);

        // Allow for some rounding difference, but the distance should be approximately correct
        assertTrue(distance > 3900 && distance < 4000);
    }

    @Test
    void testIsWithinRadius() {
        // New York City
        Location nyc = new Location(40.7128, -74.0060);

        // Boston (42.3601, -71.0589) - about 306 km from NYC
        Location boston = new Location(42.3601, -71.0589);

        // Philadelphia (39.9526, -75.1652) - about 130 km from NYC
        Location philly = new Location(39.9526, -75.1652);

        // Test with 200 km radius
        assertFalse(nyc.isWithinRadius(boston, 200));
        assertTrue(nyc.isWithinRadius(philly, 200));
    }

    @Test
    void testToString() {
        Location location = new Location(40.7128, -74.0060);
        String toString = location.toString();

        assertTrue(toString.contains("40.7128"));
        assertTrue(toString.contains("-74.006"));
    }

    @Test
    void testValidation() {
        // Valid location
        Location validLocation = new Location(40.7128, -74.0060);
        assertEquals(0, validator.validate(validLocation).size());

        // Invalid latitude (out of range)
        Location invalidLatitude = new Location(91.0, -74.0060);
        assertFalse(validator.validate(invalidLatitude).isEmpty());

        // Invalid longitude (out of range)
        Location invalidLongitude = new Location(40.7128, -181.0);
        assertFalse(validator.validate(invalidLongitude).isEmpty());

        // Null values
        Location nullValues = new Location(null, null);
        assertFalse(validator.validate(nullValues).isEmpty());
    }
}