package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
class ActivityServiceValidationTest {
    public static final BindingResult MOCKED_BINDING_RESULT = Mockito.mock(BindingResult.class);
    private Activity activity;
    private UserEntity user;
    private Team team;
    private List<Lineup> lineups = new ArrayList<>();
    private ActivityService activityService;

    @BeforeEach
    void setup() {
        user = new UserEntity(
                "SecurePassword123!#%",
                "Download-MuseSwipr",
                "On-Steam-Now",
                "email",
                "1970-01-01",
                Set.of(),
                null
        );
        team = Team.createTestTeam();
        team.setDateCreated(LocalDateTime.now().minusYears(1).toString());
        activityService = new ActivityService(null, null);
        lineups.add(new Lineup(activity, user));
    }

    @Test
    void valid_activity_created_by_manager_returns_true() {
        team.addManager(user);
        activity = new Activity("",
                LocalDateTime.now().plusDays(10).toString(),
                LocalDateTime.now().plusDays(20).toString(),
                ActivityType.GAME,
                team,
                lineups);

        Assertions.assertTrue(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void valid_activity_created_by_coach_returns_true() {
        team.addCoach(user);
        activity = new Activity("",
                LocalDateTime.now().plusDays(10).toString(),
                LocalDateTime.now().plusDays(20).toString(),
                ActivityType.GAME,
                team,
                lineups);

        Assertions.assertTrue(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void valid_activity_without_team_returns_true() {
        activity = new Activity("",
                LocalDateTime.now().plusDays(10).toString(),
                LocalDateTime.now().plusDays(20).toString(),
                ActivityType.OTHER,
                null,
                lineups);

        Assertions.assertTrue(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void activity_with_team_and_unauthorised_user_returns_false() {
        activity = new Activity("",
                LocalDateTime.now().plusDays(10).toString(),
                LocalDateTime.now().plusDays(20).toString(),
                ActivityType.GAME,
                team,
                lineups);


        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void activity_with_start_time_after_end_time_returns_false() {
        activity = new Activity("",
                LocalDateTime.now().plusDays(20).toString(),
                LocalDateTime.now().plusDays(10).toString(),
                ActivityType.OTHER,
                null,
                null);

        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }


    @Test
    void activity_with_start_time_before_team_creation_returns_false() {
        team.addManager(user);
        activity = new Activity("",
                LocalDateTime.now().minusYears(3).toString(),
                LocalDateTime.now().plusDays(10).toString(),
                ActivityType.GAME,
                team,
                lineups);

        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void activity_with_type_game_without_team_returns_false() {
        activity = new Activity("",
                LocalDateTime.now().minusYears(3).toString(),
                LocalDateTime.now().plusDays(10).toString(),
                ActivityType.GAME,
                null,
                null);

        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void activity_with_type_friendly_without_team_returns_false() {
        activity = new Activity("",
                LocalDateTime.now().minusYears(3).toString(),
                LocalDateTime.now().plusDays(10).toString(),
                ActivityType.FRIENDLY,
                null,
                null);

        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }

    @Test
    void activity_with_invalid_start_time_returns_false() {
        activity = new Activity("",
                "L",
                "RATIO",
                ActivityType.OTHER,
                null,
                null);

        Assertions.assertFalse(activityService.isValid(activity, user, MOCKED_BINDING_RESULT));
    }
}