package nz.ac.canterbury.seng302.tab.controller;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Controller for the creating team form
 */
@Controller
public class EditTeamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditTeamController.class);
    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SportService sportService;

    private static final String REDIRECT_TEAMS_PREFIX = "redirect:/teams/";

    private static final String IMAGE_ERROR = "imageError";

    /**
     * Tooltip messages to be displayed when hovered, informing the user of the requirements for each field
     */
    private final List<String> errorMessages = Arrays.asList("Team name must be at least 3 chars long as well as consist of only: alphanumeric characters, dots, and curly braces",
            "Sport must start with a letter and after only contain letters and spaces",
            "City must include only alphabetic characters",
            "Country must include only alphabetic characters");

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     *
     * @param model  (map-like) representation of teamName and sport for use in thymeleaf.
     * @param teamId id of the team to edit
     * @return thymeleaf editTeamTemplate
     */
    @GetMapping("/edit_team/{teamId}")
    public String form(Model model, @PathVariable String teamId) {
        LOGGER.info("GET /edit_team");
        try {
            Long id = Long.parseLong(teamId);
            Optional<Team> result = teamService.getTeamById(id);
            if (result.isEmpty()) {
                throw new NoSuchElementException();
            }
            Team team = result.get();
            model.addAttribute("team", team);
            model.addAttribute("location", team.getLocation());
            model.addAttribute("sports", sportService.getAllSports());
            model.addAttribute("errorMessages", errorMessages);


            UserEntity user = userService.getLoggedInUser();
            if (!team.getManagers().contains(user)) { //illegal edit attempt
                return REDIRECT_TEAMS_PREFIX + team.getId().toString();
            }
            model.addAttribute("user", user);
            return REDIRECT_TEAMS_PREFIX + team.getId().toString();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
    }

    /**
     * Post request for editing a team
     *
     * @param team team object that gets validated
     * @param teamResult errors from validating the team object
     * @param sportId id of the sport selected from the dropdown
     * @param teamId id of the team to edit on
     * @param file image to upload
     * @param model model for the attributes of the view
     * @param redirectAttributes attributes to be used in the redirect
     * @return either the thymeleaf editTeam html or the teamPage template
     */
    @PostMapping("/edit_team/{teamId}")
    public String editTeam(
            @Valid Team team,
            BindingResult teamResult,
            @RequestParam(value = "sport", required = false) String sportId,
            @RequestParam("imageUpload") Optional<MultipartFile> file,
            @PathVariable("teamId") Long teamId,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        // Get the team from the database using the team id
        Optional<Team> optionalTeam = teamService.getTeamById(teamId);

        Sport sport = sportService.findById(Long.parseLong(sportId));

        if (sport == null) {
            return "error/404";
        }

        if (teamResult.hasErrors()) {
            model.addAttribute("sports", sportService.getAllSports());
            FieldError fieldError = teamResult.getFieldError("teamName");
            if (fieldError != null) {
                String errorMsg = fieldError.getDefaultMessage();
                if (errorMsg != null) {
                    redirectAttributes.addFlashAttribute("teamNameError", errorMsg);
                }
            }
            return REDIRECT_TEAMS_PREFIX + teamId;
        }

        var oldTeamImage = new AtomicReference<>("");
        Team existingTeam;
        if (optionalTeam.isPresent()) {
            existingTeam = optionalTeam.get();

            if (existingTeam.getClub() != null && (sport != existingTeam.getSport())) {
                // Remove team from club since new sport and existing club sport is different
                existingTeam.getClub().removeClubFromTeam(existingTeam);
            }

            var image = existingTeam.getImage();
            oldTeamImage.set(image);
            existingTeam.setImage(image);
            if (file.isPresent() && !mediaService.uploadTeamImage(file.get(), model, existingTeam)) {
                existingTeam.setImage(oldTeamImage.get());
            }

            existingTeam.setSport(sport);
            existingTeam.setTeamName(team.getTeamName());



            teamService.addTeam(existingTeam);

            return REDIRECT_TEAMS_PREFIX + existingTeam.getId().toString();

        } else {
            return "error/404";
        }
    }

    /**
     * Edits the team location and saves the location to the team
     *
     * @param location location entity to add to the team
     * @param bindingResult binding result for validating the location entity
     * @param teamId id of the team
     * @param redirectAttributes redirect attributes to pass through the error messages
     * @return redirects to the team page
     */
    @PostMapping("/teams/{teamId}/edit-location")
    public String editTeamLocation(@Valid Location location,
                                   BindingResult bindingResult,
                                   @PathVariable String teamId,
                                   RedirectAttributes redirectAttributes) {
        Team existingTeam = teamService.getTeamById(Long.parseLong(teamId)).orElseThrow();

        if (!existingTeam.getManagers().contains(userService.getLoggedInUser())) {
            redirectAttributes.addFlashAttribute("authError", "You thought you were a sneaky fella didn't you?");
            return REDIRECT_TEAMS_PREFIX + teamId;
        }

        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors()
            .stream()
            .filter(fieldError -> fieldError.getDefaultMessage() != null)
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing + ", " + replacement
            ));
            redirectAttributes.addFlashAttribute("modalId", "myModal");
            redirectAttributes.addFlashAttribute("fieldErrors", errorMap);
            return REDIRECT_TEAMS_PREFIX + existingTeam.getId().toString();
        }

        existingTeam.setLocation(location);
        teamService.save(existingTeam);

        return REDIRECT_TEAMS_PREFIX + existingTeam.getId().toString();
    }

    /**
     * Post mapping for editing the team's picture.
     * @param team team object passed in the form
     * @param result An error container for team validation
     * @param redirectAttributes attributes to be added to the model on redirect
     * @param file file chosen to be set as the team picture
     * @param model The {@link Model} of the page
     * @param teamId id of the team being edited
     * @return to team details page
     * @throws NoSuchElementException if the team is not found
     */
    @PostMapping("/team_pic_edit/{teamId}")
    public String editTeamPicture(
            @Valid Team team,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @RequestParam("imageUpload") MultipartFile file,
            Model model,
            @PathVariable("teamId") Long teamId
    ) throws NoSuchElementException {
        // Get the image of the team from the database
        Optional<Team> dbResult = teamService.getTeamById(teamId);
        AtomicReference<String> oldTeamImage = new AtomicReference<>("");
        if (dbResult.isEmpty()) {
            throw new NoSuchElementException();
        }
        Team dbTeam = dbResult.get();

        //check user can edit team
        UserEntity user = userService.getLoggedInUser();
        if (!dbTeam.getManagers().contains(user)) { //illegal edit attempt
            return REDIRECT_TEAMS_PREFIX + dbTeam.getId().toString();
        }
        oldTeamImage.set(dbTeam.getImage());

        String teamPage = "teamPage";

        try {
            mediaService.setTeamImageIfValid(file, dbTeam);
        } catch (MediaService.UploadFailure failure) {
            LOGGER.error("Error updating team image", failure);
            redirectAttributes.addFlashAttribute(
                    IMAGE_ERROR,
                    failure.getReason().getMessage()
            );
            return REDIRECT_TEAMS_PREFIX + teamId.toString();
        }

        if (!mediaService.uploadTeamImage(file, model, dbTeam)) {
            dbTeam.setImage(oldTeamImage.get());
            return teamPage;
        }

        teamService.addTeam(dbTeam);
        return REDIRECT_TEAMS_PREFIX + teamId.toString();
    }
}
