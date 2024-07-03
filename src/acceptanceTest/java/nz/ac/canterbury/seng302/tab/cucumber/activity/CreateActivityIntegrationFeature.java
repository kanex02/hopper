package nz.ac.canterbury.seng302.tab.cucumber.activity;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DataJpaTest
public class CreateActivityIntegrationFeature {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ActivityRepository activityRepository;

    ResultActions result;

    MockHttpServletRequestBuilder requestBuilder;

    @Given("I am on the create activity form")
    public void i_am_on_the_create_activity_form() throws Exception {
        // Make sure that the page works
        mockMvc.perform(
                MockMvcRequestBuilders.get("/activity/create")
        ).andExpect(status().is3xxRedirection());

        requestBuilder = MockMvcRequestBuilders.post("/activity/create");
    }

    @Given("I specify a valid activity and an address")
    public void i_specify_an_address() {
        requestBuilder = requestBuilder
                .with(csrf())
                .param("type", "Other")
                .param("description", "A unique description for CreateActivityIntegrationTests")
                .param("team", "")
                .param("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("location.city", "Brisbane")
                .param("location.country", "Uganda");
    }

    @When("I submit the form")
    public void i_submit_the_form() throws Exception {
        result = mockMvc.perform(requestBuilder);
    }
    @Then("I do not receive any errors")
    public void i_do_not_receive_any_errors() throws Exception {
        result.andExpect(status().is3xxRedirection());
    }
}
