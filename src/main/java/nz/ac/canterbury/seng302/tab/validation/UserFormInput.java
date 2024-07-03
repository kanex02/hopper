package nz.ac.canterbury.seng302.tab.validation;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.time.LocalDate;

/**
 * Record that stores all data that a user will enter into a registration form.
 * <p>
 * The data stored in this record MAY contain errors; it has not been validated. This class should only be used for
 * validating input. It should NEVER be used to actually store data.
 *
 * @param firstName       The entered first name
 * @param lastName        The entered last name
 * @param email           The entered email address
 * @param dateOfBirth     The entered date of birth, as a {@link LocalDate}
 * @param password        The password as it was entered, in PLAINTEXT.
 * @param confirmPassword An additional field that the user must enter to confirm that they entered the {@code password}
 *                        they wanted to. Like {@code password}, this is in PLAINTEXT
 */
public record UserFormInput(
        String firstName,
        String lastName,
        String email,
        String dateOfBirth,
        String password,
        String confirmPassword
) {

    /**
     * Creates a new {@link UserFormInput} from a {@link UserEntity}. As the password stored in a user entity is always
     * hashed and cannot be decoded, the password and confirmed password must be specified in the method arguments.
     *
     * @param user            The user to create input data from
     * @param password        The password of the user. Note that the password is not checked with a password encoder,
     *                        and thus may not actually match.
     * @param confirmPassword The password of the user. Note that the password is not checked with a password encoder,
     *                        and thus may not actually match.
     * @return Returns a new input object
     */
    public static UserFormInput fromUser(UserEntity user, String password, String confirmPassword) {
        return new UserFormInput(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDateOfBirth(),
                password,
                confirmPassword
        );
    }

}
