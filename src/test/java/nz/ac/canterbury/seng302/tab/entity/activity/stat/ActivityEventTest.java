package nz.ac.canterbury.seng302.tab.entity.activity.stat;


import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ActivityEventTest {

    private List<ActivityEventStatistic<?>> eventStatistics;

    @BeforeEach
    void setup() {
        var builder = new ScoreEventStatistic.ScoreEventBuilder();
        eventStatistics = new ArrayList<>(
                List.of(
                        // note: using build() to avoid constraint violation due to lack of team
                        builder.withTime(6L).build(),
                        builder.withTime(Long.MAX_VALUE).build(),
                        builder.withTime(10L).build(),
                        builder.withTime(3L).build(),
                        builder.withTime(0L).build(),
                        builder.withTime(-1L).build(),
                        builder.withTime(4L).build()
                ));
    }

    @Test
    void givenAnUnsortedListOfActivityEvents_WhenSorted_ThenEventsSortedByTime() {
        eventStatistics.sort(null);

        Assertions.assertEquals(
                List.of(-1L, 0L, 3L, 4L, 6L, 10L, Long.MAX_VALUE),
                eventStatistics.stream().map(ActivityEventStatistic::getTime).toList()
        );
    }

    @Test
    void givenAnActivity_whenGetSortedEvents_thenEventsAreSortedByTime() {
        var activity = new Activity(
                "description",
                LocalDateTime.now().toString(),
                LocalDateTime.now().plusHours(1L).toString(),
                ActivityType.OTHER,
                null
        );

        eventStatistics.forEach(activity::addEvent);

        Assertions.assertNotEquals(activity.getEvents(), activity.getSortedEvents());
        Assertions.assertEquals(
                List.of(-1L, 0L, 3L, 4L, 6L, 10L, Long.MAX_VALUE),
                activity.getSortedEvents()
                        .stream().map(ActivityEventStatistic::getTime).toList()
        );
    }
}
