package nz.ac.canterbury.seng302.tab.controller.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller to update the score of the activity.
 */
@Controller
public class ActivityScoreController {

    @Autowired
    ActivityService activityService;

    /**
     * Updates the score of an activity.
     * @param activityScore the activity score, generated in thymeleaf with parmas homeScore and awayScore
     * @param activityId ID of the activity to update the score of
     * @param redirectAttributes Redirect attributes to add the success message to
     * @return redirects back to the activity details page
     */
    @PostMapping("/activity/updateScore/{activityId}")
    public String updateScore(
            ActivityScore activityScore,
            @PathVariable(value = "activityId") long activityId,
            RedirectAttributes redirectAttributes
    ) {
        Activity activity = activityService.getActivityById(activityId);

        if (activity == null) {
            return "error/404";
        }

        activity.setActivityScore(activityScore);
        activityService.saveActivity(activity);


        redirectAttributes.addFlashAttribute("successMessage", "Score saved successfully!");

        return "redirect:/activity/" + activityId;
    }
}
