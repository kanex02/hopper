package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FollowClubControllerTest {

    @InjectMocks
    FollowAPIController followAPIController;

    @Mock
    ClubService clubService;

    @Mock
    UserService userService;

    @Test
    void invalidClub_followClub_clubNotFound() {
        when(clubService.findById(any())).thenReturn(null);

        ResponseEntity<String> response = followAPIController.toggleFollowClub(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void validClub_followOrUnfollowClub_success() {
        Club club = new Club("name", "description");
        UserEntity user = new UserEntity();

        when(clubService.findById(any())).thenReturn(club);
        when(userService.getLoggedInUser()).thenReturn(user);

        ResponseEntity<String> response = followAPIController.toggleFollowClub(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void exceptionReceived_followOrUnfollowClub_internalServerError() {
        when(clubService.findById(any())).thenThrow(new RuntimeException("An error occurred"));

        ResponseEntity<String> response = followAPIController.toggleFollowClub(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}