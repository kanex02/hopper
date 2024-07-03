package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Generates a new token for the team and persists it.
 * The {@link RequestMapping} annotation defines the path to listen on.
 */
@RestController
@RequestMapping("generateToken")
public class TokenController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    /**
     * Generates a new token.
     * @param id ID of the team to generate a token for.
     * @return The new token.
     */
    @GetMapping
    public String generateToken(@RequestParam(value = "id", required = true) String id) {
        // Serialize Java as a JSON output
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.getUserByEmail(email);
        Optional<Team> result = teamService.getTeamById(Long.parseLong(id));
        if (result.isEmpty()) {
            throw new NoSuchElementException();
        }
        Team team = result.get();
        if (!team.getManagers().contains(user)) {
            LOGGER.error("Unauthorized call to generate token by user {}", user.getEmail());
            return "";
        }
        return "\""+teamService.generateNewToken(team)+"\"";
    }
}
