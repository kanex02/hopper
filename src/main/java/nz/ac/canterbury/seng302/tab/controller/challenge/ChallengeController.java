package nz.ac.canterbury.seng302.tab.controller.challenge;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.controller.ControllerUtils;
import nz.ac.canterbury.seng302.tab.controller.api.JsonChallenge;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.challenge.IsTeaPotException;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HTTP endpoints for requests related to {@link Challenge}s
 */
@Controller
public class ChallengeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeController.class);

    /**
     * Service access for {@link Challenge}
     */
    private final ChallengeService challengeService;

    /**
     * Service access for {@link nz.ac.canterbury.seng302.tab.entity.UserEntity}
     */
    private final UserService userService;

    @Autowired
    private BlogPostService blogPostService;

    private static final String CHALLENGE_ID = "challengeId";

    private static final String BLOG_POST = "blogPost";

    private static final String MEDIA_VIOLATION = "mediaViolation";


    @Autowired
    private Environment environment;


    @Value("${app.is_local}")
    private boolean isLocal;

    /**
     * Spring constructor for the controller. Do not call this constructor outside testing contexts!
     *
     * @param challengeService The service access for {@link Challenge}s
     */
    public ChallengeController(ChallengeService challengeService, UserService userService) {
        this.challengeService = challengeService;
        this.userService = userService;
    }

    /**
     * Endpoint for accessing the view-all page for challenges.
     *
     * @param model The mutable model to add attributes to for Thymeleaf etc.
     * @return Returns the challenge view HTML template file.
     */
    @GetMapping("/challenge/view")
    public String getView(Model model, @RequestParam(value = "notificationChallengeId", required = false) Long notificationChallengeId) {
        UserEntity user = userService.getLoggedInUser();
        List<JsonChallenge> jsonChallenges = challengeService.getAllAvailableChallengesForUser(user)
                .stream()
                .map(challenge -> new JsonChallenge(challenge, user))
                .toList();

        model.addAttribute("availableChallenges", jsonChallenges);
        model.addAttribute(
                "completedChallenges",
                challengeService.getAllCompletedChallengesForUser(user)
        );
        if (!model.containsAttribute(BLOG_POST)) {
            model.addAttribute(BLOG_POST, new BlogPost());
        }

        model.addAttribute("notificationChallengeId", notificationChallengeId);
        model.addAttribute("baseUrl", environment.getProperty("app.baseUrl"));
        model.addAttribute("challenges", jsonChallenges);

        return "challenge/view.html";
    }

    /**
     * Endpoint for completing a challenge.
     *
     * @param blogPost blog post being created
     * @param blogPostBindingResult the binding result for the form
     * @param challengeId The ID of the challenge to complete.
     * @param referrerURL The URL to redirect to after completing the challenge.
     * @param redirectAttributes attributes to add when redirecting the page
     * @return Returns path to an HTML template file.
     */
    @PostMapping("/challenge/complete")
    public String completeChallenge(@Valid BlogPost blogPost,
                                    BindingResult blogPostBindingResult,
                                    @ModelAttribute("challengeId") Long challengeId,
                                    @RequestParam(value = "mediaUpload", required = false) MultipartFile file,
                                    @RequestHeader(value = "referer", required = false) final String referrerURL,
                                    RedirectAttributes redirectAttributes) {

        LOGGER.info("POST /challenge/complete");

        if (blogPostBindingResult.hasErrors()) {
            Map<String, String> errorMap = blogPostBindingResult.getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage, ((s, s2) -> s + " " + s2)));

            redirectAttributes.addFlashAttribute("editErrors", errorMap);
            redirectAttributes.addFlashAttribute(CHALLENGE_ID, challengeId);
            redirectAttributes.addFlashAttribute(BLOG_POST, blogPost);
            return referrerViewPath(referrerURL);
        }

        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute(MEDIA_VIOLATION, "You must upload a photo or video of yourself doing the challenge as proof!");
            redirectAttributes.addFlashAttribute(CHALLENGE_ID, challengeId);
            redirectAttributes.addFlashAttribute(BLOG_POST, blogPost);
            return referrerViewPath(referrerURL);
        }

        Challenge challenge = challengeService.getChallengeById(challengeId);
        UserEntity user = userService.getLoggedInUser();

        try {
            blogPostService.createChallengePost("Challenge Complete: " + challenge.getTitle(), blogPost.getDescription(), user, challenge, file);
        } catch (MediaService.UploadFailure uploadFailure) {
            // catch the upload failure and add the appropriate error message
            if (Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_IMAGE) {
                redirectAttributes.addFlashAttribute(MEDIA_VIOLATION, uploadFailure.getMessage());
            } else if (uploadFailure.getReason() == MediaService.FailReason.INVALID_VIDEO) {
                redirectAttributes.addFlashAttribute(MEDIA_VIOLATION, uploadFailure.getReason());
            } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                redirectAttributes.addFlashAttribute(MEDIA_VIOLATION, "Error uploading file: "
                        + uploadFailure.getMessage());
            }
            redirectAttributes.addFlashAttribute(CHALLENGE_ID, challengeId);
            redirectAttributes.addFlashAttribute(BLOG_POST, blogPost);
            return referrerViewPath(referrerURL);
        }

        try {
            if (challengeService.tryCompleteChallenge(challengeId, user)) {
                return referrerViewPath(referrerURL);
            }
        } catch (IsTeaPotException e) {
            return "error/418";
        }
        return "error/400";
    }

    /**
     * Helper method to determine the page to redirect to
     *
     * @param referrerURL the URL that the request was made from. May be Null under acceptance testing
     * @return the path of the view template to display
     */
    private String referrerViewPath(String referrerURL) {
        return ControllerUtils.referrerPath(referrerURL, isLocal);
    }

    /**
     * Endpoint for sharing a challenge.
     */
    @PostMapping("/challenge/share")
    public String shareChallenge(@ModelAttribute("challengeId") String challengeId,
                                 @ModelAttribute("selectedFriends") String selectedFriends,
                                 @ModelAttribute("redirect") String redirectPath,
                                 Model model) {
        LOGGER.info("POST /challenge/share");
        try {
            Challenge challenge = challengeService.getChallengeById(Long.parseLong(challengeId));
            if (challenge == null) {
                return "error/404";
            }

            UserEntity user = userService.getLoggedInUser();
            if (challenge.getUserGeneratedFor() != user) {
                return "error/403";
            }

            List<UserEntity> users = userService.getUsersFromUserIdArrayString(selectedFriends);

            challengeService.inviteUsersToChallenge(challenge, users);

            return referrerViewPath(redirectPath);
        } catch (NumberFormatException e) {
            return "error/400";
        }
    }
}
