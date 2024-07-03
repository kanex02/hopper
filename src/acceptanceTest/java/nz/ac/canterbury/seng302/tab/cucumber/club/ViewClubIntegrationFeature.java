package nz.ac.canterbury.seng302.tab.cucumber.club;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@DataJpaTest
public class ViewClubIntegrationFeature {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ClubRepository clubRepository;
    
    MockHttpServletRequestBuilder requestBuilder;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    private UserEntity user;
    
    @Autowired
    private UserService userService;
    
    @Given("I am on the club homepage")
    public void i_am_on_the_club_homepage() throws Exception {
    
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
            .param("description", "A unique description for test")
            .param("city", "Brisbane")
            .param("country", "Uganda"));
    
        requestBuilder = MockMvcRequestBuilders.get("/club/1");
    }
    
    @Then("I can see the details of the club")
    public void i_can_see_the_details_of_the_club() throws Exception {
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(view().name("club/clubPage.html"))
            .andExpect(model().attributeExists("club"))
            .andExpect(model().attribute("club", hasProperty("name", is("New Club"))))
            .andExpect(model().attribute("club", hasProperty("description", is("A unique description for test"))));
    }
    
}
