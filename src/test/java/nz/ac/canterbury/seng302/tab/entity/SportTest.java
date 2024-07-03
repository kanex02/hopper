package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SportTest {

    static private Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    ".",
                    " football",
                    "$a",
                    "f00tball"
            }
    )
    void test_whenInvalidSportName_thenViolationsPresent(String name) {
        var sport = new Sport(name);
        Set<ConstraintViolation<Sport>> violations = validator.validate(sport);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "a",
                    "football",
                    "That sport where the ball on string goes into the cup",
                    "救命"
            }
    )
    void test_whenValidSportName_thenNoViolationsPresent(String name) {
        var sport = new Sport(name);
        Set<ConstraintViolation<Sport>> violations = validator.validate(sport);
        assertTrue(violations.isEmpty());
    }


}
