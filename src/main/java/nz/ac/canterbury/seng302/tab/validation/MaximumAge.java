package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Checks that a {@link java.time.LocalDate} was a maximum number of years ago.
 * <p>
 * This is an inclusive check, so @MaximumAge(150) will allow dates from exactly 150 years from now.
 * <p>
 * Supports {@link java.time.LocalDate} only
 */
@Documented
@Constraint(validatedBy = {MaximumAgeValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaximumAge {

    /**
     * @return Returns the error message to be displayed when the date is too old
     */
    String message() default "You are too old to register.";

    /**
     * @return The maximum number of years ago that the date should allow
     */
    int value() default 150;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
