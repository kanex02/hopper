package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeParseException;

/**
 * Implements the validation check for {@link MaximumAge}
 *
 * @see MaximumAge
 */
public class MaximumAgeValidator implements ConstraintValidator<MaximumAge, String> {

    private int maxAge;

    /**
     * Sets the maximum age of the annotation check
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(MaximumAge constraintAnnotation) {
        this.maxAge = constraintAnnotation.value();
    }

    /**
     * Checks that the annotated date was not too old. For example {@code @MaximumAge(150)} would check that the date
     * is within the last 150 years from now.
     *
     * @param date    object to validate
     * @param context context in which the constraint is evaluated
     * @return Returns true if the annotated date is within the allowed range
     */
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        try {
            LocalDate value = LocalDate.parse(date);
            LocalDate earliestDate = LocalDate.now().minusYears(maxAge);
            return value.isAfter(ChronoLocalDate.from(earliestDate));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
