package nz.ac.canterbury.seng302.tab.cucumber.team;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DataJpaTest
@SpringBootTest
public class JoinTeamIntegrationFeature {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private Team team;

    private String token;

    private UserEntity user;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    private MvcResult mvcResult;


    @Given("The user {string} wants to join a team")
    public void the_user_wants_to_join_a_team(String name) {
        String email = name + "@example.com";
        String password = "Password1@";
        user = new UserEntity(
                password,
                "Fabian",
                "Gilson",
                email,
                "1900-01-01",
                Set.of(),
                null
        );
        user.hashPassword(passwordEncoder);
        user.confirmEmail();
        userService.updateUser(user);

        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Given("I have a valid team invitation token for a team")
    public void i_have_a_valid_team_invitation_token_for_a_team() {
        team = Team.createTestTeam();
        token = teamService.generateNewToken(team);
        teamService.addTeam(team);
        Assertions.assertNotNull(team);
        Assertions.assertEquals(team.getJoinToken(), token);
        Assertions.assertNotNull(team.getJoinToken());
    }

    @When("I input the invitation token that is associated with the team")
    public void i_input_the_invitation_token_that_is_associated_with_the_team() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/join-team")
                                .with(csrf())
                                .param("textInput", token)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }


    @Then("I am added as a member to the team")
    public void i_am_added_as_a_member_to_the_team() {
        team = teamService.getTeamByToken(token);
        assertFalse(team.getMembers().isEmpty());
    }

    @Given("I have an invalid team invitation token")
    public void i_have_an_invalid_team_invitation_token() {
        token = "fake token";
    }

    @When("I input the invitation token")
    public void i_input_the_invitation_token() throws Exception {
        mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/join-team")
                                .with(csrf())
                                .param("textInput", token)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"))
                .andReturn();
    }

    @Then("An error message tells me the token is invalid")
    public void an_error_message_tells_me_the_token_is_invalid() {
        assertEquals("Team invitation token is invalid!", mvcResult.getFlashMap().get("error"));
    }
}
