package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Validator annotation to prevent fields from containing emojis.
 * <p>
 * May be applied to any {@link CharSequence}
 */
@Documented
@Constraint(validatedBy = {DoesNotContainEmojiValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoesNotContainEmoji {

    /**
     * @return Returns an error message to display if the field contains emojis
     */
    String message() default "This field may not contain emojis";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
