package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.controller.activity.ActivityController;
import nz.ac.canterbury.seng302.tab.pojo.ScoreStatisticForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ActivityControllerTest {
    @Test
    void givenAnInvalidActivityID_whenPosting_return404() {
        ActivityService activityServiceMock = Mockito.mock(ActivityService.class);
        Mockito.doReturn(null).when(activityServiceMock).getActivityById(Mockito.any());

        ActivityController activityController = new ActivityController(activityServiceMock,
                null, null, null);

        ScoreStatisticForm scoreStatisticForm = new ScoreStatisticForm(
                1,
                "NotANumber",
                "1",
                "15:11:04");

        Assertions.assertEquals("error/404", activityController.createScoreEventStatistic(1L,
                scoreStatisticForm, null, null));
    }
}
