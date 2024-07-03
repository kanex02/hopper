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

class NameValidationTests {

    static private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Fabian-Gilson",
                    "A-a",
                    "MuseSwipr",
                    "Dean-Carter",
                    "Le'Suex",
                    "Żółć",
                    "田中様",
                    "zzz",
                    "ZZZ",
                    "Aaa",
                    "Muse Swipr",
                    "Bagel Magic"
            }
    )
    void whenRegisterUserAndValidFirstName_thenNoError(String firstName) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                firstName,
                "On-Steam-Now",
                "museswipr@gmail.com",
                "1970-01-01",
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Fabian-Gilson",
                    "A-a",
                    "MuseSwipr",
                    "Dean-Carter",
                    "Le'Suex",
                    "Żółć",
                    "田中様",
                    "zzz",
                    "ZZZ",
                    "Aaa",
                    "Muse Swipr",
                    "Bagel Magic"
            }
    )
    void whenRegisterUserAndValidLastName_thenNoError(String lastName) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-Museswipr",
                lastName,
                "museswipr@gmail.com",
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
            "1",
            "!",
            "?",
            "➳",
            "℃",
            " ",
            "Ab➳",
            "MuseSwipr123",
            "-",
            "a",
            "OaO2zYjAOykuOAXT9Aj1w9jKAR5kNyyI16act4PCLGKpJWOHsri5qjBoGKtk446ihRSvTkn0GlzdcdBUbp9Fp9YWReiPA" +
                "0SAqPta6YkqrNSNeEjDHFOOIRFfhHNKah7BdKY3u7RY8yW1B3llzw9MqK378s3sKiHaiP2ajaJDhtj0Ao1iE8IVaF" +
                "dn5IO07UMG8UiMyOPsEzqa9CoFfii5hzNE5sePT4gNmSndJGkVdHbWxn0nxfLgYuNjroqpzuOh"
    })
    void whenRegisterUserAndInvalidFirstName_thenErrorIsReturned(String firstName) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                firstName,
                "Download-Museswipr",
                "museswipr@gmail.com",
                "1970-01-01",
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "1",
            "!",
            "?",
            "➳",
            "℃",
            " ",
            "Ab➳",
            "MuseSwipr123",
            "-",
            "a",
            "OaO2zYjAOykuOAXT9Aj1w9jKAR5kNyyI16act4PCLGKpJWOHsri5qjBoGKtk446ihRSvTkn0GlzdcdBUbp9Fp9YWReiPA" +
                "0SAqPta6YkqrNSNeEjDHFOOIRFfhHNKah7BdKY3u7RY8yW1B3llzw9MqK378s3sKiHaiP2ajaJDhtj0Ao1iE8IVaF" +
                "dn5IO07UMG8UiMyOPsEzqa9CoFfii5hzNE5sePT4gNmSndJGkVdHbWxn0nxfLgYuNjroqpzuOh"
    })
    void whenRegisterUserAndInvalidLastName_thenErrorIsReturned(String lastName) {
        var user = new UserEntity(
                "SecurePassword123!#%",
                "Download-Museswipr",
                lastName,
                "museswipr@gmail.com",
                "1970-01-01",
                Set.of(),
                null
        );

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
