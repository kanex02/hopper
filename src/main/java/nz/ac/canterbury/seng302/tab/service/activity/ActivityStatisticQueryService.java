package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import nz.ac.canterbury.seng302.tab.repository.activity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityStatisticQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityStatisticQueryService.class);

    @Autowired
    private ActivityScoreRepository activityScoreRepository;

    @Autowired
    private ScoreEventStatisticRepository scoreEventRepository;

    @Autowired
    private SubstitutionStatisticRepository substitutionEventRepository;

    @Autowired
    private FactStatisticRepository factStatisticRepository;

    @Autowired
    private GenericEventStatisticRepository genericEventStatisticRepository;

    /**
     * Constructor for activity statistic query service (for use in testing)
     *
     * @param scoreEventRepository repository for scoring events
     * @param substitutionEventRepository repository for substitution events
     * @param genericEventStatisticRepository repository for generic events
     */
    public ActivityStatisticQueryService(
            ScoreEventStatisticRepository scoreEventRepository,
            SubstitutionStatisticRepository substitutionEventRepository,
            GenericEventStatisticRepository genericEventStatisticRepository) {
        this.scoreEventRepository = scoreEventRepository;
        this.substitutionEventRepository = substitutionEventRepository;
        this.genericEventStatisticRepository = genericEventStatisticRepository;
    }

    /**
     * Fetches all the event statistics for the given {@link Activity}, ordered by ascending time
     * within the activity. NOTE: This is not guaranteed to be type safe; assumes a known quantity
     * of type parameters for wildcard and appropriate care in handling the output.
     * @param activity the activity to query for
     * @return an ordered list of event statistics
     */
    public List<ActivityEventStatistic<?>> getAllActivityEventsInOrder(Activity activity) {
        LOGGER.info("Retrieving all activity events");
        return genericEventStatisticRepository
                .findAllByActivity_OrderByTimeAsc(activity)
                .stream()
                .sorted()
                .toList();
    }

    /**
     * Fetches all the score event statistics in favour of the given {@link Activity}'s team,
     * ordered by ascending time within the activity.
     * @param activity the activity to query for
     * @return an ordered list of score event statistics
     */
    public List<ScoreEventStatistic> getLineupScoreEvents (Activity activity) {
        LOGGER.info("Retrieving activity lineup score events");
        Team activityTeam = activity.getTeam();
        if (activityTeam != null) {
            return scoreEventRepository
                    .findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(activity, activityTeam)
                    .stream()
                    .sorted()
                    .toList();
        }
        return List.of();
    }

    /**
     * Fetches all the substitution event statistics affecting the given {@link Activity}'s lineup,
     * ordered by ascending time within the activity.
     * @param activity the activity to query for
     * @return an ordered list of substitution event statistics
     */
    public List<SubstitutionStatistic> getLineupSubstitutionEvents(Activity activity) {
        LOGGER.info("Retrieving activity lineup substitution events");
        Team activityTeam = activity.getTeam();
        if (activityTeam != null) {
            return substitutionEventRepository
                    .findAllByActivity_OrderByTimeAsc(activity)
                    .stream()
                    .filter(sub -> (
                                activityTeam.getMembers().contains(sub.getOriginalPlayer()) ||
                                activityTeam.getMembers().contains(sub.getNewPlayer())
                            )
                    ).sorted()
                    .toList();
        }
        return List.of();
    }
}
