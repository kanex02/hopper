package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.LineupRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.LineupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "/sql/activity_service.sql")
class ActivityServiceTest {


    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private LineupRepository lineupRepository;

    @Autowired
    private UserRepository userRepository;
    
    private LineupService lineupService;

    private ActivityService activityService;

    @BeforeEach
     void setup() {
        activityService = new ActivityService(activityRepository, userRepository);
        lineupService = new LineupService(lineupRepository);
    }

    @Test
    void getActivities_userActivities_validNumberOfActivities() {
        List<Activity> activityList = activityService.getActivitiesForUser(1L);
        assertEquals(4, activityList.size());
    }

    @Test
    void getActivities_teamActivities_validNumberOfActivities() {
        List<Activity> activityList = activityService.getActivitiesForTeam(1L);
        assertEquals(2, activityList.size());
    }

    @Test
    void getActivities_teamActivities_sortedByStartTime() {
        List<Activity> activityList = activityService.getActivitiesForTeam(1L);
        assertEquals(activityList.stream().sorted(Comparator.comparing(
                Activity::getStartTime)).toList(), activityList);
    }

    @Test
    void getActivities_userActivities_sortedByStartTime() {
        List<Activity> activityList = activityService.getActivitiesForUser(1L);
        assertEquals(activityList.stream().sorted(Comparator.comparing(
                Activity::getStartTime)).toList(),activityList);
    }

    @Test
    void paginateUserActivities_teamActivities_sortedByNameIgnoringCase() {
        Map<Team, List<Activity>> teamActivitymap = activityService.getTeamActivitiesGroupedByTeam(1L);
        List<Activity> personalActivities = activityService.getPersonalActivities(1L);
        Page<Activity> activityEntityPage = activityService.paginateUserActivities(PageRequest.of(0,
                10), personalActivities, teamActivitymap);

        List<String> original = activityEntityPage.getContent().stream().map(activity -> activity.getTeam().getTeamName())
                .toList();
        List<String> sorted = activityEntityPage.getContent().stream().map(activity -> activity.getTeam().getTeamName())
                        .sorted(String.CASE_INSENSITIVE_ORDER).toList();

        assertEquals(sorted, original);
    }
    
    @Test
     void convertToLongList_validStringList_stringConvertedToList() {
        String list = "[1,2,3]";
        List<Long> expected = List.of(1L,2L,3L);
        
        assertEquals(expected, ActivityService.convertStringToLongList(list));
    }
    
    @Test
     void convertToLongList_emptyStringList_stringConvertedToList() {
        String list = "[]";
        List<Long> expected = new ArrayList<>();
        
        assertEquals(expected, ActivityService.convertStringToLongList(list));
    }
    
    @Test
     void convertToLongList_invalidStringList_stringConvertedToList() {
        String list = "[sdsds, sdssd, sdds]";
        
        assertThrows(NumberFormatException.class, () -> ActivityService.convertStringToLongList(list));
    }
    
    @Test
     void addPlayersToActivityLineup_starterPlayers_playersAddedToStarters() {
        activityService.setLineupRepository(lineupRepository);
        List<Long> list = List.of(1L, 388L, 389L);
        Activity activity = activityService.getActivityById(1L);
        
        activityService.addPlayersToActivityLineup(activity, LineupRole.STARTER, list);
        assertEquals(3, lineupService.getStartingLineup(activity.getId()).size());
    }
    
    @Test
     void addPlayersToActivityLineup_subPlayers_playersAddedToSubs() {
        activityService.setLineupRepository(lineupRepository);
        List<Long> list = List.of(1L, 388L, 389L);
        Activity activity = activityService.getActivityById(1L);
        
        activityService.addPlayersToActivityLineup(activity, LineupRole.SUB, list);
        assertEquals(3, lineupService.getSubstituteLineup(activity.getId()).size());
    }
    
    @Test
     void addPlayersToActivityLineup_invalidPlayers_emptyLineup() {
        activityService.setLineupRepository(lineupRepository);
        List<Long> list = List.of(100L, 400L, 500L);
        Activity activity = activityService.getActivityById(1L);
    
        activityService.addPlayersToActivityLineup(activity, LineupRole.STARTER, list);
        assertEquals(0, lineupService.getSubstituteLineup(activity.getId()).size());
    }

