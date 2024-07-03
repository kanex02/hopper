package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Checks that a {@link nz.ac.canterbury.seng302.tab.entity.Location} has valid fields.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocationFieldsValidator.class)
@Documented
public @interface ValidLocationFields {
    String message() default "Invalid location details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}