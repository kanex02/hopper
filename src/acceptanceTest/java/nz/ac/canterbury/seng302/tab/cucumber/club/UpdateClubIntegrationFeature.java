package nz.ac.canterbury.seng302.tab.cucumber.club;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@DataJpaTest
public class UpdateClubIntegrationFeature {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ClubRepository clubRepository;
    
    MockHttpServletRequestBuilder requestBuilder;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private UserService userService;

    private String description = "description";

    private String clubErrors = "clubErrors";
    
    @Given("I am editing my club's details")
    public void i_am_editing_my_club_s_details() throws Exception {
        UserEntity user;
        String email = "test@example.com";
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
        user.setId(1L);
        user.hashPassword(passwordEncoder);
        user.confirmEmail();
        userService.updateUser(user);
    
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    
        SecurityContextHolder.getContext().setAuthentication(auth);
    
        mockMvc.perform(post("/club/create")
            .with(csrf())
            .param("name", "New Club")
            .param("sport", "1")
            .param(description, "A unique description for test")
            .param("city", "Brisbane")
            .param("country", "Uganda"));
    
        requestBuilder = MockMvcRequestBuilders.post("/club/1");
    }
    
    @When("I submit the changes to my club's details")
    public void i_submit_the_changes_to_my_club_s_details() {
        
        requestBuilder = requestBuilder.with(csrf())
                            .param("name", "Brand New Name")
                            .param(description, "Brand New Description");
        
    }
    
    @Then("I am brought back to my club's homepage with the changes visible")
    public void i_am_brought_back_to_my_club_s_homepage_with_the_changes_visible() throws Exception {
        
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
            .andExpect(status().is3xxRedirection())
            .andReturn();
    
        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
    
        assert redirectedUrl != null;
        mockMvc.perform(get(redirectedUrl))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("club"))
            .andExpect(model().attribute("club", hasProperty("name", is("Brand New Name"))))
            .andExpect(model().attribute("club", hasProperty(description, is("Brand New Description"))));
    }
    
    @When("I enter invalid values and submit the form")
    public void i_enter_invalid_values_and_submit_the_form() {
        requestBuilder = requestBuilder.with(csrf())
            .param("name", "/")
            .param(description, "");
    }
    
    @Then("I see error messages saying the fields contain invalid values")
    public void i_see_error_messages_saying_the_fields_contain_invalid_values() throws Exception {
        
        mockMvc.perform(requestBuilder)
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("club"))
            .andExpect(flash().attributeExists("hasErrors"))
            .andExpect(flash().attribute("hasErrors", is(true)))
            .andExpect(flash().attributeExists(clubErrors))
            .andExpect(flash().attribute(clubErrors, hasKey("name")))
            .andExpect(flash().attribute(clubErrors, hasKey(description)))
            .andReturn();
    }
}
