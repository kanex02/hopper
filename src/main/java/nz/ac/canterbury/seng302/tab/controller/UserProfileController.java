package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.ImageVerificationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.cosmetic.CosmeticService;
import nz.ac.canterbury.seng302.tab.service.cosmetic.LevelService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

/**
 * Controller for the user profile page
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class UserProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private CosmeticService cosmeticService;

    @Autowired
    private SportService sportService;

    @Autowired
    private LevelService levelService;

    private static final String REDIRECT_USER = "redirect:/user";

    /**
     * Gets logged in user details page to be displayed
     *
     * @param model The {@link Model} of the page
     * @return Returns the Thymeleaf userProfilePage
     */
    @GetMapping({"/user", "/upload"})
    public String getUserPage(Model model) {
        LOGGER.info("GET /user");

        // Get the logged in user
        UserEntity user = userService.getLoggedInUser();
        if (user == null) {
            return "redirect:/";
        }

        // Add form model for team invitation
        Map<String, String> formModel = new HashMap<>();
        formModel.put("textInput", "");
        model.addAttribute("formModel", formModel);

        List<UserEntity> userFollowers = user.getFollowers();
        List<UserEntity> userFollowing = user.getFollowing();
        List<UserEntity> mutuals = user.getMutualFollowers();

        // Sort the lists
        Comparator<UserEntity> compareByName = Comparator
                .comparing(UserEntity::getLastName)
                .thenComparing(UserEntity::getFirstName);

        userFollowers.sort(compareByName);
        userFollowing.sort(compareByName);
        mutuals.sort(compareByName);

        List<Team> followingTeams = user.getFollowingTeams();
        List<Club> followingClubs = user.getFollowingClubs();

        followingTeams.sort(Comparator
                .comparing(Team::getTeamName));

        followingClubs.sort(Comparator
                .comparing(Club::getName));

        model.addAttribute("profileUser", user);
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId());
        model.addAttribute("allTeams", user.getAllTeams());
        model.addAttribute("favouriteSports", user.getFavouriteSports());
        model.addAttribute("sportOptions", sportService.getAllSports());
        model.addAttribute("location", user.getLocation() == null ? new Location() : user.getLocation());
        model.addAttribute("borders", cosmeticService.getBorders());
        model.addAttribute("levels", levelService.getAllLevels());
        model.addAttribute("borders", cosmeticService.getBorders());
        model.addAttribute("unlockedBorders", levelService.getUnlockedBorders(user.getLevel()));
        model.addAttribute("badges", cosmeticService.getBadges());
        model.addAttribute("unlockedBadges", levelService.getUnlockedBadges(user.getLevel()));
        model.addAttribute("hopsRequired", user.getTotalHopsRequiredForNextLevel()-user.getTotalHops());
        model.addAttribute("followers", userFollowers);
        model.addAttribute("following", userFollowing);
        model.addAttribute("followingTeams", followingTeams);
        model.addAttribute("followingClubs", followingClubs);
        model.addAttribute("mutuals", mutuals);

        model.addAttribute("notViewingOwnPage", false);
        if (user.getLocation() == null) {
            model.addAttribute("completeProfilePrompt", "Add your location to complete your profile!");
            model.addAttribute("location", new Location());
        }

        return "userProfilePage";
    }


    /**
     * Changes user profile picture if all criteria is met
     *
     * @param file               UserEntity's chosen file to upload
     * @param redirectAttributes the redirect attributes to add an error message.
     * @return redirect to user profile page if successfully registered, else refresh user page
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        LOGGER.info("POST /upload");

        UserEntity user = userService.getLoggedInUser();

        // Crop if image dimensions are not square to prevent stretching
        if ((Objects.equals(file.getContentType(), "image/jpeg") || Objects.equals(file.getContentType(), "image/png"))
                && !ImageVerificationService.verifyImageDimensions(file)) {
            ImageVerificationService.cropImage(file);
        }

        try {
            mediaService.setUserImageIfValid(file, user);
        } catch (MediaService.UploadFailure failure) {
            LOGGER.error("Error updating user image", failure);
            redirectAttributes.addFlashAttribute(
                    "imageError",
                    failure.getReason().getMessage()
            );
            return REDIRECT_USER;
        }

        userService.updateUser(user);
        LOGGER.debug("Successfully uploaded image to {}", user.getProfilePicturePath());

        return REDIRECT_USER;

    }

    /**
     * Process the user's request to join a team
     *
     * @param textInput          the team invitation token submitted by the user
     * @param redirectAttributes the RedirectAttributes object used to display success or error messages
     * @return a redirect to the user's profile with success or error messages added to the redirect attributes
     */
    @PostMapping("/join-team")
    public String submitJoinTeam(@RequestParam("textInput") String textInput,
                                 RedirectAttributes redirectAttributes) {

        String errorMessage = teamService.joinTeam(textInput);

        if (errorMessage != null) {
            sendRedirectErrors(redirectAttributes, errorMessage);
        } else {
            Team team = teamService.getTeamByToken(textInput);
            redirectAttributes.addFlashAttribute("successMessage", String.format("Joined team %s!", team.getTeamName()));
        }

        return REDIRECT_USER;
    }

    /**
     * Adds an error message to the redirect attributes to be displayed upon redirect
     *
     * @param redirectAttributes the RedirectAttributes object to which the error message is added
     * @param message            the error message to be displayed
     */
    public void sendRedirectErrors(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("joinTeamModal", "myModal");
        redirectAttributes.addFlashAttribute("error", message);
    }

    /**
     * Processes the form that the user submits to complete their profile and link
     * their profile to a location.
     *
     * @param user               to be linked to location
     * @param userId             id of the user to be linked
     * @param redirectAttributes the RedirectAttributes object used to display success or error messages
     * @return a redirect to the user's profile with a success message once complete
     */
    @PostMapping("/user/complete-profile/{userId}")
    public String submitCompleteProfile(
            @ModelAttribute("user") UserEntity user,
            @PathVariable("userId") Long userId,
            RedirectAttributes redirectAttributes) {
        LOGGER.info("POST /user/complete-profile/{}", userId);

        UserEntity existingUser = userService.getUserById(userId);

        existingUser.setLocation(user.getLocation());

        userService.updateUser(existingUser);

        redirectAttributes.addFlashAttribute("changesSaved", "Changes have been saved");
        return REDIRECT_USER;
    }


    /**
     * Changes the user's border
     *
     * @param borderId the id of the border to change to
     * @return a redirect to the user's profile
     */
    @PostMapping("/change-border")
    public String changeBorder(@RequestParam(value = "borderId", required = false) String borderId) {

        LOGGER.info("POST /change-border");

        UserEntity user = userService.getLoggedInUser();
        if (!borderId.isBlank() && user != null) {
            try {
                cosmeticService.setBorder(user, borderId);
            } catch (NullPointerException | IllegalArgumentException e) {
                LOGGER.error("Invalid border id", e);
                return "error/400";
            }
        }

        return REDIRECT_USER;
    }


    /**
     * Changes the user's badges
     *
     * @param badge0Id the id of the badge to display in the first position
     * @param badge1Id the id of the badge to display in the second position
     * @param badge2Id the id of the badge to display in the third position
     * @return a redirect to the user's profile
     */
    @PostMapping("/change-badges")
    public String changeBadge(@RequestParam(value = "badge0Id", required = false) String badge0Id,
                              @RequestParam(value = "badge1Id", required = false) String badge1Id,
                              @RequestParam(value = "badge2Id", required = false) String badge2Id
    ) {

        LOGGER.info("POST /change-badges");

        List<String> badgeIds = List.of(badge0Id, badge1Id, badge2Id);
        UserEntity user = userService.getLoggedInUser();
        try {
            cosmeticService.setBadges(badgeIds, user);
        } catch (NullPointerException | IllegalArgumentException e) {
            LOGGER.error("Invalid badge id", e);
            return "error/400";
        }

        return REDIRECT_USER;
    }
}
