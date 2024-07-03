package nz.ac.canterbury.seng302.tab.controller;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CreateClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserEntity user;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void setup() {

        user = new UserEntity("password", "firstName", "lastName",
                "email@example.com", "1970-01-01", Set.of(), null);

        user.grantAuthority("ROLE_USER");
        user.hashPassword(this.passwordEncoder);

        userRepository.save(user);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_invalidClubName_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                .with(csrf())
                .param("name", "....") // This is an invalid input
                .param("description", "32$") //This is a valid input
                .param("city", "Christchurch")
                .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_symbolInDescription_clubCreated() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Team") // This is a valid input
                        .param("description", "aa✦") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_emptyName_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "") // This is an invalid input
                        .param("description", "✦") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_emptyDescription_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Club") // This is a valid input
                        .param("description", "") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_longName_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "adsadsaasdasadadadsadsadsadsaadsadsaa" +
                                "sdasadadadasadas") // This is an invalid input
                        .param("description", "description") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_longDescription_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Club") // This is a valid input
                        .param("description", "adsadsaasdasadadadsadsadsadsaadsadsaasdasadadadas" +
                                "adasadsadsaasdasadadadsadsadsadsaadsadsaasdasadadadasadasadsadsaasdasadadadsadsad" +
                                "sadsaadsadsaasdasadadadasadas") //This is an invalid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_apostrophesInName_clubIsCreated() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Club") // This is a valid input
                        .param("description", "Valid description") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "New Zealand"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_emptyCity_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Club") // This is a valid input
                        .param("description", "Valid description") //This is a valid input
                        .param("city", "") // Invalid empty city
                        .param("country", "New Zealand"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void clubCreation_emptyCountry_unableToCreateClub() throws Exception {
        this.mockMvc.perform(post("/club/create")
                        .with(csrf())
                        .param("name", "Ben's Club") // This is a valid input
                        .param("description", "Valid description") //This is a valid input
                        .param("city", "Christchurch")
                        .param("country", "")) // Invalid empty country
                .andExpect(status().isOk())
                .andExpect(view().name("club/createClub.html")); // Has error
    }

}
