package nz.ac.canterbury.seng302.tab.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeFormatterServiceTest {

    @Test
    void testNull() {
        String result = TimeFormatterService.getFormattedTimeSinceCreation(null);
        assertEquals(null, result);
    }

    @Test
    void testJustNow() {
        LocalDateTime now = LocalDateTime.now();
        String result = TimeFormatterService.getFormattedTimeSinceCreation(now);
        assertEquals("Just now", result);
    }

    @Test
    void testMinutesAgo() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(Duration.ofMinutes(5));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(fiveMinutesAgo);
        assertEquals("5 minutes ago", result);
    }

    @Test
    void testHoursAgo() {
        LocalDateTime threeHoursAgo = LocalDateTime.now().minus(Duration.ofHours(3));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(threeHoursAgo);
        assertEquals("3 hours ago", result);
    }

    @Test
    void testDaysAgo() {
        LocalDateTime fourDaysAgo = LocalDateTime.now().minus(Duration.ofDays(4));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(fourDaysAgo);
        assertEquals("4 days ago", result);
    }

    @Test
    void testWeeksAgo() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(Duration.ofDays(14));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(twoWeeksAgo);
        assertEquals("2 weeks ago", result);
    }

    @Test
    void testMonthsAgo() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minus(Duration.ofDays(90));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(threeMonthsAgo);
        assertEquals("3 months ago", result);
    }

    @Test
    void testYearsAgo() {
        LocalDateTime twoYearsAgo = LocalDateTime.now().minus(Duration.ofDays(730));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(twoYearsAgo);
        assertEquals("2 years ago", result);
    }

    @ParameterizedTest
    @CsvSource({
            "59, Just now",
            "60, 1 minute ago",
            "3600, 1 hour ago",
            "86400, 1 day ago",
            "604800, 1 week ago",
            "2419200, 1 month ago",
            "29030400, 1 year ago"
    })
    void testSingularUnits(long seconds, String expected) {
        LocalDateTime time = LocalDateTime.now().minus(Duration.ofSeconds(seconds));
        String result = TimeFormatterService.getFormattedTimeSinceCreation(time);
        assertEquals(expected, result);
    }

}
