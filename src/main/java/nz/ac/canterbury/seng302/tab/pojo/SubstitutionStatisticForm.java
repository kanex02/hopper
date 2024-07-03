package nz.ac.canterbury.seng302.tab.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;

/**
 * Input record for creating substitution statistics
 *
 * @param time           The time the event occurred. See: {@link SubstitutionStatistic#getTime()}
 * @param originalPlayer The original player of the substitution. See: {@link SubstitutionStatistic#getOriginalPlayer()}
 * @param newPlayer      The new player of the substitution. See: {@link SubstitutionStatistic#getNewPlayer()}
 */
public record SubstitutionStatisticForm(
        @Min(-1) int time,
        @NotNull UserEntity originalPlayer,
        @NotNull UserEntity newPlayer
) {

}
