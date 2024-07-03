package nz.ac.canterbury.seng302.tab.controller;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Controller for the creating team form
 */
@Controller
public class CreateTeamFormController {
    Logger logger = LoggerFactory.getLogger(CreateTeamFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SportService sportService;

    @Autowired
    private MediaService mediaService;

    private static final String CREATE_TEAM_FORM = "createTeamForm";

    private final List<String> errorMessages = Arrays.asList("Team name must be at least 3 chars long as well as consist of only: alphanumeric characters, dots, and curly braces",
            "Sport must start with a letter and after only contain letters and spaces",
            "Country must include only alphabetic characters",
            "City must include only alphabetic characters");

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param team the team object
     * @param model (map-like) representation of teamName and sport for use in thymeleaf
     * @return thymeleaf createTeamFormTemplate
     */
    @GetMapping("/create_team")
    public String form(Team team, Location location, Model model) {
        logger.info("GET /create_team");

        Set<Sport> sports = sportService.getAllSports();

        model.addAttribute("team", team);
        model.addAttribute("location", location);
        model.addAttribute("sports", sports);


        UserEntity userProfilePicture = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("userProfilePicture", userProfilePicture);
        model.addAttribute("errorMessages", errorMessages);
        return CREATE_TEAM_FORM;
    }

    /**
     * Posts a form response with team name and sport
     * @param team the team form object
     * @param teamResult The error result of the team
     * @param location the location of the team
     * @param locationResult location validator result
     * @param file profile picture
     * @return either the thymeleaf createTeamFormTemplate or a redirect to a list of teams
     */
    @PostMapping("/create_team")
    public String createTeam(@Valid Team team,
                                         BindingResult teamResult,
                                         @Valid Location location,
                                         BindingResult locationResult,
                                         @RequestParam(value = "sport", required = false) String sportId,
                                         @RequestParam(value = "imageUpload", required = false) MultipartFile file,
                                         Model model) {
        boolean sportValid = false;
        model.addAttribute("errorMessages", errorMessages);
        Set<Sport> sports = sportService.getAllSports();
        model.addAttribute("sports", sports);
        model.addAttribute("selectedSport", sportId);


        try {
            Sport sport = sportService.findById(Long.parseLong(sportId));
            team.setSport(sport);
            sportValid = true;
        } catch (NumberFormatException e) {
            model.addAttribute("sportError", "Sport is required!");
        }


        if (locationResult.hasErrors() || teamResult.hasErrors() || !sportValid) {
            return CREATE_TEAM_FORM;
        }

        Location savedLocation = locationService.checkLocationExists(location);

        // Compose team with location
        team.setLocation(savedLocation);
        // Generate string for team Joining
        teamService.generateNewToken(team);

        // Set the current user as the team's manager
        UserEntity user = userService.getLoggedInUser();
        team.addManager(user);

        // Save to get the id
        Team savedTeam = teamService.addTeam(team);

        if(!file.isEmpty()) {
            if (!mediaService.uploadTeamImage(file, model, savedTeam)) {
                teamService.deleteTeamById(savedTeam.getId());
                return CREATE_TEAM_FORM;
            } else {
                teamService.addTeam(savedTeam);
            }
        }

        return "redirect:/teams/" + savedTeam.getId().toString();
    }


}
