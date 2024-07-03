package nz.ac.canterbury.seng302.tab.entity.user;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationTests {

    private PasswordValidator passwordValidator;

    private final String firstName = "Jane";
    private final String lastName = "Doe";
    private final String email = "janedoe@gmail.com";
    private final LocalDate dob = LocalDate.parse("1970-01-01");

    @BeforeEach
    void setUp() {
        passwordValidator = PasswordValidator.getInstance();
    }

    @Test
    void samePasswordsMatch() {
        assertTrue(passwordValidator.passwordsMatch("same", "same"));
    }

    @Test
    void differentPasswordsDoNotMatch() {
        assertFalse(passwordValidator.passwordsMatch("same", "diff"));
    }

    @Test
    void passwordMatchIsCaseSensitive() {
        assertFalse(passwordValidator.passwordsMatch("password", "Password"));
    }

    @Test
    void passwordMatchWorksAcrossInstances() {
        String password1 = new String("password");
        String password2 = new String("password");

        assertNotSame(password1, password2);
        assertTrue(passwordValidator.passwordsMatch(password1, password2));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "XXXXXJane!12",
                    "XXXXXJANE!12",
                    "XXXXXDoe!12",
                    "XXXXXDOE!12",
                    "PASSWORDjanedoe@gmail.com1234",
                    "p1D@",
                    "passwordisverylong",
                    "123",
                    "passworD",
                    "1234567890",
                    "Password1",
                    "",
                    " "
            }
    )
    void weakPasswordsAreNotStrong(String password) {
        assertFalse(passwordValidator.isStrongPassword(new UserEntity(
                password,
                firstName,
                lastName,
                email,
                dob.toString(),
                null,
                null
        ), password));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Password1!@",
                    "PasswoRd1!"
            }
    )
    void longAndMixedPasswordIsStrong(String password) {
        assertTrue(passwordValidator.isStrongPassword(new UserEntity(
                password,
                firstName,
                lastName,
                email,
                dob.toString(),
                null,
                null
        ), password));
    }
}
