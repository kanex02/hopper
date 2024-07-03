package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FollowTeamControllerTest {
    
    @InjectMocks
    FollowAPIController followAPIController;
    
    @Mock
    TeamService teamService;
    
    @Mock
    UserService userService;
    
    @Test
    void invalidTeam_followTeam_teamNotFound() {
        when(teamService.getTeamById(any())).thenReturn(Optional.empty());
        
        ResponseEntity<String> response = followAPIController.toggleFollowTeam(1L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void validTeam_followOrUnfollowTeam_success() {
        Team team = Team.createTestTeam();
        UserEntity user = new UserEntity();
        
        when(teamService.getTeamById(any())).thenReturn(Optional.of(team));
        when(userService.getLoggedInUser()).thenReturn(user);
        
        ResponseEntity<String> response = followAPIController.toggleFollowTeam(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void exceptionReceived_followOrUnfollowTeam_internalServerError() {
        when(teamService.getTeamById(any())).thenThrow(new RuntimeException("An error occurred"));
        
        ResponseEntity<String> response = followAPIController.toggleFollowTeam(1L);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
}
