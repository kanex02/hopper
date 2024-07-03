package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EditTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    private Team team;


    @MockBean
    private UserService userService;

    @MockBean
    private TeamService teamService;

    private UserEntity user;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SportRepository sportRepository;

    @MockBean
    private SportService sportService;

    private MockMultipartFile file;

    private Sport sport;

    @BeforeEach
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void setup() throws IOException {

        file = new MockMultipartFile(
                "imageUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );

        user = new UserEntity("password", "firstName", "lastName",
                "email@example.com", "1970-01-01", Set.of(), null);
        user.grantAuthority("ROLE_USER");
        user.hashPassword(this.passwordEncoder);

        team = Team.createTestTeam();
        team.getManagers().add(user);

        sport = new Sport();
        sportRepository.save(sport);

        userRepository.save(user);
        teamRepository.save(team);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
    }



    @ParameterizedTest
    @ValueSource(strings = {
            "new team name",
            "はははは",
            "Ben's Team",
            "✦✦✦✦"
    })
    @WithMockUser
    void editTeam_blueSky_teamSuccessfullyUpdated() throws Exception {

        when(sportService.findById(any())).thenReturn(sport);
        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));

        this.mockMvc.perform(
                        multipart("/edit_team/1")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New Team Name")
                                .param("sport", "1")
                                .param("city", "Novigrad")
                                .param("country", "Kingdom of Redania")
                )
                .andExpect(status().is3xxRedirection());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "はは", // Too short
            "a",
            "ab!",
            "     ",
            """
                adsadsaasdasadadadsadsadsadsaadsaasdsadasdasdsadasdd        
            """
    })
    @WithMockUser
    void editTeam_invalidTeamName_modelHasErrors(String teamName) throws Exception {

        when(sportService.findById(any())).thenReturn(sport);
        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));

        this.mockMvc.perform(
                        multipart("/edit_team/1")
                                .file(file)
                                .with(csrf())
                                .param("teamName", teamName)
                                .param("sport", "1")
                                .param("city", "Novigrad")
                                .param("country", "Kingdom of Redania")
                )
                .andExpect(flash().attributeExists("teamNameError"));
    }

    @Test
    @WithMockUser
    void editTeam_invalidCity_modelHasErrors() throws Exception {

        when(sportService.findById(any())).thenReturn(sport);
        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));
        when(userService.getLoggedInUser()).thenReturn(user);

        this.mockMvc.perform(
                        multipart("/teams/1/edit-location")  // updated URL
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New team name")
                                .param("sport", "1")
                                .param("city", "")
                                .param("country", "Kingdom of Redania")
                )
                .andExpect(flash().attributeExists("fieldErrors"));
    }

    @Test
    @WithMockUser
    void editTeam_invalidCountry_modelHasErrors() throws Exception {

        when(sportService.findById(any())).thenReturn(sport);
        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));
        when(userService.getLoggedInUser()).thenReturn(user);

        this.mockMvc.perform(
                        multipart("/teams/1/edit-location")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New team name")
                                .param("sport", "1")
                                .param("city", "Christchurch")
                                .param("country", "")
                )
                .andExpect(flash().attributeExists("fieldErrors"));
    }

    @Test
    @WithMockUser
    void editTeam_userNotManager_authErrorExists() throws Exception {
        when(sportService.findById(any())).thenReturn(sport);
        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));
        when(userService.getLoggedInUser()).thenReturn(user);

        team.getManagers().remove(user);

        this.mockMvc.perform(
                        multipart("/teams/1/edit-location")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New team name")
                                .param("sport", "1")
                                .param("city", "Christchurch")
                                .param("country", "NZ")
                )
                .andExpect(flash().attributeExists("authError"));
    }

    @Test
    @WithMockUser
    void editTeam_invalidTeamId_404Page() throws Exception {

        this.mockMvc.perform(
                        multipart("/edit_team/999")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New team name")
                                .param("sport", "1")
                                .param("city", "Christchurch")
                                .param("country", "NZ")
                )
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithMockUser
    void editTeam_invalidSportId_404Page() throws Exception {

        when(teamService.getTeamById(anyLong())).thenReturn(Optional.ofNullable(team));

        this.mockMvc.perform(
                        multipart("/edit_team/1")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "New team name")
                                .param("sport", "999")
                                .param("city", "Christchurch")
                                .param("country", "NZ")
                )
                .andExpect(view().name("error/404"));
    }
}