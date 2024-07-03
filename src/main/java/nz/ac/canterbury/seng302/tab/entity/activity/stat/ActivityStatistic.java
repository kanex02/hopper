package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;

/**
 * Generic interface for activity statistics. This is just a basic representation for all statistics,
 * and contains only a single statistical value.
 * <p>
 * Activity statistics are just simple pieces of data attached to an {@link Activity},
 * such as the team scores, facts about the activity, and when players scored.
 *
 * @param <T> The type of the statistical value
 * @see Activity
 * @see ActivityEventStatistic
 */
public interface ActivityStatistic<T> {

    /**
     * @return Returns the statistical value stored in this statistic
     */
    T getValue();

}
