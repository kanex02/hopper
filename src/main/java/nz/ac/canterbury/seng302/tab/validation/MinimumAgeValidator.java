package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeParseException;

/**
 * Implements the validation check for {@link MinimumAge}
 *
 * @see MinimumAge
 */
public class MinimumAgeValidator implements ConstraintValidator<MinimumAge, String> {

    private int minAge;

    /**
     * Sets the minimum age of the annotation check
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(MinimumAge constraintAnnotation) {
        this.minAge = constraintAnnotation.value();
    }

    /**
     * Checks that the annotated date was not too recent. For example {@code @MinimumAge(13)} would check that the date
     * was 13 or more years ago from now.
     *
     * @param date   object to validate
     * @param context context in which the constraint is evaluated
     * @return Returns true if the annotated date was far enough in the past
     */
    @Override
    public boolean isValid(String date , ConstraintValidatorContext context) {
        try {
            LocalDate value = LocalDate.parse(date);
            LocalDate latestDate = LocalDate.now().minusYears(minAge);
            return !value.isAfter(ChronoLocalDate.from(latestDate));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
