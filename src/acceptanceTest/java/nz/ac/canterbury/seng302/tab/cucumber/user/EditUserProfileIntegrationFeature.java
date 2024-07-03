package nz.ac.canterbury.seng302.tab.cucumber.user;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
@DataJpaTest
public class EditUserProfileIntegrationFeature {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    UserRepository userRepository;
    
    MockHttpServletRequestBuilder requestBuilder;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    
    @Autowired
    private UserService userService;
    
    private ResultActions mockResult;
    
    private static final String ERRORS = "editErrors";
    
    private static final String FIRST_NAME = "firstName";
    
    private static final String LAST_NAME = "lastName";
    
    private static final String EMAIL = "email";
    
    private static final String EXAMPLE_EMAIL = "test99@example.com";
    
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    
    private static final String EXAMPLE_DOB = "1998-01-01";
    
    @Given("I am on the edit profile form")
    public void i_am_on_the_edit_profile_form() {
        ContextConfiguration.authenticateTestUser(
            this.authenticationManager,
            this.passwordEncoder,
            this.userService
        );
    
        requestBuilder = MockMvcRequestBuilders.post("/editProfile/1");
    }
    
    @Given("I enter valid values for my first name, last name, email, and date of birth")
    public void i_enter_valid_values_for_my_first_name_last_name_email_and_date_of_birth() {
        requestBuilder = requestBuilder.with(csrf())
                        .param(FIRST_NAME, "Test")
                        .param(LAST_NAME, "User")
                        .param(EMAIL, EXAMPLE_EMAIL)
                        .param(DATE_OF_BIRTH, EXAMPLE_DOB);
    }
    
    @When("I hit the save changes button")
    public void i_hit_the_save_changes_button() throws Exception {
        mockResult = mockMvc.perform(requestBuilder);
    }
    
    @Then("The new details are saved")
    public void the_new_details_are_saved() throws Exception {
        MvcResult mvcResult = editProfileMvcResult();
        
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(mvcResult.getRequest());
        assertThat(flashMap.get("changesSaved"), is("Changes have been saved"));
        
        Optional<UserEntity> updatedUser = userRepository.findById(1L);
        
        assertThat(updatedUser.isPresent(), is(true));
        assertThat(updatedUser.get().getFirstName(), is("Test"));
        assertThat(updatedUser.get().getLastName(), is("User"));
        assertThat(updatedUser.get().getEmail(), is(EXAMPLE_EMAIL));
        assertThat(updatedUser.get().getDateOfBirth(), is(EXAMPLE_DOB));
    }
    
    @Given("I enter {string} for my first name and last name")
    public void i_enter_for_my_first_name_and_last_name(String invalidName) {
        requestBuilder = requestBuilder.with(csrf())
            .param(FIRST_NAME, invalidName)
            .param(LAST_NAME, invalidName)
            .param(EMAIL, EXAMPLE_EMAIL)
            .param(DATE_OF_BIRTH, EXAMPLE_DOB);
    }
    
    @Then("The error message tells me the first name and last name fields contain invalid values")
    public void the_error_message_tells_me_the_first_name_and_last_name_fields_contain_invalid_values() throws Exception {
        MvcResult mvcResult = editProfileMvcResult();
    
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(mvcResult.getRequest());
    
        assertThat(flashMap, hasKey(ERRORS));
    
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) flashMap.get(ERRORS);

        assertThat(errors, hasKey(FIRST_NAME));
        assertThat(errors, hasKey(LAST_NAME));
    }
    
    @Given("I enter {string} for my email address")
    public void i_enter_for_my_email_address(String invalidEmail) {
        requestBuilder = requestBuilder.with(csrf())
            .param(FIRST_NAME, "Test")
            .param(LAST_NAME, "User")
            .param(EMAIL, invalidEmail)
            .param(DATE_OF_BIRTH, EXAMPLE_DOB);
    }
    
    @Then("The error message tells me the email address is invalid")
    public void the_error_message_tells_me_the_email_address_is_invalid() throws Exception {
        MvcResult mvcResult = editProfileMvcResult();
    
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(mvcResult.getRequest());
    
        assertThat(flashMap, hasKey(ERRORS));
    
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) flashMap.get(ERRORS);
    
        assertThat(errors, hasKey(EMAIL));
    }
    
    @Given("I enter a date of birth of someone younger than {int} years old")
    public void i_enter_a_date_of_birth_of_someone_younger_than_years_old(Integer yearsAgo) {
    
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.minusYears((long) yearsAgo-1);
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = futureDate.format(formatter);
        
        requestBuilder = requestBuilder.with(csrf())
            .param(FIRST_NAME, "Test")
            .param(LAST_NAME, "User")
            .param(EMAIL, EXAMPLE_EMAIL)
            .param(DATE_OF_BIRTH, formattedDate);
    }
    
    @Then("The error message tells me the date of birth is invalid")
    public void the_error_message_tells_me_the_date_of_birth_is_invalid() throws Exception {
        MvcResult mvcResult = editProfileMvcResult();
    
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(mvcResult.getRequest());
    
        assertThat(flashMap, hasKey(ERRORS));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) flashMap.get(ERRORS);
    
        assertThat(errors, hasKey(DATE_OF_BIRTH));
    }
    
    @Given("I enter an existing email address")
    public void i_enter_an_existing_email_address() {
    
        String existingEmail = "existing.email@example.com";
        String password = "ExistingPassword1@";
        UserEntity existingUser = new UserEntity(
            password,
            "Existing",
            "User",
            existingEmail,
            "1990-01-01",
            Set.of(),
            null
        );
        existingUser.hashPassword(passwordEncoder);
        existingUser.confirmEmail();
        userService.updateUser(existingUser);
        
        requestBuilder = requestBuilder.with(csrf())
            .param(FIRST_NAME, "Test")
            .param(LAST_NAME, "User")
            .param(EMAIL, existingEmail)
            .param(DATE_OF_BIRTH, EXAMPLE_DOB);
    }
    
    @Then("The error message tells met he email address already exists")
    public void the_error_message_tells_met_he_email_address_already_exists() throws Exception {
        MvcResult mvcResult = editProfileMvcResult();
    
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(mvcResult.getRequest());
    
        assertThat(flashMap, hasKey(ERRORS));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) flashMap.get(ERRORS);
    
        assertThat(errors, hasKey(EMAIL));
        assertThat(errors.get(EMAIL), is("Email already exists!"));
    }
    
    private MvcResult editProfileMvcResult () throws Exception {
    
        return mockResult
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/user"))
            .andReturn();
    }
    
}
