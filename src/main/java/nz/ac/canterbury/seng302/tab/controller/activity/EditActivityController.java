package nz.ac.canterbury.seng302.tab.controller.activity;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.InvalidFormException;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.LineupService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Controller for the edit activity page
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class EditActivityController {


    private final UserService userService;

    private final ActivityService activityService;

    private final TeamService teamService;

    private final LineupService lineupService;

    private final Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditActivityController.class);
    private static final String ACTIVITY = "activity";

    public EditActivityController(UserService userService, ActivityService activityService, TeamService teamService, LineupService lineupService, Environment environment) {
        this.userService = userService;
        this.activityService = activityService;
        this.teamService = teamService;
        this.lineupService = lineupService;
        this.environment = environment;
    }

    /**
     * Handles the GET request for the edit activity form.
     * <p>
     * Fetches the existing activity and its associated team and lineup, if available.
     * Also fetches the list of teams the user manages/coaches.
     *
     * @param model Model object to use in the view.
     * @param activityId ID of the activity to edit.
     * @return String name of the view.
     */
    @GetMapping("/activity/edit/{activityId}")
    public String editActivity(Model model,
                               @PathVariable("activityId") Long activityId) {
        LOGGER.info("GET /activity/edit");

        Activity existingActivity = activityService.getActivityById(activityId);
        UserEntity user = userService.getLoggedInUser();
        
        if (existingActivity == null) {
            return "error/404";
        }
        
        if (user != existingActivity.getUser() && existingActivity.getTeam() == null) {
            return "error/403";
        }

        if (existingActivity.getTeam() != null &&
            !existingActivity.getTeam().getCoaches().contains(user) &&
            !existingActivity.getTeam().getManagers().contains(user)) {
            return "error/403";
        }

        Team team = existingActivity.getTeam();
        if(team != null) {
            // get existing lineups
            List<UserEntity> starters = lineupService.getStartingLineup(activityId);
            List<UserEntity> subs = lineupService.getSubstituteLineup(activityId);
            model.addAttribute("starters", lineupService.convertUserToJsonEquivalent(starters));
            model.addAttribute("subs", lineupService.convertUserToJsonEquivalent(subs));

            // get remaining players (the ones not in the lineup already)
            Set<UserEntity> players = team.getMembers();
            starters.forEach(players::remove);
            subs.forEach(players::remove);
            model.addAttribute("remaining", lineupService.convertUserToJsonEquivalent(players.
                stream().toList()));
            model.addAttribute("selectedTeamId", team.getId());
        }

        
        List<Team> teams = Stream.concat(user.getCoachedTeams().stream(),
            user.getManagedTeams().stream()).distinct().toList();

        model.addAttribute("teams", teams);
        model.addAttribute("activityID", activityId);
        model.addAttribute(ACTIVITY, existingActivity);
        model.addAttribute("location", existingActivity.getLocation());
        model.addAttribute("locationForm", new Location());
        model.addAttribute("description", existingActivity.getDescription());
        model.addAttribute("startTime", existingActivity.getStartTime());
        model.addAttribute("endTime", existingActivity.getEndTime());
        model.addAttribute("team", existingActivity.getTeam());
        model.addAttribute("user", user);
        model.addAttribute("basePath", environment.getProperty("app.baseUrl"));
        return "activity/editActivity.html";
    }

    /**
     * Handles the POST request to edit an activity.
     * <p>
     * Validates user rights and form data, associates team if teamId provided, converts
     * lineupList and subList to List<Long>, saves changes, and redirects to the edit page.
     *
     * @param activity The activity object encapsulating the activity details.
     * @param activityResult BindingResult that holds validation results.
     * @param teamId The ID of the team as a String.
     * @param activityId The ID of the activity to be edited.
     * @param lineupList A string of player IDs for the lineup, e.g. "[1, 2, 3]"
     * @param subList A string of player IDs for the substitutes, e.g. "[1, 2, 3]"
     * @param redirectAttributes RedirectAttributes for storing attributes for redirect scenarios.
     * @param model The Model object to be used in the view.
     * @return A string representing the name of the view.
     * @throws InvalidFormException if the user does not have rights to edit the activity,
     * if there are any form errors, or if the teamId provided is invalid.
     */
    @PostMapping("/activity/edit/{activityId}")
    public String editActivity(@Valid @ModelAttribute Activity activity,
                               BindingResult activityResult,
                               @Valid Location location,
                               BindingResult locationResult,
                               @RequestParam("team") String teamId,
                               @PathVariable("activityId") Long activityId,
                               @RequestParam("lineupList") String lineupList,
                               @RequestParam("subList") String subList,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        LOGGER.info("POST /activity/edit");
        
        Activity existingActivity = activityService.getActivityById(activityId);
        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (existingActivity == null) {
            return "error/404";
        }

        if (existingActivity.getTeam() == null && existingActivity.getUser() != user) {
            throw new InvalidFormException("You are not the owner of this personal activity and cannot edit it!");
        }
        else if (existingActivity.getTeam() != null) {
            boolean isCoach = existingActivity.getTeam().getCoaches().contains(user);
            boolean isManager = existingActivity.getTeam().getManagers().contains(user);
            if (!isCoach && !isManager) {
                throw new InvalidFormException("You are not a coach or manager of this team and cannot edit its activity!");
            }
        }

        boolean isValid = activityService.isValid(activity, user, activityResult);
        boolean isLocationValid = activityService.isLocationValid(location, locationResult);


        if (!isValid || !isLocationValid) {
            List<Team> teams = Stream.concat(user.getCoachedTeams().stream(),
                    user.getManagedTeams().stream()).distinct().toList();

            model.addAttribute("teams", teams);
            model.addAttribute(ACTIVITY, activity);
            model.addAttribute("location", location);
            Team team = existingActivity.getTeam();
            if(team != null) {
                // get existing lineups
                List<UserEntity> starters = lineupService.getStartingLineup(activityId);
                List<UserEntity> subs = lineupService.getSubstituteLineup(activityId);
                model.addAttribute("starters", lineupService.convertUserToJsonEquivalent(starters));
                model.addAttribute("subs", lineupService.convertUserToJsonEquivalent(subs));

                // get remaining players (the ones not in the lineup already)
                Set<UserEntity> players = team.getMembers();
                starters.forEach(players::remove);
                subs.forEach(players::remove);
                model.addAttribute("remaining", lineupService.convertUserToJsonEquivalent(players.
                        stream().toList()));
                model.addAttribute("selectedTeamId", team.getId());
            }
            model.addAttribute("basePath", environment.getProperty("app.baseUrl"));
            return "activity/editActivity.html";
        }

        if (!teamId.isBlank()) {
            try {
                Optional<Team> teamResult = teamService.getTeamById(activityId);
                teamResult.ifPresent(activity::setTeam);
            } catch (Exception ignored) {
                throw new InvalidFormException("Invalid arguments passed when editing activity!");
            }
        }
        
        existingActivity.setType(activity.getType());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setTeam(activity.getTeam());
        existingActivity.setStartTime(activity.getStartTime());
        existingActivity.setEndTime(activity.getEndTime());
        
        activityService.resetLineup(existingActivity);
        
        existingActivity.setLocation(location);
        
        activityService.saveActivity(existingActivity);
        
        
        // turns lineup and substitute from string to list of long
        List<Long> starterIds = ActivityService.convertStringToLongList(lineupList);
        List<Long> substituteIds = ActivityService.convertStringToLongList(subList);
        
        if(!starterIds.isEmpty()) {
            activityService.addPlayersToActivityLineup(existingActivity, LineupRole.STARTER, starterIds);
        }
        
        if(!substituteIds.isEmpty()) {
            activityService.addPlayersToActivityLineup(existingActivity, LineupRole.SUB , substituteIds);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Activity edited successfully!");
        model.addAttribute(ACTIVITY, activity);
        return "redirect:/activity/" + activityId;
    }
}