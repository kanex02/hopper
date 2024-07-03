package nz.ac.canterbury.seng302.tab.entity;

import jakarta.validation.ConstraintViolationException;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ActivityTest {
    @Test
    void whenActivityLater_whenCompared_correctValueReturned() {

        Activity activity1 = new Activity() {{
            setStartTime(LocalDateTime.now().plusHours(1).toString());
        }};
        Activity activity2 = new Activity() {{
            setStartTime(LocalDateTime.now().minusHours(1).toString());
        }};

        Assertions.assertEquals(1, activity1.compareByDate(activity2));
    }

    @Test
    void whenActivityEarlier_whenCompared_correctValueReturned() {

        Activity activity1 = new Activity() {{
            setStartTime(LocalDateTime.now().plusHours(1).toString());
        }};
        Activity activity2 = new Activity() {{
            setStartTime(LocalDateTime.now().minusHours(1).toString());
        }};

        Assertions.assertEquals(-1, activity2.compareByDate(activity1));
    }


    @Test
    void whenActivitySameTime_whenCompared_correctValueReturned() {

        Activity activity1 = new Activity() {{
            setStartTime(LocalDateTime.now().plusHours(1).toString());
        }};

        Assertions.assertEquals(0, activity1.compareByDate(activity1));
    }

    @Test
    void testConvertDateFormat() {
        Activity activity = new Activity();
        String inputDate = "2022-09-28T18:00:00";
        String expectedOutput = "September 28, 2022";

        String actualOutput = activity.convertDateFormat(inputDate);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }
}
