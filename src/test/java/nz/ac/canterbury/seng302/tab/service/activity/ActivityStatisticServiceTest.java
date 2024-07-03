package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.FactStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import nz.ac.canterbury.seng302.tab.pojo.FactStatisticForm;
import nz.ac.canterbury.seng302.tab.pojo.ScoreStatisticForm;
import nz.ac.canterbury.seng302.tab.repository.activity.FactStatisticRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ScoreEventStatisticRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


class ActivityStatisticServiceTest {
    private Activity activity;
    @MockBean
    static ActivityStatisticService activityScoreStatisticService;


    @MockBean
    static ActivityStatisticService activityFactStatisticService;


    private UserEntity mockUser;

    private Team team;


    @BeforeEach
    void beforeEachTest() {
        Set<Sport> sports = new HashSet<>();
        Location location = new Location("a1", "a2", "s", "c", "p", "c1");
        mockUser = new UserEntity("ss", "ss", "ss", "testuser",
                "1111-11-11", sports, location);
        team = Team.createTestTeam();
        activity = new Activity("test",
                "2023-08-10T14:11:04.462288164",
                "2023-08-20T16:11:04.462288164",
                ActivityType.GAME,
                team
        );

        ActivityService activityServiceMock = Mockito.mock(ActivityService.class);
        // Return the activity when saved
        Mockito.when(activityServiceMock.saveActivity(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserService userServiceMock = Mockito.mock(UserService.class);
        Mockito.when(userServiceMock.getUserById(1L)).thenReturn(mockUser);

        TeamService teamServiceMock = Mockito.mock(TeamService.class);
        Mockito.when(teamServiceMock.getTeamById(1L)).thenReturn(Optional.ofNullable(team));

        ScoreEventStatisticRepository scoreEventStatisticRepositoryMock = Mockito.mock(ScoreEventStatisticRepository.class);
        Mockito.when(scoreEventStatisticRepositoryMock.save(Mockito.any())).thenReturn(null);

        activityScoreStatisticService = new ActivityStatisticService(activityServiceMock, userServiceMock, teamServiceMock,
                scoreEventStatisticRepositoryMock);

        FactStatisticRepository factEventStatisticRepositoryMock = Mockito.mock(FactStatisticRepository.class);
        Mockito.when(factEventStatisticRepositoryMock.save(Mockito.any())).thenReturn(null);

        activityFactStatisticService = new ActivityStatisticService(activityServiceMock, userServiceMock, teamServiceMock,
                factEventStatisticRepositoryMock);
    }

    @Test
    void givenValidScoreEventStatisticForm_whenAddedToActivity_thenStatisticIsSaved() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "1",
                "2023-08-10T15:11:04");

        Activity savedActivity = activityScoreStatisticService.saveScoreStatisticToActivity(scoreStatisticForm, activity);

        ScoreEventStatistic statistic = (ScoreEventStatistic) savedActivity.getEvents().get(0);

        Assertions.assertEquals(1, statistic.getPointValue());
        Assertions.assertEquals(mockUser, statistic.getScorer());
        Assertions.assertEquals(team, statistic.getTeamScoredFor());
        // One hour
        Assertions.assertEquals(3600, statistic.getTime());
    }

