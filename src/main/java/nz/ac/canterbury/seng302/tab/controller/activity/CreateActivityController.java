package nz.ac.canterbury.seng302.tab.controller.activity;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.InvalidFormException;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controller for the create activity page
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class CreateActivityController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private Environment environment;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateActivityController.class);
    
    /**
     * Controller method for getting the activity creation form
     * @param model model to add attributes to
     * @param activity activity object
     * @param team team object
     * @return model to be rendered
     */
    @GetMapping("/activity/create")
    public String createActivity(Model model,
                                 Activity activity,
                                 Location location,
                                 Team team) {
        LOGGER.info("GET /activity/create");
        
        UserEntity user = userService.getLoggedInUser();
        List<Team> teams = Stream.concat(user.getCoachedTeams().stream(),
            user.getManagedTeams().stream()).distinct().toList();
        
        model.addAttribute("teams", teams);
        model.addAttribute("activity", activity);
        model.addAttribute("location", location);
        model.addAttribute("team", team);
        model.addAttribute("user", user);
        
        model.addAttribute("basePath", environment.getProperty("app.baseUrl"));
        
        return "activity/createActivity.html";
    }
    
    /**
     * Handles the HTTP POST request to create a new activity.
     * This method also supports adding a list of users as the lineup and substitute players in the activity.
     * After validation of the activity, user, lineup and substitutes, the activity is saved and the
     * players are added to the activity's lineup.
     *
     * @param activity The activity to create. An activity has its location set in Thymeleaf directly.
     * @param activityResult The result of validating the activity.
     * @param teamId The ID of the team associated with the activity, if any.
     * @param lineupList Comma separated string of user ids who will be the main players in the activity.
     * @param subList Comma separated string of user ids who will be the substitute players in the activity.
     * @param model The model to be used for rendering the view.
     * @return The name of the view to be rendered, which displays the details of the newly created activity.
     * @throws InvalidFormException If the given arguments are invalid for creating the activity.
     */
    @PostMapping("/activity/create")
    public String form(@Valid @ModelAttribute Activity activity,
                       BindingResult activityResult,
                       @Valid Location location,
                       BindingResult locationResult,
                       @RequestParam("team") String teamId,
                       @RequestParam("lineupList") String lineupList,
                       @RequestParam("subList") String subList,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        // Set the activity team if a team with the given ID is found. Some activities may not require a team.
        if (!teamId.isBlank()) {
            try {
                Optional<Team> teamResult = teamService.getTeamById(Long.parseLong(teamId));
                teamResult.ifPresent(activity::setTeam);
            } catch (Exception ignored) {
                LOGGER.warn("Invalid team id passed when creating activity!");
                throw new InvalidFormException("Invalid arguments passed when creating activity!");
            }
        }
        
        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());


        boolean isValid = activityService.isValid(activity, user, activityResult);
        boolean isLocationValid = activityService.isLocationValid(location, locationResult);


        if (!isValid || !isLocationValid) {
            List<Team> teams = Stream.concat(user.getCoachedTeams().stream(),
                    user.getManagedTeams().stream()).distinct().toList();

            model.addAttribute("teams", teams);
            return "activity/createActivity.html";
        }

        activity.setUser(user);
        activity.setLocation(location);
        activityService.saveActivity(activity);

        List<Long> starterIds = new ArrayList<>();
        List<Long> substituteIds = new ArrayList<>();

        if(!lineupList.isEmpty()) {
            starterIds = ActivityService.convertStringToLongList(lineupList);
        }

        if(!subList.isEmpty()) {
            substituteIds = ActivityService.convertStringToLongList(subList);
        }

        if(!starterIds.isEmpty()) {
            activityService.addPlayersToActivityLineup(activity, LineupRole.STARTER, starterIds);
        }
        
        if(!substituteIds.isEmpty()) {
            activityService.addPlayersToActivityLineup(activity, LineupRole.SUB, substituteIds);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Activity created successfully!");
        return "redirect:/activity/" + activity.getId();
    }
}
