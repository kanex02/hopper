package nz.ac.canterbury.seng302.tab.cucumber.club;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DataJpaTest
public class CreateClubIntegrationFeature {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ClubRepository clubRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    ResultActions result;
    
    MockHttpServletRequestBuilder requestBuilder;

    @Given("I am on the home page and I want to create a club")
    public void i_am_on_the_home_page_and_i_want_to_create_a_club() throws Exception {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/home")
        ).andExpect(status().isOk());
    }

    @Given("I am on the club creation form")
    public void i_am_on_the_club_creation_form() throws Exception {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/club/create")
        ).andExpect(status().isOk());
    
        requestBuilder = MockMvcRequestBuilders.post("/club/create");
    }
    
    @Given("I specify a valid name, description, teamIds, sport, city and country")
    public void i_specify_a_valid_name_description_team_ids_sport_city_and_country() {
        requestBuilder = requestBuilder
            .with(csrf())
            .param("name", "New Club")
            .param("sport", "1")
            .param("description", "A unique description for test")
            .param("city", "Brisbane")
            .param("country", "Uganda");
    }
    
    @When("I submit the club creation form")
    public void i_submit_the_club_creation_form() throws Exception {
        result = mockMvc.perform(requestBuilder);
    }

    @When("I navigate to the club creation page")
    public void i_navigate_to_the_club_creation_page() {
        requestBuilder = MockMvcRequestBuilders.get("/club/create");
    }
    
    @Then("The club is created")
    public void the_club_is_created() {
        Club club = clubRepository.findClubByName("New Club");
        assertNotNull(club);
        assertEquals("New Club", club.getName());
        assertEquals("A unique description for test", club.getDescription());
        assertEquals("Brisbane", club.getLocation().getCity());
        assertEquals("Uganda", club.getLocation().getCountry());
    }
    
    @Then("I am redirected to the club's homepage")
    public void i_am_redirected_to_the_club_s_homepage() throws Exception {
        result
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/club/1"));
    }

    @Then("I am directed to the club creation form")
    public void i_am_directed_to_the_club_creation_form() throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