    @Test
    void givenNonExistentScorerId_whenFormIsSaved_thenExceptionThrown() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "-5436563463",
                "1",
                "2023-08-10T15:11:04");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> activityScoreStatisticService.saveScoreStatisticToActivity(scoreStatisticForm, activity));
    }

    @Test
    void validStatisticsForm_whenValidated_hasNoErrors() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "1",
                "2023-08-10T15:11:04");

        ArrayList<String> errors = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errors.add(invocation.getArgument(0))).when(bindingResultMock).reject(Mockito.any(), Mockito.any(), Mockito.any());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(0, errors.size());
    }

    @Test
    void nonexistentScorerId_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "-1",
                "1",
                "2023-08-10T15:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("scorerId.notFound", errorCodes.get(0));
    }

    @Test
    void invalidScorerId_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "NotANumber",
                "1",
                "2023-08-10T15:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("scorerId.invalid", errorCodes.get(0));
    }



    @Test
    void nonexistentTeamId_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "-1",
                "2023-08-10T15:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("teamScoredForId.notFound", errorCodes.get(0));
    }

    @Test
    void invalidTeamId_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "NotANumber",
                "2023-08-10T15:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("teamScoredForId.invalid", errorCodes.get(0));
    }

    @Test
    void timeBeforeActivityStart_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "1",
                "2023-08-10T10:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("time.beforeStart", errorCodes.get(0));
    }

    @Test
    void timeAfterActivityEnd_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "1",
                "2025-08-10T15:11:04");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("time.afterEnd", errorCodes.get(0));
    }

    @Test
    void invalidTime_whenValidated_showsError() {
        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "1",
                "1",
                "MORBIN");

        ArrayList<String> errorCodes = new ArrayList<>();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        activityScoreStatisticService.validateStatisticForm(
                scoreStatisticForm,
                activity,
                bindingResultMock);

        Assertions.assertEquals(1, errorCodes.size());
        Assertions.assertEquals("time.invalid", errorCodes.get(0));
    }

        @Test
        void givenValidFactStatisticForm_whenAddedToActivity_thenStatisticIsSaved() {
            String validDescription = "Booing from crowd!";
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    validDescription,
                    "2023-08-10T15:11:04");

            Activity savedActivity = activityFactStatisticService.saveFactStatisticToActivity(factStatisticForm, activity);

            FactStatistic fact = (FactStatistic) savedActivity.getEvents().get(0);

            Assertions.assertEquals(validDescription.length(), fact.getDescription().length());
            // One hour
            Assertions.assertEquals(3600, fact.getTime());
        }

        @Test
        void givenEmptyDescription_whenFormIsSaved_thenConstraintsViolated() {
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    "",
                    "2023-08-10T15:11:04");

            ArrayList<String> errorCodes = new ArrayList<>();

            BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
            Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                    .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            activityFactStatisticService.validateStatisticForm(
                    factStatisticForm,
                    activity,
                    bindingResultMock);

            Assertions.assertEquals(1, errorCodes.size());
            Assertions.assertEquals("description.invalid", errorCodes.get(0));
    }

        @Test
        void validFactStatisticsForm_whenValidated_hasNoErrors() {
            String validDescription = "Booing from crowd!";
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    validDescription,
                    "2023-08-10T15:11:04");

            ArrayList<String> errors = new ArrayList<>();

            BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
            Mockito.doAnswer(invocation -> errors.add(invocation.getArgument(0))).when(bindingResultMock).reject(Mockito.any(), Mockito.any(), Mockito.any());

            activityFactStatisticService.validateStatisticForm(
                    factStatisticForm,
                    activity,
                    bindingResultMock);

            Assertions.assertEquals(0, errors.size());
        }


        @Test
        void timeBeforeFactActivityStart_whenValidated_showsError() {
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    "BOO",
                    "2023-08-10T10:11:04");

            ArrayList<String> errorCodes = new ArrayList<>();

            BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
            Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                    .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            activityFactStatisticService.validateStatisticForm(
                    factStatisticForm,
                    activity,
                    bindingResultMock);

            Assertions.assertEquals(1, errorCodes.size());
            Assertions.assertEquals("time.beforeStart", errorCodes.get(0));
        }

        @Test
        void timeAfterFactActivityEnd_whenValidated_showsError() {
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    "BOO",
                    "2025-08-10T15:11:04");

            ArrayList<String> errorCodes = new ArrayList<>();

            BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
            Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                    .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            activityFactStatisticService.validateStatisticForm(
                    factStatisticForm,
                    activity,
                    bindingResultMock);

            Assertions.assertEquals(1, errorCodes.size());
            Assertions.assertEquals("time.afterEnd", errorCodes.get(0));
        }

        @Test
        void invalidFactTime_whenValidated_showsError() {
            FactStatisticForm factStatisticForm = new FactStatisticForm(
                    "BOO",
                    "MORBIN");

            ArrayList<String> errorCodes = new ArrayList<>();

            BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
            Mockito.doAnswer(invocation -> errorCodes.add(invocation.getArgument(1)))
                    .when(bindingResultMock).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

            activityFactStatisticService.validateStatisticForm(
                    factStatisticForm,
                    activity,
                    bindingResultMock);

            Assertions.assertEquals(1, errorCodes.size());
            Assertions.assertEquals("time.invalid", errorCodes.get(0));
        }
    }
