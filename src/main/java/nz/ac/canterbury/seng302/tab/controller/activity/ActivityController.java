package nz.ac.canterbury.seng302.tab.controller.activity;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import nz.ac.canterbury.seng302.tab.pojo.FactStatisticForm;
import nz.ac.canterbury.seng302.tab.pojo.ScoreStatisticForm;
import nz.ac.canterbury.seng302.tab.pojo.SubstitutionStatisticForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.LineupService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.activity.ActivityStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controls the get mapping to view the activity details page.
 */
@Controller
public class ActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityController.class);
    public static final String NULL_FORM_MESSAGE = "Tried to create event statistic from null form object";
    private static final String FACT_FORM = "factStatisticForm";
    private static final String SCORE_FORM = "scoreStatisticForm";
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LineupService lineupService;

    @Autowired
    ActivityStatisticService activityStatisticService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;
    private static final String ERROR404 = "error/404";
    private static final String REDIRECT_ACTIVITY = "redirect:/activity/";

    /**
     * Creates a new controller with custom services.
     *
     * @param activityService          The service for activities
     * @param lineupService            The service for lineups
     * @param activityStatisticService The service for activity statistics
     * @param userService              The service for users
     */
    public ActivityController(ActivityService activityService, LineupService lineupService,
                              ActivityStatisticService activityStatisticService, UserService userService) {
        this.activityService = activityService;
        this.lineupService = lineupService;
        this.activityStatisticService = activityStatisticService;
        this.userService = userService;
    }

    /**
     * Controller for viewing activity details.
     *
     * @param activityId ID of the activity to view
     * @param model      (map-like) representation of teamName and sport for use in thymeleaf.
     * @return thymeleaf activity detail page
     */
    @GetMapping("/activity/{activityId}")
    public String viewActivity(
            @PathVariable("activityId") long activityId,
            Model model) {

        LOGGER.info("GET /activity/{}", activityId);

        Activity activity = activityService.getActivityById(activityId);

        if (activity == null) {
            return ERROR404;
        }

        UserEntity user = userService.getLoggedInUser();

        model.addAttribute("user", user);
        List<UserEntity> starterLineup = lineupService.getStartingLineup(activityId);
        List<UserEntity> substituteLineup = lineupService.getSubstituteLineup(activityId);
        model.addAttribute("activity", activity);
        model.addAttribute("starterLineup", lineupService.convertUserToJsonEquivalent(starterLineup));
        model.addAttribute("substituteLineup", lineupService.convertUserToJsonEquivalent(substituteLineup));

        model.addAttribute("activityScore", new ActivityScore(0, 0));
        model.addAttribute("basePath", environment.getProperty("app.baseUrl"));

        if (!model.containsAttribute(SCORE_FORM)) {
            model.addAttribute(SCORE_FORM,
                    new ScoreStatisticForm(1, "0", "0", activity.getStartTime()));
        }

        if (!model.containsAttribute(FACT_FORM)) {
            model.addAttribute(FACT_FORM,
                    new FactStatisticForm("He should not have scored...", activity.getStartTime()));
        }

        return "activity/activityPage.html";
    }

    /**
     * Validates and saves fact events.
     *
     * @param activityId         ID of the activity to save to
     * @param factStatisticForm  User data
     * @param formResult         Binding Result for form validation. Note that this must be the parameter
     *                           directly after the form.
     * @param redirectAttributes Model to add flash attributes for use after redirection
     * @return The redirected view
     */
    @PostMapping("/activity/{activityId}/create-statistic/fact-event")
    public String createFactEventStatistic(@PathVariable Long activityId,
                                           @ModelAttribute
                                           FactStatisticForm factStatisticForm,
                                           BindingResult formResult,
                                           RedirectAttributes redirectAttributes) {

        LOGGER.info("POST /activity/{}/create-statistic/fact-event", activityId);

        Activity activity = activityService.getActivityById(activityId);
        if (activity == null) return ERROR404;

        if (factStatisticForm != null) {
            formResult = activityStatisticService.validateStatisticForm(factStatisticForm, activity, formResult);
        }

        if (factStatisticForm == null) {
            throw new IllegalArgumentException(NULL_FORM_MESSAGE);
        }

        if (formResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.factStatisticForm", formResult);
            redirectAttributes.addFlashAttribute(FACT_FORM, factStatisticForm);
            redirectAttributes.addFlashAttribute("focussedStatistic", "FACT");

            return REDIRECT_ACTIVITY + activityId;
        }

        Activity savedActivity = activityStatisticService.saveFactStatisticToActivity(factStatisticForm, activity);


        if (savedActivity != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Statistic saved successfully.");
        }
        return REDIRECT_ACTIVITY + activityId;
    }


    /**
     * Validates and saves score events.
     *
     * @param activityId         ID of the activity to save to
     * @param scoreStatisticForm User data
     * @param formResult         Binding Result for form validation. Note that this must be the parameter
     *                           directly after the form.
     * @param redirectAttributes Model to add flash attributes for use after redirection
     * @return The redirected view
     */
    @PostMapping("/activity/{activityId}/create-statistic/score-event")
    public String createScoreEventStatistic(@PathVariable Long activityId,
                                            @Valid
                                            @ModelAttribute
                                            ScoreStatisticForm scoreStatisticForm,
                                            BindingResult formResult,
                                            RedirectAttributes redirectAttributes) {

        LOGGER.info("POST /activity/{}/create-statistic/score-event", activityId);


        Activity activity = activityService.getActivityById(activityId);
        if (activity == null) return ERROR404;

        if (scoreStatisticForm != null) {
            formResult = activityStatisticService.validateStatisticForm(scoreStatisticForm, activity, formResult);
        }

        if (scoreStatisticForm == null) {
            throw new IllegalArgumentException(NULL_FORM_MESSAGE);
        }

        if (formResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.scoreStatisticForm", formResult);
            redirectAttributes.addFlashAttribute(SCORE_FORM, scoreStatisticForm);
            redirectAttributes.addFlashAttribute("focussedStatistic", "SCORE_EVENT");
            redirectAttributes.addFlashAttribute(SCORE_FORM, scoreStatisticForm);

            return REDIRECT_ACTIVITY + activityId;
        }

        Activity savedActivity = activityStatisticService.saveScoreStatisticToActivity(scoreStatisticForm, activity);


        if (savedActivity != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Statistic saved successfully.");
        }
        return REDIRECT_ACTIVITY + activityId;
    }

    @PostMapping("/activity/{activityId}/create-statistic/substitution-event")
    public String createSubstitutionEventStatistic(
            @PathVariable Long activityId,
            @Valid SubstitutionStatisticForm substitutionStatisticForm,
            BindingResult formResult,
            RedirectAttributes redirectAttributes
    ) {

        Activity activity = activityService.getActivityById(activityId);

        if (activity == null) {
            return ERROR404;
        }

        return REDIRECT_ACTIVITY + activityId;
    }
}
