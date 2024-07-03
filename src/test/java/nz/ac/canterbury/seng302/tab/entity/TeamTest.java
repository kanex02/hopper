package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.controller.TeamsController;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
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
public class TeamTest {
    static private Validator validator;

    @InjectMocks
    private TeamsController teamsController;

    @Mock
    private TeamService teamService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Autowired
    private TeamRepository teamRepository;


    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ".", "$", "$a", "2", "thisstringislongerthan30characters", "@^*&^#*", "{}{}"})
    void testInvalidTeamNames(String teamName) {
        Team team = Team.createTestTeam();
        team.setTeamName(teamName);
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", ".aa", "SKT T1", "救救命", "{Team}"})
    void testValidTeamNames(String teamName) {
        Team team = Team.createTestTeam();
        team.setTeamName(teamName);
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "'xx', 'Team name must be at least 3 characters'",
            "'thisstringislongerthan30characters', 'Team name must be less than 30 characters'",
            "'$&%@#', 'Team name must contain a letter or number and only consist of letters, numbers, dots, and curly braces'"
    })
    void test_invalidTeamName_displaysCorrectMessage(String teamName, String expectedMessage) {
        Team team = Team.createTestTeam();
        team.setTeamName(teamName);

        Set<ConstraintViolation<Team>> violations = validator.validate(team);

        // Check that there is exactly one violation
        assertEquals(1, violations.size());

        // Get the violation message
        String message = violations.iterator().next().getMessage();

        // Check that the expected error message is displayed
        assertEquals(expectedMessage, message);
    }

    @Test
    void testAddingManagerWhenNonePresent() {
        Team team = Team.createTestTeam();
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        team.addManager(user);
        assertEquals(1, team.getManagers().stream().filter(userEntity -> userEntity.getFirstName() == "Test").toList().size());
    }

    @Test
    void generateToken_TeamCreation_TokenAddedToTeam() {
        teamService = new TeamService(teamRepository);
        Team team = Team.createTestTeam();
        teamService.generateNewToken(team);
        assertNotEquals(null, team.getJoinToken());
    }

    @Test
    void generateToken_generateTokenTwice_twoDifferentTokens() {
        teamService = new TeamService(teamRepository);
        Team team = Team.createTestTeam();
        String firstToken = teamService.generateNewToken(team);
        String secondToken = teamService.generateNewToken(team);
        assertNotSame(firstToken, secondToken);
    }

    @Test
    void generateToken_newTokenRepoState_tokenSavedToRepo() {
        teamService = new TeamService(teamRepository);
        Team team = Team.createTestTeam();
        teamService.generateNewToken(team);
        assertEquals(1, teamRepository.getAllTokens().size());
    }

    @Test
    void testAddingManagerWhenUserIsAlreadyManager() {
        Team team = Team.createTestTeam();
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        team.addManager(user);
        assertThrows(IllegalArgumentException.class, () -> team.addManager(user));
    }

    @Test
    void testUpdateRolesMoreThanThreeManagers() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("user1", "manager");
        formParams.put("user2", "manager");
        formParams.put("user3", "manager");
        formParams.put("user4", "manager");

        String result = teamsController.updateRoles(1L, model, formParams, "_csrf", redirectAttributes);

        assertEquals("redirect:/teams/1", result);
        verify(redirectAttributes).addFlashAttribute("formErrorRoles", "There can only be 3 managers per team!");
    }

    @Test
    void testUpdateRolesLessThanOneManager() {
        Map<String, String> formParams = new HashMap<>();

        String result = teamsController.updateRoles(1L, model, formParams, "_csrf", redirectAttributes);

        assertEquals("redirect:/teams/1", result);
        verify(redirectAttributes).addFlashAttribute("formErrorRoles", "There must be at least 1 manager!");
    }

    @Test
    void testRemovingManagerWhenMoreThanOnePresent() {
        Team team = Team.createTestTeam();
        UserEntity user1 = new UserEntity();
        user1.setFirstName("one");
        UserEntity user2 = new UserEntity();
        user2.setFirstName("two");
        UserEntity user3 = new UserEntity();
        user3.setFirstName("three");
        team.addManager(user1);
        team.addManager(user2);
        team.addManager(user3);
        team.removeManager(user3);
        assertEquals(1, team.getManagers().stream().filter(userEntity -> Objects.equals(userEntity.getFirstName(), "one")).toList().size());
        assertEquals(1, team.getManagers().stream().filter(userEntity -> Objects.equals(userEntity.getFirstName(), "two")).toList().size());
        assertEquals(2, team.getManagers().size());
    }

}