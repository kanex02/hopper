package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.controller.api.JsonUser;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * A RESTful API Controller class for managing team-related data.
 */
@RestController
@RequestMapping("/teams/getTeamMembers")
public class TeamAPIController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TeamAPIController.class);
    
    @Autowired
    TeamService teamService;
    
    /**
     * API endpoint for fetching all members of a team as JSON data.
     * Note: This is a REST API endpoint that returns application/json data.
     * It is not intended to be loaded as an HTML/Thymeleaf page.
     *
     * @param teamId The ID of the team whose members are to be fetched.
     * @return A JSON string representation of a list of the team members. Each member is represented by a JsonUser object.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTeamMembers(@RequestParam("teamId") Long teamId) {
        
        Optional<Team> result = teamService.getTeamById(teamId);
        
        if (result.isEmpty()) {
            throw new NoSuchElementException();
        }
        
        Team team = result.get();
        
        Set<UserEntity> userList = team.getMembers();
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        List<JsonUser> jsonUsers = new ArrayList<>();
        String json = "";
        
        for(UserEntity user: userList) {
            JsonUser newUser = new JsonUser(user);
            jsonUsers.add(newUser);
        }
        
        try {
            // Convert list of users to JSON
            json = objectMapper.writeValueAsString(jsonUsers);
        } catch (JsonProcessingException e) {
            logger.error("Error converting list of users to JSON: {}", e.getMessage());
        }
        
        return json;
    }
}