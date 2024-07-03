package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * API endpoint for following a team
 */
@RestController
@RequestMapping("/api/follow")
public class FollowAPIController {
    
    @Autowired
    private TeamService teamService;

    @Autowired
    private ClubService clubService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Ensures that the user cannot navigate to the follow team page
     */
    @GetMapping("/team/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTeamGetRequests() {
        // This will return a 404 status code.
    }

    /**
     * Ensures that the user cannot navigate to the follow club page
     */
    @GetMapping("/club/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleClubGetRequest() {
        // This will return a 404 status code.
    }

    /**
     * Ensures that the user cannot navigate to the follow user page
     */
    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleUserGetRequest() {
        // This will return a 404 status code.
    }

    /**
     * Follows or unfollows a team
     *
     * @param id id of the team
     * @return response of the request
     */
    @PostMapping("/team/{id}")
    public ResponseEntity<String> toggleFollowTeam(@PathVariable(value = "id", required = true) Long id) {

        try {
            Optional<Team> optionalTeamToFollow = teamService.getTeamById(id);
            if (optionalTeamToFollow.isEmpty()) {
                return new ResponseEntity<>("Team not found", HttpStatus.NOT_FOUND);
            }
            
            Team teamToFollow = optionalTeamToFollow.get();
            UserEntity loggedInUser = userService.getLoggedInUser();
            
            teamService.followOrUnfollowTeam(teamToFollow, loggedInUser);
            
            teamService.save(teamToFollow);
            userService.updateUser(loggedInUser);
            
            return new ResponseEntity<>("Follow status for team updated", HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Follows or unfollows a club
     *
     * @param id of the club
     * @return response of the request
     */
    @PostMapping("/club/{id}")
        public ResponseEntity<String> toggleFollowClub(@PathVariable(value = "id", required = true) Long id) {

        try {
            Club clubToFollow = clubService.findById(id);
            if (clubToFollow == null) {
                return new ResponseEntity<>("Club not found", HttpStatus.NOT_FOUND);
            }

            UserEntity loggedInUser = userService.getLoggedInUser();

            clubService.followOrUnfollowClub(clubToFollow, loggedInUser);

            clubService.save(clubToFollow);
            userService.updateUser(loggedInUser);

            return new ResponseEntity<>("Follow status for club updated", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggles the status of the logged-in user following the viewed user
     *
     * @param id The id of the user to follow
     * @return A void response entity with an OK status
     */
    @PostMapping("/user/{id}")
    public ResponseEntity<Void> toggleFollowUser(@PathVariable(value = "id") String id) {

        try {
            UserEntity viewedUser = userService.getUserById(Long.parseLong(id));
            if (viewedUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            UserEntity loggedInUser = userService.getLoggedInUser();

            if (userService.isUserFollowing(loggedInUser, viewedUser)) {
                loggedInUser.unfollow(viewedUser);
            } else {
                loggedInUser.follow(viewedUser);
            }

            userService.updateUser(loggedInUser);
            userService.updateUser(viewedUser);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
