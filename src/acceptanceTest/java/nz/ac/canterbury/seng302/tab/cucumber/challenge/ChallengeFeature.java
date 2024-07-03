package nz.ac.canterbury.seng302.tab.cucumber.challenge;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest
public class ChallengeFeature {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserRepository userRepository;

    Challenge challenge;

    @Given("I am viewing a currently available challenge")
    public void i_am_viewing_a_currently_available_challenge() {

        UserEntity dummyUser = new UserEntity(
                "Password1@",
                "Matthew", "Minish",
                "scrumboardfan@gmail.com",
                LocalDateTime.now().minusYears(99L).toString(),
                Collections.emptySet(),
                null
        );
        dummyUser.hashPassword(passwordEncoder);

        dummyUser = userRepository.save(dummyUser);

        Assertions.assertNotNull(dummyUser);

        challenge = new Challenge(
                dummyUser,
                LocalDateTime.now().plusWeeks(1L),
                "Go outside",
                "Drop 302",
                Challenge.HOPS_LOWER_BOUND
        );

        challenge = challengeRepository.save(challenge);

        Assertions.assertNotNull(challenge);
    }

    @When("I click a button to mark the challenge as complete")
    public void i_click_a_button_to_mark_the_challenge_as_complete() {
        Assertions.assertDoesNotThrow(() -> challenge.complete());
    }

    @Then("The challenge is marked as complete")
    public void the_challenge_is_marked_as_complete() {
        Assertions.assertTrue(challenge.isComplete());
    }

    @Then("I cannot complete it again")
    public void i_cannot_complete_it_again() {
        Assertions.assertThrows(IllegalStateException.class, () -> challenge.complete());
    }

}
