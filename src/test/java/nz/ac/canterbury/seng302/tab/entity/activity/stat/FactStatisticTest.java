package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
class FactStatisticTest {

    static private Validator validator;
    static private FactStatistic factStatistic;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        } catch (Exception e) {
            throw new RuntimeException("Validator could not be built. Aborting tests.", e);
        }
    }

    /**
     * Tests that the entity cannot be built without a description.
     */
    @Test
    void givenNoDescription_WhenBuildFact_ConstraintsViolated() {
        var builder = new FactStatistic.FactBuilder();
        assertThrows(IllegalStateException.class, () -> builder.buildAndValidate());
    }

    @Test
    void factStatisticWithDescriptionAndTimeBuilds() {
        var builder = new FactStatistic.FactBuilder();
        factStatistic = builder.withDescription("Test description").withTime(10).buildAndValidate();
        Set<ConstraintViolation<FactStatistic>> violations = validator.validate(factStatistic);
        assertTrue(violations.isEmpty());
        Assertions.assertEquals("Test description", factStatistic.getDescription());
        Assertions.assertEquals(10, factStatistic.getTime());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Foul ball!",
            "CAN YOU BELIEVE IT!",
            "Absolute deathtrap.",
            "Facts are for sissies.",
            """
                    Keep in mind that user attention spans 
                    can vary, and users may be viewing 
                    the event description on different 
                    devices with varying screen sizes.
            """,
            """
                    It's a good practice to provide a concise 
                    summary and, if needed, offer the option 
                    for users to access additional details or 
                    a full description if they desire more information.
            """
    })
    void whenBuildFactAndDescriptionValid_ThenNoViolation(String description) {
        var builder = new FactStatistic.FactBuilder();
        factStatistic = builder.withDescription(description).buildAndValidate();
        Set<ConstraintViolation<FactStatistic>> violations = validator.validate(factStatistic);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    In Java, the regular expression '.' matches any character 
                    except for line terminators. Therefore, the multiline strings 
                    containing line breaks in your @ValueSource are not matching 
                    the pattern, resulting in a validation error.
                                
                    To allow multiline strings in your description field, you can 
                    update the regular expression pattern to include the DOTALL flag, 
                    which enables the dot character ('.') to match all characters, 
                    including line terminators.
            """,
            """
                    The maximum character length for a user-facing sports game event 
                    description string can vary depending on various factors, including 
                    the platform or medium where it will be displayed and the level of 
                    detail you want to provide to the users. However, as a general 
                    guideline, a maximum character length of around 200 to 300 characters 
                    is often considered reasonable for a concise and informative event 
                    description.
            """
    })
    void whenBuildFactAndDescriptionInvalid_ThenViolation(String description) {
        var builder = new FactStatistic.FactBuilder();
        assertThrows(IllegalStateException.class, () -> builder.withDescription(description).buildAndValidate());
    }
}
