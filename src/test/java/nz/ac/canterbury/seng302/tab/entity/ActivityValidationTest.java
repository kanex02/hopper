package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivityValidationTest {

    static private Validator validator;

    private Activity activity;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        activity = new Activity("Valid description",
                LocalDateTime.of(2022, 1, 22, 4, 30).toString(),
                LocalDateTime.of(2022, 1, 22, 5, 30).toString(),
                ActivityType.GAME, new Team(), null);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "a",
                    "abc21212321",
                    "Thequickbrownfoxjumpsoverthelazydogjumpedoverthefenceandranawaywithoutlookingbacsksssss"
            }
    )
    void createActivity_setValidDescription_NoErrorShown(String description) {
        activity.setDescription(description);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    " ",
                    "21212321",
                    "Thequickbrownfoxjumpsoverthelazydogjumpedoverthefenceandranawaywithoutlookingbacsksssss" +
                            "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss\n"
            }
    )
    void createActivity_setInvalidDescription_ErrorShown(String description) {
        activity.setDescription(description);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createActivity_startTimeProvided_NoErrorShown() {
        activity.setStartTime(LocalDateTime.now().toString());

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createActivity_noStartTimeProvided_ErrorShown() {
        activity.setStartTime(null);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createActivity_endTimeProvided_NoErrorShown() {
        activity.setEndTime(LocalDateTime.now().toString());

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createActivity_noEndTimeProvided_ErrorShown() {
        activity.setEndTime(null);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createActivity_emptyType_ErrorShown() {
        activity.setType(null);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(ActivityType.class)
    void createActivity_validType_NoErrorShown(ActivityType type) {
        activity.setType(type);

        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        assertTrue(violations.isEmpty());
    }

}
