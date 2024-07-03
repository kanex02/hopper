package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUserControllerTest {
    @InjectMocks
    FollowAPIController followAPIController;

    @Mock
    UserService userService;

    @Test
    void validUser_followOrUnfollowUser_success() {
        UserEntity user = new UserEntity();
        UserEntity userToFollow = new UserEntity();

        when(userService.getLoggedInUser()).thenReturn(user);
        when(userService.getUserById(2L)).thenReturn(userToFollow);

        ResponseEntity<Void> response = followAPIController.toggleFollowUser("2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void invalidUser_followOrUnfollowUser_userNotFound() {
        when(userService.getUserById(any())).thenReturn(null);

        ResponseEntity<Void> response = followAPIController.toggleFollowUser("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void parseError_followOrUnfollowUser_internalServerError() {
        ResponseEntity<Void> response = followAPIController.toggleFollowUser("test");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
