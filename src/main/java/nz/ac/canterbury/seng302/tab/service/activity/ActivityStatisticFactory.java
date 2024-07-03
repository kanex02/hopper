package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.service.UserService;

import java.util.Map;

/**
 * Defines an abstract factory for creating {@link ActivityEventStatistic}s from JSON maps
 *
 * @param <T> The type of the activity statistic
 */
public interface ActivityStatisticFactory<T extends ActivityEventStatistic<?>> {

    /**
     * Parses a raw data object into a {@link ActivityEventStatistic}.
     *
     * @param raw The raw data to parse
     * @return Returns the event statistic that {@code raw} data represents
     * @throws IllegalArgumentException Thrown if there is an error in parsing. E.g., a value is missing or invalid.
     */
    T parse(Map<String, String> raw, UserService userService) throws IllegalArgumentException;

}
