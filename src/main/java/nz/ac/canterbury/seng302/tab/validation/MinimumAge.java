package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Checks that a {@link java.time.LocalDate} was a minimum number of years ago.
 * <p>
 * This is an inclusive check, so @MinimumAge(13) will allow dates from exactly 13 years from now.
 * <p>
 * Supports {@link java.time.LocalDate} only
 */
@Documented
@Constraint(validatedBy = {MinimumAgeValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumAge {

    /**
     * @return Returns the error message to be displayed when the date is too recent
     */
    String message() default "You are too young to register.";

    /**
     * @return The number of years ago that the date should allow
     */
    int value() default 13;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
