package nz.ac.canterbury.seng302.tab.cucumber.activity;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.activity.ActivityEventController;
import nz.ac.canterbury.seng302.tab.controller.activity.ActivityScoreController;
import nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import nz.ac.canterbury.seng302.tab.pojo.FactStatisticForm;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.activity.ActivityStatisticService;
import nz.ac.canterbury.seng302.tab.service.activity.SubstitutionEventFactory;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ActivityStatisticsFeature {

    private static final int HOME_SCORE = 3;
    private static final int AWAY_SCORE = 7;
    @Autowired
    ActivityScoreController activityScoreController;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityStatisticService activityStatisticService;

    MockHttpServletRequestBuilder requestBuilder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;
    private long activityId;
    private UserEntity user;
    private Activity activity;
    private SubstitutionStatistic.Substitution substitution;
    private Team team;


    @Before("@ActivityStatistics")
    public void setup() {

        user = ContextConfiguration.authenticateTestUser(
                this.authenticationManager,
                this.passwordEncoder,
                this.userService
        );

        team = Team.createTestTeam();
        team.setManagers(Set.of(user));
        team = teamRepository.save(team);
    }

    @Given("I have an activity")
    public void i_have_an_activity() {
        user = authenticateTestUser(authenticationManager, passwordEncoder, userService);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startTime = LocalDateTime.now().plusHours(1).format(formatter);
        String endTime = LocalDateTime.now().plusHours(2).format(formatter);

        Activity createdActivity = new Activity("test",
                "2023-08-10T14:11:04.462288164",
                "2023-08-20T16:11:04.462288164",
                ActivityType.GAME,
                team
        );
        createdActivity.setUser(user);

        Assertions.assertDoesNotThrow(() -> this.activity = activityService.saveActivity(createdActivity));
        Assertions.assertNotNull(this.activity);

        activityId = this.activity.getId();
    }


    @When("I submit a score with one integer for each team")
    public void iSubmitAScoreWithOneIntegerForEachTeam() throws Exception {

        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/activity/updateScore/" + activityId)
                                .with(csrf())
                                .param("homeScore", String.valueOf(HOME_SCORE))
                                .param("awayScore", String.valueOf(AWAY_SCORE))
                )
                .andExpect(
                        MockMvcResultMatchers.redirectedUrl("/activity/" + activityId)
                )
                .andReturn();
    }

    @When("I want to record a substitution event with time {long}")
    public void i_record_a_substitution_event_with_time(long time) {
        UserEntity originalPlayer = new UserEntity(
                "Password1@",
                "The OG",
                "Gamer",
                "original@example.com",
                LocalDateTime.now().minusYears(20).toString(),
                Set.of(),
                null
        );

        UserEntity newPlayer = new UserEntity(
                "Password1@",
                "The Brand New",
                "And Exciting",
                "brave.new.world@example.com",
                LocalDateTime.now().minusYears(22).toString(),
                Set.of(),
                null
        );

        originalPlayer.hashPassword(passwordEncoder);
        newPlayer.hashPassword(passwordEncoder);

        originalPlayer = userRepository.save(originalPlayer);
        newPlayer = userRepository.save(newPlayer);

        substitution = new SubstitutionStatistic.Substitution(originalPlayer, newPlayer);

        requestBuilder = MockMvcRequestBuilders.post("/activity/create-statistic/{activityId}", activityId)
                .with(csrf())
                .param("type", "substitution")
                .param(SubstitutionEventFactory.TIME_KEY, String.valueOf(time))
                .param(SubstitutionEventFactory.ORIGINAL_PLAYER_ID_KEY, String.valueOf(originalPlayer.getId()))
                .param(SubstitutionEventFactory.NEW_PLAYER_ID_KEY, String.valueOf(newPlayer.getId()));
    }

    @Then("The score is saved")
    public void theScoreIsSaved() {

        Activity updatedActivity = activityService.getActivityById(activityId);

        Assertions.assertNotNull(updatedActivity);
        Assertions.assertEquals(this.activity.getId(), updatedActivity.getId());

        ActivityScore updatedScore = updatedActivity.getActivityScore();
        Assertions.assertEquals(HOME_SCORE, updatedScore.getHomeScore());
        Assertions.assertEquals(AWAY_SCORE, updatedScore.getAwayScore());
    }

    @When("I add facts about the activity in the form of a fact {string} and an optional {string} when that fact happened")
    public void iAddFactsAboutTheActivityInTheFormOfAFactDescriptionAndAnOptionalTimeWhenThatFactHappened(String description, String time) {

        FactStatisticForm factStatisticForm = new FactStatisticForm(
                description, time);

        activity = activityStatisticService.saveFactStatisticToActivity(factStatisticForm, activity);
    }

    @Then("The fact is saved")
    public void theFactIsSaved() {
        Assertions.assertFalse(activity.getEvents().isEmpty());
    }

    @Then("The substitution is created successfully")
    public void the_substitution_is_created_successfully() throws Exception {
        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists(ActivityEventController.SUCCESS_ATTRIBUTE_KEY));

        Activity updatedActivity = activityService.getActivityById(activityId);

        Assertions.assertNotNull(updatedActivity);
        Assertions.assertEquals(this.activity.getId(), updatedActivity.getId());

        List<ActivityEventStatistic<?>> events = updatedActivity.getEvents();

        Assertions.assertTrue(
                events.stream()
                        .anyMatch(event -> {
                            return substitution.equals(event.getValue());
                        })
        );

    }
}
