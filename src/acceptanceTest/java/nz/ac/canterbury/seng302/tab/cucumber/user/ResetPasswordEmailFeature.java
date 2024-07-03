package nz.ac.canterbury.seng302.tab.cucumber.user;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import nz.ac.canterbury.seng302.tab.controller.PasswordResetController;
import nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration;
import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.schedule.TokenDeletionService;
import nz.ac.canterbury.seng302.tab.service.user.PasswordResetService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DataJpaTest
@AutoConfigureMockMvc
public class ResetPasswordEmailFeature {
    ResultActions result;
    @Autowired
    EntityManager entityManager;
    MockHttpServletRequestBuilder requestBuilder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    private Token token;
    private UserEntity user;
    private MvcResult mvcResult;
    private ContextConfiguration contextConfiguration;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordResetController passwordResetController;

    @Autowired
    private TokenDeletionService tokenDeletionService;

    private static final String RESET_PASS_EMAIL = "/resetPassEmail";
    private static final String RESET_PASSWORD = "/resetPassword/";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String TEST_PASSWORD = "Password1@";
    private static final String RETYPE_PASSWORD = "retypePassword";

    @Given("I am on the login page and want to reset my password")
    public void i_am_on_the_login_page() {
        // Nothing was here :D
    }

    @When("I hit the forgot password button")
    public void i_hit_the_forgot_password_button() {
        requestBuilder = MockMvcRequestBuilders.get(RESET_PASS_EMAIL);
    }

    @Then("I see a form asking me for my email address")
    public void i_see_a_form_asking_me_for_my_email_address() throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("resetPassEmail"));
    }

    @Given("I am on the lost password form")
    public void i_am_on_the_lost_password_form() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(RESET_PASS_EMAIL)
        ).andExpect(status().isOk());

        requestBuilder = MockMvcRequestBuilders.post(RESET_PASS_EMAIL);
    }

    @When("I submit an email with an invalid format")
    public void i_enter_an_email_with_an_invalid_format() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("enterEmail", ".@hdgfbjbfvh");
        result = mockMvc.perform(requestBuilder);
    }

    @Then("an error message tells me the email address is invalid")
    public void an_error_message_tells_me_the_email_address_is_invalid() throws Exception {
        result
                .andExpect(status().is4xxClientError())
                .andExpect(model().attributeExists("errorMessage"));
    }

    @When("I enter a valid email that is not known to the system")
    public void i_enter_a_valid_email_that_is_not_known_to_the_system() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("enterEmail", "Simon@hdgf.com");
        result = mockMvc.perform(requestBuilder);
    }

    @Then("a confirmation message tells me that an email was sent to the address if it was recognised.")
    public void a_confirmation_message_tells_me_that_an_email_was_sent_to_the_address_if_it_was_recognised() throws Exception {
        result
                .andExpect(status().isOk())
                .andExpect(view().name("checkResetPasswordEmail"));
    }

    @Given("{string} received an email to reset my password")
    public void received_an_email_to_reset_my_password(String name) {
        user = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        token = passwordResetService.createPasswordResetToken(user);

    }

    @When("I go to the given URL passed in the link")
    public void i_go_to_the_given_url_passed_in_the_link() {
        requestBuilder = MockMvcRequestBuilders.get(RESET_PASSWORD + token.getId());
    }

    @Then("I am asked to supply a new password with new password and retype password fields")
    public void i_am_asked_to_supply_a_new_password_with_new_password_and_retype_password_fields() throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("resetPassword"));
    }

    @Given("I am on the reset password form")
    @Sql(scripts = {"/sql/resetPassword"})
    public void i_am_on_the_reset_password_form() throws Exception {
        user = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        token = passwordResetService.createPasswordResetToken(user);
        requestBuilder = MockMvcRequestBuilders.get(RESET_PASSWORD + token.getId());
        mockMvc.perform(requestBuilder);

    }

    @Given("I enter two different passwords in new and retype password fields")
    public void i_enter_two_different_passwords_in_new_and_retype_password_fields() {
        requestBuilder = MockMvcRequestBuilders.post(RESET_PASSWORD + token.getId())
                .param(NEW_PASSWORD, TEST_PASSWORD)
                .param(RETYPE_PASSWORD, "IHopeThatThisIsNotTheSameAsThePreviousPlease");
    }

    @When("I hit the save button")
    public void i_hit_the_save_button() throws Exception {
        result = mockMvc.perform(requestBuilder);
    }

    @Then("an error message tells me the passwords do not match.")
    public void an_error_message_tells_me_the_passwords_do_not_match() throws Exception {
        result
                .andExpect(status().is4xxClientError());
    }

    @Given("I enter a weak password")
    public void i_enter_a_weak_password() throws Exception {
        requestBuilder = MockMvcRequestBuilders.post(RESET_PASSWORD + token.getId())
                .param(NEW_PASSWORD, "Passwor")
                .param(RETYPE_PASSWORD, "IHopeThatThisIsNotTheSameAsThePreviousPlease");
        result = mockMvc.perform(requestBuilder);
    }

    @Then("then an error message tells me the password is too weak and provides me with the requirements for a strong password.")
    public void then_an_error_message_tells_me_the_password_is_too_weak_and_provides_me_with_the_requirements_for_a_strong_password() throws Exception {
        result
                .andExpect(status().is4xxClientError());
    }

    @Given("I enter fully compliant details")
    public void i_enter_fully_compliant_details() throws Exception {
        requestBuilder = requestBuilder
                .param(NEW_PASSWORD, TEST_PASSWORD)
                .param(RETYPE_PASSWORD, TEST_PASSWORD);
        result = mockMvc.perform(requestBuilder);

    }

    @Then("my password is updated, and an email is sent to my email address to confirm that my password has been updated.")
    public void my_password_is_updated_and_an_email_is_sent_to_my_email_address_to_confirm_that_my_password_has_been_updated() throws Exception {
        result
                .andExpect(status().isOk());
    }

    @Given("A reset link was created")
    public void a_reset_link_was_created() {
        user = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        token = passwordResetService.createPasswordResetToken(user);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(token);
    }

    @When("One hour has passed since the link was created")
    public void one_hour_has_passed_since_the_link_was_created() {
        long hourAgo = Instant.now()
                .minus(1L, ChronoUnit.HOURS)
                .getEpochSecond();

        token.setTimeCreated(hourAgo);

        token = tokenRepository.save(token);
    }

    @Then("The reset token is deleted")
    public void the_reset_token_is_deleted() {
        @SuppressWarnings("Convert2Lambda") // suppressed due to lambdas being final and therefore un-spyable
        Consumer<Token> callback = Mockito.spy(new Consumer<Token>() {
            @Override
            public void accept(Token token) {
                // stub - just want to verify that this is called
            }
        });
        tokenDeletionService.deleteTokens(TokenType.PASSWORD_RESET, callback);
        Mockito.verify(callback, Mockito.times(1)).accept(token);
        Assertions.assertTrue(tokenRepository.findById(token.getId()).isEmpty());
    }

    @Then("It cannot be used to reset a password anymore")
    public void it_cannot_be_used_to_reset_a_password_anymore() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/resetPassword/" + token.getId())
                        .param("newPassword", "Password1@")
                        .param("retypePassword", "Password1@")
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }


}
