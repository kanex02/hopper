package nz.ac.canterbury.seng302.tab.controller.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nz.ac.canterbury.seng302.tab.controller.api.JsonActivityScoreEvent;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.activity.ActivityStatisticQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * A RESTful API Controller class to provide score event query data.
 */
@RestController
@RequestMapping("/activity/getLineupScoreEvents")
public class ActivityScoreEventsAPIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitySubstitutionStatsAPIController.class);

    @Autowired
    private ActivityStatisticQueryService queryService;

    @Autowired
    private ActivityRepository activityRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * API endpoint for fetching score event data as JSON data.
     * Note: This is a REST API endpoint that returns application/json data.
     * It is not intended to be loaded as an HTML/Thymeleaf page.
     *
     * @param activityId The ID of the activity whose score events are to be queried.
     * @return A JSON string representation of a list of the score events.
     * Each event is represented by a JsonActivityScoreEvent object.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLineupScoreEvents(@RequestParam("activityId") Long activityId) {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        LOGGER.info(String.format("GET /activity/getLineupScoreEvents?activityId=%d", activityId));

        Optional<Activity> activityOptional = activityRepository.findById(activityId);

        List<ScoreEventStatistic> result = activityOptional.isPresent() ? queryService.getLineupScoreEvents(activityOptional.get()) : List.of();

        String json = "";

        List<JsonActivityScoreEvent> jsonScoreEvents = result.stream()
                .map(s -> new JsonActivityScoreEvent(s))
                .toList();

        try {
            // Convert list to JSON repr
            json = objectMapper.writeValueAsString(jsonScoreEvents);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
