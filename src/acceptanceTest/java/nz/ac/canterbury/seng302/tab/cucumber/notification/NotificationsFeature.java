package nz.ac.canterbury.seng302.tab.cucumber.notification;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Notification;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.notification.NotificationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
public class NotificationsFeature {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    private ResultActions result;

    private Notification challengeShareNotification;

    private static final Long CHALLENGE_ID = 2L;

    @Given("I am logged in as a user who has received a challenge share notification")
    public void iAmLoggedInAsAUserWhoHasReceivedAChallengeShareNotification() {
        UserEntity testUser;
        testUser = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        challengeShareNotification = new Notification("shared a challenge!", List.of(testUser), testUser, null, null, CHALLENGE_ID, LocalDateTime.now());
        challengeShareNotification.setId(1L);
        notificationService.save(challengeShareNotification);
        userService.updateUser(testUser);
    }

    @When("I click on the notification")
    public void iClickOnTheNotification() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get("/notification/" + challengeShareNotification.getId()));
    }

    @Then("I am taken to the challenge page")
    public void iAmTakenToTheChallengePage() throws Exception {
        result.andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/challenge/view"))
                .andExpect(model().attribute("notificationChallengeId", String.valueOf(CHALLENGE_ID)));
    }

}
