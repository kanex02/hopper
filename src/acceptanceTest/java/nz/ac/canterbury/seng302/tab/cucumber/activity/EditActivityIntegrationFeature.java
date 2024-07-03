package nz.ac.canterbury.seng302.tab.cucumber.activity;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EditActivityIntegrationFeature {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityService activityServiceMock;

    private Activity activity;

    private final String ACTIVITY_START_TIME = "2024-05-15T11:43:09";

    private final String ACTIVITY_END_TIME = "2024-06-15T11:43:09";

    private UserEntity user;

    ResultActions result;

    MockHttpServletRequestBuilder requestBuilder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TeamRepository teamRepository;

    @Given("I am on the edit activity page as a valid user")
    public void i_am_on_the_edit_activity_page_as_a_valid_user() throws Exception {
        UserEntity mockUser = authenticateTestUser(authenticationManager, passwordEncoder, userService);
        requestBuilder = MockMvcRequestBuilders.post("/activity/create");
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "TRAINING")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane2")
                .param("postcode", "3333");
        result = mockMvc.perform(requestBuilder);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/activity/edit/2")
        ).andExpect(status().isOk());
        requestBuilder = MockMvcRequestBuilders.post("/activity/edit/2");

    }

    @When("I enter valid values and click the save activity button")
    public void i_enter_valid_values_and_click_the_save_activity_button() throws Exception {


        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "OTHER")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }

    @Then("The activity is updated and I am shown the activitys page")
    public void the_activity_is_updated_and_i_am_shown_the_activitys_page() throws Exception {
        result.andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/activity/2"));
    }

    @Given("I try to navigate to the edit activity page of a non-existent Activity")
    public void i_try_to_navigate_to_the_edit_activity_page_of_a_non_existent_activity()
    {
        requestBuilder = MockMvcRequestBuilders.get("/activity/edit/test");
        UserEntity mockUser = authenticateTestUser(authenticationManager, passwordEncoder, userService);
    }

    @When("I should be taken to the page")
    public void i_should_be_taken_to_the_page() throws Exception {
        result = mockMvc.perform(requestBuilder);
    }

    @Then("I am taken to an error page")
    public void i_am_taken_to_an_error_page() throws Exception {
        result.andExpect(status().is4xxClientError());

    }

    @Given("I try to navigate the edit activity page as an invalid user")
    public void i_try_to_navigate_the_edit_activity_page_as_an_invalid_user()
    {
        requestBuilder = MockMvcRequestBuilders.get("/activity/edit/0");
        UserEntity mockUser = authenticateTestUser(authenticationManager, passwordEncoder, userService);
    }

    @Then("I am taken to an error page telling me I do not have permission to edit this activity")
    public void i_am_taken_to_an_error_page_telling_me_i_do_not_have_permission_to_edit_this_activity() throws Exception {
        result.andExpect(status().isOk())
                .andExpect(view().name("error/404"));

    }

    @When("I enter valid values but no team with activity type set to GAME and click the save activity button")
    public void i_enter_valid_values_but_no_team_with_activity_type_set_to_GAME_and_click_the_save_activity_button() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "GAME")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }

    @When("I enter valid values but no team with activity type set to FRIENDLY and click the save activity button")
    public void i_enter_valid_values_but_no_team_with_activity_type_set_to_FRIENDLY_and_click_the_save_activity_button() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "FRIENDLY")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }

    @Then("I am shown an error that I need a team for this activity")
    public void i_am_shown_an_error_that_i_need_a_team_for_this_activity() throws Exception {
        result.andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activity", "team"));
    }

    @When("I enter valid values but no description and click the save activity button")
    public void i_enter_valid_values_but_no_description_and_click_the_save_activity_button() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "OTHER")
                .param("description", "")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }
    @Then("I am shown an error that I need a description for this activity")
    public void i_am_shown_an_error_that_i_need_a_description_for_this_activity() throws Exception {
        result.andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activity", "description"));
    }

    @When("I enter valid values with invalid start time and click the save activity button")
    public void i_enter_valid_values_with_invalid_start_time_and_click_the_save_activity_button() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "OTHER")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", "15T11:43:09")
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }
    @Then("I am shown an error that I need a valid start time for this activity")
    public void i_am_shown_an_error_that_i_need_a_valid_start_time_for_this_activity() throws Exception {
        result.andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activity", "startTime"));
    }

    @When("I enter valid values with invalid end time and click the save activity button")
    public void i_enter_valid_values_with_invalid_end_time_and_click_the_save_activity_button() throws Exception {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "OTHER")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("lineupList", "[]")
                .param("subList", "[]")
                .param("city", "Brisbane")
                .param("country", "Uganda")
                .param("addressLine1", "Brisbane")
                .param("postcode", "3333");

        result = mockMvc.perform(requestBuilder);
    }
    @Then("I am shown an error that I need a valid end time for this activity")
    public void i_am_shown_an_error_that_i_need_a_valid_end_time_for_this_activity() throws Exception {
        result.andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activity", "endTime"));
    }
}
