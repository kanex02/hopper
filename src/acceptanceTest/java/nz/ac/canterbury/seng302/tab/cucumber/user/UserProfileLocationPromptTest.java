package nz.ac.canterbury.seng302.tab.cucumber.user;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.ModelMap;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileLocationPromptTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    private MvcResult mvcResult;


    @Given("I have registered to the system")
    public void iHaveRegisteredToTheSystem() {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
    }

    @When("I navigate to my profile page")
    public void iNavigateToMyProfilePage() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("I can choose to complete my profile by adding my location")
    public void iCanChooseToCompleteMyProfileByAddingMyLocation() {

        ModelMap model = mvcResult.getModelAndView().getModelMap();
        assertTrue(model.containsAttribute("completeProfilePrompt"));
    }
}
