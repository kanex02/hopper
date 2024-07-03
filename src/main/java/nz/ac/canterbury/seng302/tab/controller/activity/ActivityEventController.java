package nz.ac.canterbury.seng302.tab.controller.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Defines controllers for creating statistics
 */
@Controller
public class ActivityEventController {

    public static final String SUCCESS_ATTRIBUTE_KEY = "activity_create_success";
    public static final String ERROR_ATTRIBUTE_KEY = "activity_create_error";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityEventController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    /**
     * POST endpoint for creating statistic events. Accepts the activity id, and the raw form data for creating
     * the statistic. This raw data is passed off to a factory and parsed manually.
     * <p>
     * Returns 404 if the {@code activityId} is not found, 403 if user is not allowed to edit the activity.
     * Returns a redirection if the user successfully edited the activity, or some error occurred in parsing
     * the data.
     *
     *
     * @param activityId         The id of the activity to add the event too
     * @param requestData        The raw form data to be parsed
     * @param redirectAttributes Redirection attributes for displaying flash messages
     * @return Returns a status string for the client to interpret.
     */
    @PostMapping("/activity/create-statistic/{activityId}")
    public String createStatistic(
            @PathVariable Long activityId,
            @RequestParam Map<String, String> requestData,
            RedirectAttributes redirectAttributes
    ) {
        LOGGER.info("POST /activity/create-statistic/{}", activityId);

        Activity activity = activityService.getActivityById(activityId);
        String type = requestData.get("type");

        if (activity == null) {
            return "error/404";
        }

        UserEntity user = userService.getLoggedInUser();

        try {
            activityService.recordStatistic(activity, user, type, requestData);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE_KEY, "Unable to create activity: " + e.getMessage());
            return "redirect:/activity/" + activityId;
        } catch (SecurityException e) {
            return "error/403";
        }
        redirectAttributes.addFlashAttribute(SUCCESS_ATTRIBUTE_KEY, "Statistic created successfully");
        return "redirect:/activity/" + activityId;
    }

}
