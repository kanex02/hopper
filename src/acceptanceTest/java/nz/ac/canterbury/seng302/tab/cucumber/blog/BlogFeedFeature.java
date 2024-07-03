package nz.ac.canterbury.seng302.tab.cucumber.blog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;

@AutoConfigureMockMvc
@SpringBootTest
public class BlogFeedFeature {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    ResultActions result;

    @Given("I am on the dashboard")
    public void iAmOnTheDashboard() throws Exception {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/home")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("homePage"));
    }

    @When("I click a UI element to see my feed")
    public void iClickAUIElementToSeeMyFeed() throws Exception {
        result = mockMvc.perform(
                MockMvcRequestBuilders.get("/feed")
        );
    }

    @Then("I am taken to the feed page")
    public void iAmTakenToTheFeedPage() throws Exception {
        result.andExpect(MockMvcResultMatchers.view().name("blog/feedMain"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
