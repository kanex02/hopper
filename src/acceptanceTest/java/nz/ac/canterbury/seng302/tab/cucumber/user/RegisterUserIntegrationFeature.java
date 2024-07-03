package nz.ac.canterbury.seng302.tab.cucumber.user;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterUserIntegrationFeature {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    private UserEntity user;

    @Given("There is no user with the email {string}")
    public void there_is_no_user_with_the_email(String email) {
        user = userRepository.findByEmailIgnoreCase(email);
        if (user != null) {
            userRepository.delete(user);
        }
        Assertions.assertNull(userRepository.findByEmailIgnoreCase(email));
    }

    @When("I register with the email {string} and have valid values for my details")
    public void i_register_with_the_email_and_have_valid_values_for_my_details(String email) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("password", "SecurePassword123!#%")
                        .param("confirmPassword", "SecurePassword123!#%")
                        .param("firstName", "Download-MuseSwipr")
                        .param("lastName", "On-Steam-Now")
                        .param("email", email)
                        .param("dateOfBirth", "2000-01-01")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
        );
    }

    @Then("a new user is created into the system with the email {string}")
    public void a_new_user_is_created_into_the_system_with_the_email(String email) {
        Assertions.assertNotNull(userRepository.findByEmailIgnoreCase(email));
    }
}
