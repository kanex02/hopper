package nz.ac.canterbury.seng302.tab.validation;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * Validates the strength of plaintext passwords
 * <p>
 * Putting this as a Spring validation annotation seemed too risky to make into a ModelAttribute, and having it as a
 * validated RequestParam does not seem to work very well with BindingResults
 * <p>
 * It would also not work on {@link UserEntity#getPassword()} as the password is hashed prior to storage such that we
 * cannot recover the plaintext password. Therefore, this class exists to provide that validation manually.
 */
public final class PasswordValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * @return the singleton instance of InputValidationService
     */
    public static PasswordValidator getInstance() {
        return INSTANCE;
    }

    /**
     * Checks that the password entered into a registration form is valid.
     * <p>
     * A password is valid if both of the following conditions are met:
     * <p>
     * 1. The password is 'strong' by the definition in {@link PasswordValidator#isStrongPassword(UserEntity, String)}
     * 2. The password and the confirmed password are equal, by {@link String#equals(Object)}
     *
     * @param user The user from input submitted to the form
     * @return Returns an {@link Optional} containing an error message if any errors are present in the entered information,
     * otherwise returns an empty Optional if everything is ok.
     */
    public Optional<String> isPasswordValid(UserEntity user, String password, String confirmPassword) {
        if (Objects.equals(password, "") && Objects.equals(confirmPassword, "")) {
            return Optional.of("Password and confirm password cannot be empty.");
        }

        if (Objects.equals(password, "")) {
            return Optional.of("Password cannot be empty.");
        }

        if (Objects.equals(confirmPassword, "")) {
            return Optional.of("Confirm password cannot be empty.");
        }

        if (!this.isStrongPassword(user, password)) {
            return Optional.of(
                    "This password is too weak. The password must not contain any other fields from the user profile form, be at least 8 characters long, and contain a variation of different types of characters"
            );
        }

        if (!this.passwordsMatch(password, confirmPassword)) {
            return Optional.of("Passwords do not match.");
        }

        return Optional.empty();
    }

    /**
     * Method to check whether the user inputted the same password twice.
     *
     * @param password          password to check
     * @param confirmedPassword confirm password to check with password
     * @return true if both fields are the same, false otherwise.
     */
    public boolean passwordsMatch(String password, String confirmedPassword) {
        return Objects.equals(password, confirmedPassword);
    }

    /**
     * Checks if the password entered by the user is 'strong'.
     * <p>
     * A password is strong if BOTH of the following conditions are met:
     * <p>
     * 1. No other fields are contained in the password, including the first name, last name, and email.
     * 2. The password contains a mix of upper case characters, lower case characters, numbers, and special characters
     * <p>
     * If the {@link UserFormInput#password()} is {@code null}, then returns true as the user profile is being edited so
     * this check does not need to apply.
     *
     * @param user The user from input submitted to the form
     * @return true if password is strong, false otherwise.
     */
    public boolean isStrongPassword(UserEntity user, String password) {
        if (password == null) {
            return true;
        }

        String lowerCasePassword = password.toLowerCase();

        boolean passwordContainsOtherFields = false;

        if (!user.getFirstName().isBlank()){
            passwordContainsOtherFields = lowerCasePassword.contains(user.getFirstName().toLowerCase());
        }

        if (!user.getLastName().isBlank() && !passwordContainsOtherFields){
            passwordContainsOtherFields = lowerCasePassword.contains(user.getLastName().toLowerCase());
        }

        if (!user.getEmail().isBlank() && !passwordContainsOtherFields){
            passwordContainsOtherFields = lowerCasePassword.contains(user.getEmail().toLowerCase());
        }

        if (passwordContainsOtherFields) {
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        // measures the diversity of the character set of the password
        Set<PasswordStrengthMetrics> strengthMetrics = new HashSet<>();
        for (char chr : password.toCharArray()) {
            if (Character.isUpperCase(chr)) {
                strengthMetrics.add(PasswordStrengthMetrics.HAS_UPPER_CASE);
            } else if (Character.isLowerCase(chr)) {
                strengthMetrics.add(PasswordStrengthMetrics.HAS_LOWER_CASE);
            } else if (Character.isDigit(chr)) {
                strengthMetrics.add(PasswordStrengthMetrics.HAS_DIGIT);
            } else {
                strengthMetrics.add(PasswordStrengthMetrics.HAS_SPECIAL_CHAR);
            }
        }

        return strengthMetrics.size() >= PasswordStrengthMetrics.values().length;
    }

    private PasswordValidator() {

    }

    /**
     * Different metrics of password strength
     */
    private enum PasswordStrengthMetrics {
        HAS_LOWER_CASE,
        HAS_UPPER_CASE,
        HAS_DIGIT,
        HAS_SPECIAL_CHAR
    }

    private static final PasswordValidator INSTANCE = new PasswordValidator();
}