    @Test
     void addPlayersToActivityLineup_deleteLineup_emptyLineup() {
        activityService.setLineupRepository(lineupRepository);
        List<Long> list = List.of(1L, 388L, 389L);
        Activity activity = activityService.getActivityById(1L);

        activityService.addPlayersToActivityLineup(activity, LineupRole.STARTER, list);

        activityService.resetLineup(activity);

        assertTrue(activity.getLineups().isEmpty());
    }

    @Test
    void whenUserHasActivities_getAllActivities_sortedByTime() {
        ActivityService activityServiceSpy = Mockito.spy(ActivityService.class);
        Activity activity1 = new Activity() {{
            setStartTime(LocalDateTime.now().toString());
        }};
        Activity activity2 = new Activity() {{
            setStartTime(LocalDateTime.now().toString());
        }};
        Activity activity3 = new Activity() {{
            setStartTime(LocalDateTime.now().plusHours(1).toString());
        }};
        Activity activity4 = new Activity() {{
            setStartTime(LocalDateTime.now().minusHours(1).toString());
        }};

        Mockito.doReturn(List.of(activity1, activity2, activity3)).when(activityServiceSpy).getPersonalActivities(Mockito.any());

        Mockito.doReturn(
                new HashMap<Team, List<Activity>>() {{
                    computeIfAbsent(new Team("test"), k -> new ArrayList<>()).add(activity4);
                }}
        ).when(activityServiceSpy).getTeamActivitiesGroupedByTeam(Mockito.any());

        List<Activity> result = activityServiceSpy.getAllActivitiesForUser(new UserEntity() {{setId(1L);}});
        List<Activity> expected = List.of(activity4, activity1, activity2, activity3);
        Assertions.assertEquals(expected, result);
    }


    @Test
    void whenUserHasActivities_getAllUpcomingActivities_onlyActivitiesInFuture() {
        ActivityService activityServiceSpy = Mockito.spy(ActivityService.class);
        Activity activity1 = new Activity() {{
            setStartTime(LocalDateTime.now().plusHours(1).toString());
        }};
        Activity activity2 = new Activity() {{
            setStartTime(LocalDateTime.now().minusHours(1).toString());
        }};

        Mockito.doReturn(List.of(activity1, activity2)).when(activityServiceSpy).getAllActivitiesForUser(Mockito.any());

        List<Activity> result = activityServiceSpy.getAllUpcomingActivitiesForUser(new UserEntity() {{setId(1L);}});
        List<Activity> expected = List.of(activity1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void whenUserDoesNotEnterALocationForActivity_activityIsCreated() {
        Location location = new Location();
        BindingResult bindingResult = new BeanPropertyBindingResult(location, "location");

        Boolean result = activityService.isLocationValid(location, bindingResult);
        Boolean expected = true;
        Assertions.assertEquals(expected,result);
    }

    @Test
    void whenUserEntersOnlySpacesForLocation_locationIsInvalid() {
        Location location = new Location(" "," "," "," "," "," ");
        BindingResult bindingResult = new BeanPropertyBindingResult(location, "location");

        Boolean result = activityService.isLocationValid(location, bindingResult);
        Boolean expected = false;
        Assertions.assertEquals(expected,result);

    }

    @Test
    void whenUserEntersTextForNonRequiredLocationField_locationIsInvalid() {
        Location location = new Location("","Nuh-uh","","","","");
        BindingResult bindingResult = new BeanPropertyBindingResult(location, "location");

        Boolean result = activityService.isLocationValid(location, bindingResult);
        Boolean expected = false;
        Assertions.assertEquals(expected,result);

    }

    @Test
    void whenUserEntersTextRequiredForLocation_locationIsValid() {
        Location location = new Location("32 Test Street","","Testington","TestVille","7357","Testopia");
        BindingResult bindingResult = new BeanPropertyBindingResult(location, "location");

        Boolean result = activityService.isLocationValid(location, bindingResult);
        Boolean expected = true;
        Assertions.assertEquals(expected,result);

    }
}
