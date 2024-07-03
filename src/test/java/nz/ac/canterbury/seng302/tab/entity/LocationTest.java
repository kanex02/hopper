package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LocationTest {

    static private Validator validator;
    static private Location location;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void init() {
        location = new Location("test", "test", "test", "test", "test", "test");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123 Main St", "5th Avenue, Apt 7", "Suite 1000, 1234 Some Street", "456 Elm #9", "10-12 Broad Street"})
    void testValidAddresses(String address) {
        location.setAddressLine1(address);
        location.setAddressLine2(address);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123 Elm Street,", "456 Pine Ave.", "789 Oak Lane -", "/ Apt 123"})
    void testInvalidAddresses(String address) {
        location.setAddressLine1(address);
        location.setAddressLine2(address);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "A", "Taumatawhakatangihangakoauauotamateaturipukakapikimaungahoronukupokaiwhenuakitanatahu", "St. Kilda", "Sydney Olympic Park", "N'Djamena", "Camden & Islington"})
    void testValidSuburbs(String suburb) {
        location.setSuburb(suburb);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1234", " Suburb", "123Suburb", "Suburb!", "Suburb&", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw"})
    void testInvalidSuburbs(String suburb) {
        location.setSuburb(suburb);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch", "Paris", "New York", "St. John's", "San-Francisco", "Ciudad Juárez"})
    void testValidCities(String city) {
        location.setCity(city);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", " City", "123City", "City!", "City&", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw"})
    void testInvalidCities(String city) {
        location.setCity(city);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "AB1 2CD", "A12 3BC", "0123-456", "34-567"})
    void testValidPostcodes(String postcode) {
        location.setPostcode(postcode);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1234", "12345-", "A1! 2CD", "123456789012345678901"})
    void testInvalidPostcodes(String postcode) {
        location.setPostcode(postcode);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"United States", "United Arab Emirates", "Côte d'Ivoire", "Costa Rica", "Saint Vincent and the Grenadines"})
    void testValidCountries(String country) {
        location.setCountry(country);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-Canada", "France-", "Brazil & Chile", "Australia,", "The Bahamas are a "})
    void testInvalidCountries(String country) {
        location.setCountry(country);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

}
