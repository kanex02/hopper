package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.controller.TeamsController;
import nz.ac.canterbury.seng302.tab.controller.club.ClubController;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


@DataJpaTest
public class ClubTest {

    static private Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ".", "$", "$a", "2", "thisstringislongerthan50charactersdoyounotthinkthatthatisreallycoolIsuredothinkthat", "@^*&^#*", "{}{}"})
    void testInvalidClubNames(String clubName) {
        Club club = new Club(clubName, "TestDescription");
        Set<ConstraintViolation<Club>> violations = validator.validate(club);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", ".aa", "SKT T1", "救救命", "{Team}"})
    void testValidClubNames(String clubName) {
        Club club = new Club(clubName, "TestDescription");
        Set<ConstraintViolation<Club>> violations = validator.validate(club);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    "thisstringislongerthan150charactersdoyounotthinkthatthatisreallycoolIsuredothinkthatALLWORKANDNOPLAYMAKESJACOBADULLBOYALLWORKANDNOPLAYMAKESJACOBADULLBOYALLWORKANDNOPLAYMAKESJACOBADULLBOY",
                    "11111",
                    "@@@@@",
                    "abc\uD83D\uDE03\uD83D\uDE04" // these are emoji
            }
    )
    void testInvalidClubDescriptions(String clubDescription) {
        Club club = new Club("TestClub", clubDescription);
        Set<ConstraintViolation<Club>> violations = validator.validate(club);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", "üüü", ".aa", "SKT T1", "救救命", "{Team}", "aa1", "this is a 救救命description", "this is also a 1321 desc with numbers!#@"})
    void testValidClubDescriptions(String clubDescription) {
        Club club = new Club("TestClub", clubDescription);
        Set<ConstraintViolation<Club>> violations = validator.validate(club);
        assertTrue(violations.isEmpty());
    }

}
