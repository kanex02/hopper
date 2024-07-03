package nz.ac.canterbury.seng302.tab.service.activity;

import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.FactStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.FactStatistic.FactBuilder;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import nz.ac.canterbury.seng302.tab.pojo.FactStatisticForm;
import nz.ac.canterbury.seng302.tab.pojo.ScoreStatisticForm;
import nz.ac.canterbury.seng302.tab.repository.activity.FactStatisticRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ScoreEventStatisticRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides methods to create statistics for activities. Note: the score statistic of an activity is updated
 * directly via {@link Activity#setActivityScore(ActivityScore)}.
 */
@Service
public class ActivityStatisticService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityStatisticService.class);
    public static final String EVENT_TIME = "time"; //change this, and you must also change form property name and HTML element name

    private static final String EVENT_DESCRIPTION = "description";

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private FactStatisticRepository factStatisticRepository;
    @Autowired
    private ScoreEventStatisticRepository scoreEventStatisticRepository;

    /**
     * Creates a new instance of this service using custom services.
     */
    public ActivityStatisticService(ActivityService activityService, UserService userService, TeamService teamService,
                                    ScoreEventStatisticRepository scoreEventStatisticRepository) {
        this.activityService = activityService;
        this.userService = userService;
        this.teamService = teamService;
        this.scoreEventStatisticRepository = scoreEventStatisticRepository;
    }

    /**
     * Creates a new instance of this service using custom services.
     */
    public ActivityStatisticService(ActivityService activityService, UserService userService, TeamService teamService,
                                    FactStatisticRepository scoreEventStatisticRepository) {
        this.activityService = activityService;
        this.userService = userService;
        this.teamService = teamService;
        this.factStatisticRepository = scoreEventStatisticRepository;
    }

    /**
     * Creates a new instance of this service using custom services.
     */
    @Autowired
    public ActivityStatisticService(
            ActivityService activityService,
            UserService userService,
            TeamService teamService,
            FactStatisticRepository factStatisticRepository,
            ScoreEventStatisticRepository scoreEventStatisticRepository
    ) {
        this.activityService = activityService;
        this.userService = userService;
        this.teamService = teamService;
        this.factStatisticRepository = factStatisticRepository;
        this.scoreEventStatisticRepository = scoreEventStatisticRepository;
    }

    /**
     * Creates and saves a fact event to an activity. Validation is performed on the form,
     * which has the same requirements as the entity itself.
     * @param factStatisticForm The user data
     * @param activity The activity to save the event to
     * @return The updated activity, or null if the operation failed
     */
    public Activity saveFactStatisticToActivity(FactStatisticForm factStatisticForm, Activity activity) {

        FactBuilder factEventBuilder = new FactStatistic.FactBuilder();

        // Building the entity
        factEventBuilder.withDescription(factStatisticForm.description());

        if (!Objects.equals(factStatisticForm.time(), "")) { // explicit fact time is optional
            LOGGER.info(String.format("factFormTime: %s", factStatisticForm.time()));
            long startTime = Instant.parse(activity.getStartTime().split("\\.")[0].concat("Z")).getEpochSecond();
            long statisticTime = Instant.parse(factStatisticForm.time().concat("Z")).getEpochSecond();
            long time = statisticTime - startTime;
            factEventBuilder.withTime(time);
        }

        FactStatistic factEventStatistic = factEventBuilder.buildAndValidate();

        factStatisticRepository.save(factEventStatistic);

        activity.addEvent(factEventStatistic);

        return activityService.saveActivity(activity);
    }

    /**
     * Verifies a statistic form, and rejects invalid values that are not easily caught with annotations.
     * @param factStatisticForm The form to verify
     * @param activity The activity the statistic is for
     * @param bindingResult The binding result object for the form
     * @return An updated binding result with invalid values rejected.
     */
    public BindingResult validateStatisticForm(FactStatisticForm factStatisticForm, Activity activity, BindingResult bindingResult) {

        try {
            Instant startTime = Instant.parse(activity.getStartTime().split("\\.")[0].concat("Z"));
            Instant endTime = Instant.parse(activity.getEndTime().split("\\.")[0].concat("Z"));
            Instant statisticTime = Instant.parse(factStatisticForm.time().concat("Z"));
            if (statisticTime.isBefore(startTime)) {
                bindingResult.rejectValue(EVENT_TIME, "time.beforeStart", "Fact time is before the activity starts!");
            }
            if (statisticTime.isAfter(endTime)) {
                bindingResult.rejectValue(EVENT_TIME, "time.afterEnd", "Fact time is after the activity ends!");
            }
        } catch (DateTimeParseException e) {
            if (!factStatisticForm.time().isEmpty()) {
                bindingResult.rejectValue(EVENT_TIME, "time.invalid", "Fact time is in an invalid format!");
            }
        }

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<FactStatisticForm>> violations = validator.validate(factStatisticForm);
            Set<Class<? extends Annotation>> violationTypes = violations.stream()
                    .map(violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                    .collect(Collectors.toSet());

            if (violationTypes.contains(NotNull.class)) {
                LOGGER.warn("Trying to validate null fact description");
                bindingResult.rejectValue(EVENT_DESCRIPTION, "description.null", "The description is null!");
            }

            if (violationTypes.contains(Pattern.class)) {
                bindingResult.rejectValue(EVENT_DESCRIPTION, "description.invalid", "Descriptions must be between 1 and 255 characters long!");
            }
        } catch (ValidationException e) {
            LOGGER.error("Validation failed: ", e);
            bindingResult.rejectValue(EVENT_DESCRIPTION, "validator.error", "A non-recoverable error occurred during validation. Try again!");
        }

        return bindingResult;
    }

    /**
     * Creates and saves a score event to an activity. Validation is performed on the form,
     * which has the same requirements as the entity itself.
     * @param scoreStatisticForm The user data
     * @param activity The activity to save the event to
     * @throws IllegalArgumentException If the team or user IDs do not exist
     * @return The updated activity, or null if the operation failed
     */
    public Activity saveScoreStatisticToActivity(ScoreStatisticForm scoreStatisticForm, Activity activity) {
        long scorerId = Long.parseLong(scoreStatisticForm.scorerId());
        long teamScoredForId = Long.parseLong(scoreStatisticForm.teamScoredForId());
        ScoreEventStatistic.ScoreEventBuilder scoreEventBuilder = new ScoreEventStatistic.ScoreEventBuilder();

        UserEntity scorer = userService.getUserById(scorerId);
        if (scorer == null) throw new IllegalArgumentException("User not found!");

        Optional<Team> teamScoredForResult = teamService.getTeamById(teamScoredForId);
        if (teamScoredForResult.isEmpty()) throw new IllegalArgumentException("Team not found!");
        Team teamScoredFor = teamScoredForResult.get();

        long startTime = Instant.parse(activity.getStartTime().split("\\.")[0].concat("Z")).getEpochSecond();
        long statisticTime = Instant.parse(scoreStatisticForm.time().concat("Z")).getEpochSecond();
        long time = statisticTime - startTime;

        // Building the entity
        ScoreEventStatistic scoreEventStatistic = scoreEventBuilder.withPointValue(scoreStatisticForm.pointValue())
                .withScorer(scorer).withTeam(teamScoredFor)
                .withTime(time).buildAndValidate();

        scoreEventStatisticRepository.save(scoreEventStatistic);

        activity.addEvent(scoreEventStatistic);

        return activityService.saveActivity(activity);
    }

    /**
     * Verifies a statistic form, and rejects invalid values that are not easily caught with annotations.
     * @param scoreStatisticForm The form to verify
     * @param activity The activity the statistic is for
     * @param bindingResult The binding result object for the form
     * @return An updated binding result with invalid values rejected.
     */
    public BindingResult validateStatisticForm(ScoreStatisticForm scoreStatisticForm, Activity activity, BindingResult bindingResult) {
        try {
            long scorerId = Long.parseLong(scoreStatisticForm.scorerId());
            UserEntity scorer = userService.getUserById(scorerId);
            if (scorer == null) bindingResult.rejectValue("scorerId", "scorerId.notFound",
                    "Scorer does not exist!");
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("scorerId", "scorerId.invalid", "Invalid scorer ID!");
        }

        try {
            long teamScoredForId = Long.parseLong(scoreStatisticForm.teamScoredForId());
            Optional<Team> teamScoredForResult = teamService.getTeamById(teamScoredForId);
            if (teamScoredForResult.isEmpty()) bindingResult.rejectValue("teamScoredForId",
                    "teamScoredForId.notFound", "Team does not exist!");
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("teamScoredForId", "teamScoredForId.invalid", "Invalid team ID!");
        }

        try {
            Instant startTime = Instant.parse(activity.getStartTime().split("\\.")[0].concat("Z"));
            Instant endTime = Instant.parse(activity.getEndTime().split("\\.")[0].concat("Z"));
            Instant statisticTime = Instant.parse(scoreStatisticForm.time().concat("Z"));
            if (statisticTime.isBefore(startTime)) {
                bindingResult.rejectValue(EVENT_TIME, "time.beforeStart", "Score time is before the activity starts!");
            }
            if (statisticTime.isAfter(endTime)) {
                bindingResult.rejectValue(EVENT_TIME, "time.afterEnd", "Score time is after the activity ends!");
            }
        } catch (DateTimeParseException e) {
            if (!scoreStatisticForm.time().isEmpty()) {
                bindingResult.rejectValue(EVENT_TIME, "time.invalid", "Score time is in an invalid format!");
            }
        }


        return bindingResult;
    }
}
