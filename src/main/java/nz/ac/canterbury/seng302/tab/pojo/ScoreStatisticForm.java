package nz.ac.canterbury.seng302.tab.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * A form to store user input when submitting a score event. Call @Valid on this class to validate the user input.
 * @param pointValue Point value of the score
 * @param scorerId The id of the player who scored (they must be a registered user)
 * @param teamScoredForId The id of the team who the scorer scored for. Note that the scorer
 *                      does not necessarily have to be on this team; it is possible for the
 *                      scorer to have made an own goal.
 * @param time The time within a game that the event occurred at
 */
public record ScoreStatisticForm(
    @NotNull
    int pointValue,
    @NotNull
    String scorerId,
    @NotNull
    String teamScoredForId,
    @NotEmpty(message = "Time is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    String time
) {}
