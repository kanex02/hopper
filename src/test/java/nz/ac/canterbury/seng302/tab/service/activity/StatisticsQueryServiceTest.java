package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.FactStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import nz.ac.canterbury.seng302.tab.repository.activity.GenericEventStatisticRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ScoreEventStatisticRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.SubstitutionStatisticRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StatisticsQueryServiceTest {

    @Mock
    private static ScoreEventStatisticRepository scoreEventRepository;

    @Mock
    private static SubstitutionStatisticRepository substitutionEventRepository;
    private static SubstitutionStatistic realSubstitution1;
    private static SubstitutionStatistic realSubstitution2;

    @Mock
    private GenericEventStatisticRepository genericEventStatisticRepository;

    @InjectMocks
    private ActivityStatisticQueryService queryService;
    
    private static List<ActivityEventStatistic<?>> allEvents = new ArrayList<>();
    
    private static List<FactStatistic> facts;
    
    private static List<ScoreEventStatistic> scores;
    
    private static List<SubstitutionStatistic> substitutions;
    private static UserEntity mockUser1;
    private static UserEntity mockUser2;

    private static UserEntity mockUser3;
    private static Team team;
    private static Activity activity;

    @BeforeAll
    static void setup() {
        Set<Sport> sports = new HashSet<>();
        Location location = new Location("a1", "a2", "s", "c", "p", "c1");
        mockUser1 = new UserEntity("ss", "ss", "ss", "mockuser1",
                "1111-11-11", sports, location);
        mockUser1.setId(1L);
        mockUser2 = new UserEntity("aa", "aa", "aa", "mockuser2",
                "1111-11-11", sports, location);
        mockUser2.setId(2L);
        mockUser3 = new UserEntity("aa", "aa", "aa", "mockuser3",
                "1111-11-11", sports, location);
        mockUser3.setId(3L);
        team = new Team("testTeam");
        team.setMembers(Set.of(mockUser1, mockUser2));
        activity = new Activity("test",
                LocalDateTime.now().plusDays(10).toString(),
                LocalDateTime.now().plusDays(20).toString(),
                ActivityType.GAME,
                team);

        var factBuilder = new FactStatistic.FactBuilder();
        var scoreBuilder = new ScoreEventStatistic.ScoreEventBuilder()
                .withScorer(mockUser1)
                .withTeam(team);
        var substitutionBuilder = new SubstitutionStatistic.SubstitutionBuilder();
        facts = List.of(
                        factBuilder.withDescription("Fact event").withTime(6L).buildAndValidate(),
                        factBuilder.withTime(Long.MAX_VALUE).buildAndValidate(),
                        factBuilder.withTime(10L).buildAndValidate(),
                        factBuilder.withTime(3L).buildAndValidate(),
                        factBuilder.withTime(0L).buildAndValidate(),
                        factBuilder.withTime(-1L).buildAndValidate(),
                        factBuilder.withTime(4L).buildAndValidate()
                );
        scores = List.of(
                        scoreBuilder.withTime(Long.MAX_VALUE).buildAndValidate(),
                        scoreBuilder.withTime(10L).buildAndValidate(),
                        scoreBuilder.withTime(3L).buildAndValidate(),
                        scoreBuilder.withTime(-1L).buildAndValidate(),
                        scoreBuilder.withTime(4L).buildAndValidate(),
                        scoreBuilder.withTime(0L).buildAndValidate(),
                        scoreBuilder.withTime(6L).buildAndValidate()
                );
        substitutions = List.of(
                        substitutionBuilder.withPlayers(mockUser3, mockUser3).withTime(Long.MAX_VALUE).buildAndValidate(),
                        substitutionBuilder.withTime(10L).buildAndValidate(),
                        substitutionBuilder.withTime(0L).buildAndValidate(),
                        substitutionBuilder.withTime(-1L).buildAndValidate(),
                        substitutionBuilder.withTime(4L).buildAndValidate(),
        realSubstitution1 = substitutionBuilder.withPlayers(mockUser1, mockUser2).withTime(6L).buildAndValidate(),
        realSubstitution2 = substitutionBuilder.withPlayers(mockUser2, mockUser1).withTime(3L).buildAndValidate()
                );
        allEvents.addAll(facts);
        allEvents.addAll(scores);
        allEvents.addAll(substitutions);

    }
    @Test
    void givenActivityWithEvents_WhenGetAllActivityEventsInOrder_AllEventsReturned() {
        activity.setEvents(allEvents);

        Mockito.when(genericEventStatisticRepository.findAllByActivity_OrderByTimeAsc(any(Activity.class)))
                .thenReturn(allEvents.stream().sorted().toList());

        var result = queryService.getAllActivityEventsInOrder(activity);

        assertEquals(allEvents.size(), result.size());
        Mockito.verify(genericEventStatisticRepository,
                        Mockito.times(1))
                            .findAllByActivity_OrderByTimeAsc(any(Activity.class));
    }

    @Test
    void givenActivityWithoutEvents_WhenGetAllActivityEventsInOrder_NoEventsReturned() {

        Mockito.when(genericEventStatisticRepository.findAllByActivity_OrderByTimeAsc(any(Activity.class)))
                .thenReturn(List.of());

        var result = queryService.getAllActivityEventsInOrder(activity);

        assertEquals(0, result.size());
        Mockito.verify(genericEventStatisticRepository,
                Mockito.times(1))
                    .findAllByActivity_OrderByTimeAsc(any(Activity.class));
    }

    @Test
    void givenActivityWithEvents_WhenGetLineupScoreEvents_DesiredEventsReturned() {
        activity.setEvents(allEvents);

        Mockito.when(scoreEventRepository
                    .findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(any(Activity.class), any(Team.class)))
                    .thenReturn(scores.stream().sorted().toList());

        var result = queryService.getLineupScoreEvents(activity);

        assertEquals(scores.size(), result.size());
        Mockito.verify(scoreEventRepository,
                Mockito.times(1))
                    .findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(any(Activity.class), any(Team.class));
    }

    @Test
    void givenActivityWithoutEvents_WhenGetLineupScoreEvents_NoEventsReturned() {

        Mockito.when(scoreEventRepository
                    .findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(any(Activity.class), any(Team.class)))
                    .thenReturn(List.of());

        var result = queryService.getLineupScoreEvents(activity);

        assertEquals(0, result.size());
        Mockito.verify(scoreEventRepository,
                Mockito.times(1))
                    .findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(any(Activity.class), any(Team.class));
    }

    @Test
    void givenActivityWithEvents_WhenGetLineupSubstitutionEvents_DesiredEventsReturned() {
        activity.setEvents(allEvents);
        Team activityTeam = activity.getTeam();

        Mockito.when(substitutionEventRepository.findAllByActivity_OrderByTimeAsc(any(Activity.class)))
                .thenReturn(substitutions
                        .stream()
                        .filter(sub -> (
                                        activityTeam.getMembers().contains(sub.getOriginalPlayer()) ||
                                        activityTeam.getMembers().contains(sub.getNewPlayer())
                                )
                        ).sorted()
                        .toList());

        var result = queryService.getLineupSubstitutionEvents(activity);

        assertEquals(2, result.size());
        assertEquals(result, List.of(realSubstitution2, realSubstitution1));
        Mockito.verify(substitutionEventRepository,
                Mockito.times(1))
                    .findAllByActivity_OrderByTimeAsc(any(Activity.class));
    }

    @Test
    void givenActivityWithoutEvents_WhenGetLineupSubstitutionEvents_NoEventsReturned() {

        Mockito.when(substitutionEventRepository.findAllByActivity_OrderByTimeAsc(any(Activity.class)))
                .thenReturn(List.of());

        var result = queryService.getLineupSubstitutionEvents(activity);

        assertEquals(0,result.size());
        Mockito.verify(substitutionEventRepository, Mockito.times(1)).findAllByActivity_OrderByTimeAsc(any(Activity.class));
    }
}