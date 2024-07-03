package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.entity.Location;

import java.util.stream.Stream;

public class LocationFieldsValidator implements ConstraintValidator<ValidLocationFields, Location> {

    /**
     * Initialises the validator
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidLocationFields constraintAnnotation) {

    }

    /**
     * Checks that the annotated location has valid fields. If any of the address fields are not empty, then the city
     * and country fields must also not be empty.
     *
     * @param location object to validate
     * @param context context in which the constraint is evaluated
     * @return true if the annotated location has valid fields
     */
    @Override
    public boolean isValid(Location location, ConstraintValidatorContext context) {
        if (location == null) return true;

        boolean anyFieldNotEmpty = Stream.of(
                        location.getAddressLine1(),
                        location.getAddressLine2(),
                        location.getSuburb(),
                        location.getPostcode())
                .anyMatch(s -> s != null && !s.trim().isEmpty());

        if (anyFieldNotEmpty) {
            boolean cityInvalid = location.getCity() == null || location.getCity().trim().isEmpty();
            boolean countryInvalid = location.getCountry() == null || location.getCountry().trim().isEmpty();

            if (cityInvalid || countryInvalid) {
                context.disableDefaultConstraintViolation();

                if (cityInvalid) {
                    context.buildConstraintViolationWithTemplate("City is required when other location details are provided.")
                            .addPropertyNode("city").addConstraintViolation();
                }
                if (countryInvalid) {
                    context.buildConstraintViolationWithTemplate("Country is required when other location details are provided.")
                            .addPropertyNode("country").addConstraintViolation();
                }
                return false;
            }
        }
        return true;
    }
}
