package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CreateActivityControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    private UserEntity user;
    
    @Autowired
    private UserRepository userRepository;
    
    private Activity activity;
    
    private Team team;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private ActivityService activityService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setup () {
    
        user = new UserEntity("password", "firstName", "lastName",
            "email@example.com", "1970-01-01", Set.of(), null);
        
        team = Team.createTestTeam();
        team.setManagers(Set.of(user));
        
        user.grantAuthority("ROLE_USER");
        user.hashPassword(this.passwordEncoder);
    
        userRepository.save(user);
        teamRepository.save(team);
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startTime = LocalDateTime.now().plusHours(1).format(formatter);
        String endTime = LocalDateTime.now().plusHours(2).format(formatter);
        
        activity = new Activity("new activity", startTime,
            endTime, ActivityType.GAME, team);
        activity.setUser(user);
    }
    
//    @Test
//    @WithMockUser(username = "email@example.com" , roles = "USER")
//    public void createActivity_withStartingLineup_activityAndLineupCreated() throws Exception {
//
//        Mockito.when(userService.getUserByEmail(Mockito.anyString())).thenReturn(user);
//        Mockito.when(activityService.isValid(any(Activity.class),
//            any(UserEntity.class), any(BindingResult.class))).thenReturn(true);
//
//        List<Long> longList = List.of(1L, 388L, 389L);
//
//        Mockito.when(activityService.convertStringToLongList("[1, 388, 389]")).thenReturn(longList);
//
//        mockMvc.perform(post("/activity/create")
//                .with(csrf())
//                .param("description", activity.getDescription())
//                .param("startTime", activity.getStartTime())
//                .param("endTime", activity.getEndTime())
//                .param("type", activity.getType().toString())
//                .param("team", activity.getTeam().getId().toString())
//                .param("lineupList", "[1, 388, 389]")
//                .param("subList", "[]"))
//            .andExpect(status().isOk());
//
//        Mockito.verify(activityService).saveActivity(argThat(argument ->
//            activity.getDescription().equals(argument.getDescription())
//                && activity.getStartTime().equals(argument.getStartTime())
//                && activity.getEndTime().equals(argument.getEndTime())
//                && activity.getType().equals(argument.getType())
//                && activity.getTeam().equals(argument.getTeam())
//        ));
//
//        Mockito.verify(activityService, Mockito.times(1)).addPlayersToActivityLineup(
//            any(Activity.class), eq(LineupRole.STARTER), Mockito.anyList());
//
//    }
}

