package nz.ac.canterbury.seng302.tab.cucumber.challenge;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest
public class ChallengeRewardsFeature {

    public static final String COMPLETE_CHALLENGE = "/challenge/complete";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ChallengeService challengeService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    private UserEntity mockUser;
    private UserEntity invitedUser;
    private long initialHops;
    private Challenge challengeToComplete;
    
    private static final String TITLE = "title";
    
    private static final String DESCRIPTION = "description";
    
    private static final String CHALLENGE_ID = "challengeId";

    @Given("I am logged in and viewing a currently available challenge with ID {long}")
    public void iAmLoggedInAndViewingACurrentlyAvailableChallengeWithId(long challengeId) throws Exception {

        mockUser = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        initialHops = mockUser.getTotalHops();

        challengeToComplete = challengeService.getChallengeById(challengeId);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/challenge/view")
        ).andExpect(status().isOk());
    }

    @When("I mark the challenge as completed with uploaded media")
    public void iMarkTheChallengeAsCompletedWithUploadedMedia() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "filename.jpg",
                "image/jpeg",
                "Mock file content".getBytes()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.multipart(COMPLETE_CHALLENGE)
                        .file(file) // Attach the file to the request
                        .param(TITLE, TITLE)
                        .param(DESCRIPTION, DESCRIPTION)
                        .param(CHALLENGE_ID, String.valueOf(challengeToComplete.getId()))
                        .with(csrf())
        ).andExpect(status().is3xxRedirection());
    }

    @Then("I am rewarded with a small amount of hops \\(xp) for that challenge")
    public void iAmRewardedWithTheHopsForThatChallenge() {

        mockUser = userService.getUserById(mockUser.getId());
        Assertions.assertEquals(initialHops + challengeToComplete.getHops(), mockUser.getTotalHops());
    }

    @When("I try to complete a challenge I have already completed")
    public void iTryToCompleteAChallengeIHaveAlreadyCompleted() throws Exception {

        initialHops = mockUser.getTotalHops();
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "filename.jpg",
                "image/jpeg",
                "Mock file content".getBytes()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.multipart(COMPLETE_CHALLENGE)
                        .file(file) // Attach the file to the request
                        .param(TITLE, TITLE)
                        .param(DESCRIPTION, DESCRIPTION)
                        .param(CHALLENGE_ID, String.valueOf(challengeToComplete.getId()))
                        .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("error/400"));
    }

    @When("I try to complete a challenge without uploading media")
    public void iTryToCompleteAChallengeWithoutUploadingMedia() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post(COMPLETE_CHALLENGE)
                        .with(csrf())
                        .flashAttr(CHALLENGE_ID, String.valueOf(challengeToComplete.getId()))
        ).andExpect(status().is3xxRedirection());

    }

    @Then("I am not rewarded with the hops for that challenge")
    public void iAmNotRewardedWithTheHopsForThatChallenge() {

        mockUser = userService.getUserById(mockUser.getId());
        Assertions.assertEquals(initialHops, mockUser.getTotalHops());
    }

    @And("The challenge is not marked as complete")
    public void theChallengeIsNotMarkedAsComplete() {

        challengeToComplete = challengeService.getChallengeById(challengeToComplete.getId());
        Assertions.assertFalse(challengeToComplete.isComplete());
    }

    @Given("The challenge is shared with another user")
    public void the_challenge_is_shared_with_another_user() {

        invitedUser = userService.getUserById(3L);
        challengeToComplete.inviteUsers(List.of(invitedUser));
        challengeService.saveChallenge(challengeToComplete);
    }

    @Then("The challenge is marked as complete for me")
    public synchronized void the_challenge_is_marked_as_complete_for_me() {

        challengeToComplete = challengeService.getChallengeById(challengeToComplete.getId()); // Refresh challenge
        Assertions.assertTrue(challengeToComplete.isChallengeCompleteForUser(mockUser));
    }

    @And("The invited user has completed the challenge")
    public void the_invited_user_has_completed_the_challenge() {

        challengeToComplete = challengeService.getChallengeById(challengeToComplete.getId());
        initialHops = invitedUser.getTotalHops();
        challengeToComplete.completeForUser(invitedUser);
        challengeService.saveChallenge(challengeToComplete);
    }

    @Then("The other user is also rewarded with the hops for that challenge")
    public void the_other_user_is_also_rewarded_with_the_hops_for_that_challenge() {

        invitedUser = userService.getUserById(invitedUser.getId()); // Refresh user
        Assertions.assertEquals(initialHops + challengeToComplete.getHops(), invitedUser.getTotalHops());
    }
}