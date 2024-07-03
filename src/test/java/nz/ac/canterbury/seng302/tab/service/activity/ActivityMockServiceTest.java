package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ActivityMockServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void getActivityById_validId_activityRetrieved() {
        Long activityId = 1L;
        Activity expectedActivity = new Activity("blah",
                "2023-05-10T02:01:25.585678484Z",
                "1234",
                ActivityType.GAME,
                new Team("test"), null);

        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(expectedActivity));

        Activity actualActivity = activityService.getActivityById(activityId);

        Assertions.assertEquals(expectedActivity, actualActivity);
    }

    @Test
    void getActivityById_invalidId_NoActivityRetrieved() {
        Mockito.when(activityRepository.findById(999L)).thenReturn(Optional.empty());
        Activity activity = activityService.getActivityById(999L);

        Assertions.assertNull(activity);
    }
}
