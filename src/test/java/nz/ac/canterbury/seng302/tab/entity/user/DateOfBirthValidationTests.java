package nz.ac.canterbury.seng302.tab.entity.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateOfBirthValidationTests {

    static private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @MethodSource("datesInPastOver13")
    void whenRegisterUserAndOver13_thenNoErrors(LocalDate date) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                date.toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenRegisterUserAndExactly13_thenNoErrors() {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                LocalDate.now().minusYears(13L).toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }


    @ParameterizedTest
    @MethodSource("datesInFuture")
    void whenRegisterUserAndDateInFuture_thenErrorIsReturned(LocalDate date) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                date.toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenRegisterUserAndDateIsNow_thenErrorIsReturned() {
        LocalDate now = LocalDate.now();

        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                now.toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("datesInPastUnder13")
    void whenRegisterUserAndUnder13_thenErrorIsReturned(LocalDate date) {

        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                date.toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    static Stream<Arguments> datesInPastUnder13() {
        List<LocalDate> params = List.of(
                LocalDate.now().minusDays(1L),
                LocalDate.now().minusWeeks(1L),
                LocalDate.now().minusMonths(1L),
                LocalDate.now().minusYears(1L)
        );

        return params.stream().map(Arguments::arguments);
    }

    static Stream<Arguments> datesInPastOver13() {
        List<LocalDate> params = List.of(
                LocalDate.now().minusYears(13L).minusDays(1L),
                LocalDate.now().minusYears(14L),
                LocalDate.now().minusYears(18L),
                LocalDate.now().minusYears(21L),
                LocalDate.now().minusYears(100L)
        );

        return params.stream().map(Arguments::arguments);
    }

    static Stream<Arguments> datesInFuture() {
        List<LocalDate> params = List.of(
                LocalDate.now().plusDays(1L),
                LocalDate.now().plusWeeks(1L),
                LocalDate.now().plusMonths(1L),
                LocalDate.now().plusYears(1L)
        );

        return params.stream().map(Arguments::arguments);
    }

    @Test
    void whenRegisterUserAndExactly150_thenErrorIsReturned() {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                LocalDate.now().minusYears(150L).toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("datesInPastOver150")
    void whenRegisterUserAndOver150_thenErrorIsReturned(LocalDate date) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswipr@gmail.com",
                date.toString(),
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    static Stream<Arguments> datesInPastOver150() {
        List<LocalDate> params = List.of(
                LocalDate.now().minusYears(150L).minusDays(1L),
                LocalDate.now().minusYears(151L),
                LocalDate.now().minusYears(200L)
        );

        return params.stream().map(Arguments::arguments);
    }
}
