package nz.ac.canterbury.seng302.tab.controller.integration;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChallengeControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ChallengeService challengeService;

    @Test
    @WithMockUser
    void testGetView() throws Exception {

        String email = "test@example.com";
        String password = "Password1@";
        UserEntity mockUser = new UserEntity(
                password,
                "Fabian",
                "Gilson",
                email,
                "1900-01-01",
                Set.of(),
                null
        );
        mockUser.setId(1L);

        List<Challenge> mockChallenges = List.of(
                new Challenge(mockUser, LocalDateTime.now(), "test title", "do something", 30)
        );
        List<Challenge> mockCompletedChallenges = List.of();

        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(challengeService.getAllAvailableChallengesForUser(mockUser)).thenReturn(mockChallenges);
        when(challengeService.getAllCompletedChallengesForUser(mockUser)).thenReturn(mockCompletedChallenges);

        mockMvc.perform(get("/challenge/view"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("completedChallenges", mockCompletedChallenges))
                .andExpect(view().name("challenge/view.html"));
    }

}
