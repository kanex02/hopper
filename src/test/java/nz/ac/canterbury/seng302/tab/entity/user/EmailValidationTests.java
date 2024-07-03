package nz.ac.canterbury.seng302.tab.entity.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidationTests {

    static private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "JohnDoe@gmail.com",
            "fabian.gilson@canterbury.com",
            "museswipr@gmail.com",
            "i.am.old@hotmail.com",
            "IAMUPPERCASE@gmail.com",
            "l337g4m3r@email.com",
            "chewsday@e.co.uk",
            "a@b.com",
            "explorer-feldspar@outerwilds.com"
    })
    void whenRegisterUserAndValidEmail_thenNoError(String email) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                email,
                "1970-01-01",
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "aaa",
            "a@a@",
            "123",
            "➳➳@➳@",
            "2@",
            "@a",
            "@everyone",
            "@",
            "@@@@@@@",
            "@@",
            "a@@",
            "@@@",
            "a@a",
            "fabian.gilson@ca"

    })
    void whenRegisterUserAndInvalidEmail_thenErrorIsReturned(String email) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                email,
                "1970-01-01",
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }


}
