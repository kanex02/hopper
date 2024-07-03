package nz.ac.canterbury.seng302.tab.entity.challenge;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

class ChallengeTest {

    private static final LocalDateTime VALID_DATE = LocalDateTime.now();
    private static final String VALID_GOAL = "This is a valid goal";
    private static final String VALID_TITLE = "This is a valid title";
    private static final int VALID_HOPS = Challenge.HOPS_LOWER_BOUND;
    private static final UserEntity dummyUser  = new UserEntity(
            "Password1@",
            "Lydia",
            "Looi",
            "llo35@uclive.ac.nz",
            "26-08-1999",
            Collections.emptySet(),
            null
    );

    static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void titleWithTrailingWhitespace_onCreate_isStripped() {
        String title = " Title ";

        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                title,
                VALID_GOAL,
                VALID_HOPS
        );

        Assertions.assertEquals("Title", challenge.getTitle());
    }

    @Test
    void goalWithTrailingWhitespace_onCreate_isStripped() {
        String goal = " Goal ";

        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                goal,
                VALID_HOPS
        );

        Assertions.assertEquals("Goal", challenge.getGoal());
    }

    @ParameterizedTest
    @ValueSource(
            ints = {
                    Challenge.HOPS_LOWER_BOUND,
                    Challenge.HOPS_UPPER_BOUND,
                    Challenge.HOPS_UPPER_BOUND - 1,
                    Challenge.HOPS_LOWER_BOUND + 1,
                    (Challenge.HOPS_LOWER_BOUND + Challenge.HOPS_UPPER_BOUND) / 2 // midpoint
            }
    )
    void givenValidChallengeHops_isValid(int validHopsCount) {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                VALID_GOAL,
                validHopsCount
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Title",
                    "There And Back Again"
            }
    )
    void givenValidChallengeTitle_isValid(String validTitle) {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                validTitle,
                VALID_GOAL,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void givenChallengeWithNullDate_isInvalid() {
        Challenge challenge = new Challenge(
                dummyUser,
                null,
                VALID_TITLE,
                VALID_GOAL,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidTitles")
    void givenChallengeWithInvalidTitle_isInvalid(String invalidTitle) {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                invalidTitle,
                VALID_GOAL,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidGoals")
    void givenChallengeWithInvalidGoal_isInvalid(String invalidGoal) {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                invalidGoal,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void givenChallengeWithNullTitle_isInvalid() {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                null,
                VALID_GOAL,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void givenChallengeWithNullGoal_isInvalid() {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                null,
                VALID_HOPS
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            ints = {
                    0,
                    -1,
                    Challenge.HOPS_UPPER_BOUND + 1,
                    Challenge.HOPS_LOWER_BOUND - 1,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
            }
    )
    void givenChallengeWithInvalidHops_isInvalid(int invalidHopsCount) {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                VALID_GOAL,
                invalidHopsCount
        );

        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        Assertions.assertFalse(violations.isEmpty());
    }

    /**
     * The method source for invalid titles as it includes non-constant params
     *
     * @return Returns the array of string params on invalid titles
     */
    private static String[] invalidTitles() {
        return new String[]{
                "",
                "X".repeat(Challenge.TITLE_MAX_LENGTH + 1),
                "ThisIsAVeryLongString".repeat(Challenge.TITLE_MAX_LENGTH), // MUCH longer than title max length
                "ContainsNumbers1",
                "Contains Spaces And $",
                ",,###2!",
                "Tabs\tAre\tNot\tValid",
                "Nor\nAre\nNewlines"
        };
    }

    /**
     * The method source for invalid goals as it includes non-constant params
     *
     * @return Returns the array of string params on invalid goals
     */
    private static String[] invalidGoals() {
        return new String[]{
                "",
                "X".repeat(Challenge.GOAL_MAX_LENGTH + 1),
                "ThisIsAVeryLongString".repeat(Challenge.GOAL_MAX_LENGTH)
        };
    }

    @Test
    void givenIncompleteChallenge_WhenCompletedForUser_ChallengeIsCompleteForUser() {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                VALID_TITLE,
                VALID_GOAL,
                VALID_HOPS
        );

        challenge.completeForUser(dummyUser);

        Assertions.assertTrue(challenge.isChallengeCompleteForUser(dummyUser));
    }

    @Test
    void givenChallengeNotAssociatedWithUser_WhenCompletedForUser_ExceptionThrownAndChallengeNotCompleteForUser() {

        UserEntity otherUser = new UserEntity();
        otherUser.setId(100L);

        Challenge challenge = new Challenge(
                otherUser,
                VALID_DATE,
                VALID_TITLE,
                VALID_GOAL,
                VALID_HOPS
        );

        Assertions.assertThrows(IllegalStateException.class, () -> challenge.completeForUser(dummyUser));
        Assertions.assertFalse(challenge.isChallengeCompleteForUser(dummyUser));
    }

    @Test
    void coffeeChallenge_onCompleteForUser_throwsIsTeaPot() {
        Challenge challenge = new Challenge(
                dummyUser,
                VALID_DATE,
                "Brew Coffee",
                VALID_GOAL,
                VALID_HOPS
        );

        Assertions.assertThrows(IsTeaPotException.class, () -> challenge.completeForUser(dummyUser));
    }
}
