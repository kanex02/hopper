package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import org.springframework.data.util.Pair;

/**
 * The total score of both teams in an {@link Activity}
 * <p>
 * Uses a tuple of two integers as its statistical value.
 */
@Entity
@Table(name = "activity_score")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ActivityScore implements ActivityStatistic<Pair<Integer, Integer>> {

    /**
     * The id the activity score in its database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The associated activity
     */
    @OneToOne(mappedBy = "activityScore")
    private Activity activity;

    /**
     * The score of the "home" (left) team
     */
    @Column(name = "home_score")
    private Integer homeScore;

    /**
     * The score of the "away" (right) team
     */
    @Column(name = "away_score")
    private Integer awayScore;

    /**
     * Stores the values of {@link #homeScore} and {@link #awayScore} in a tuple.
     * <p>
     * Marked as {@link Transient} to prevent storage of duplicate data, but kept as a field to pool RAM and
     * not create new objects on every invocation of {@link #getValue()}
     */
    @Transient
    private Pair<Integer, Integer> currentScore;

    /**
     * JPA required no-args constructor, sets both scores to 0/
     */
    protected ActivityScore() {
        this(0, 0);
    }

    /**
     * Creates a new instance of this class with the home and away scores set
     *
     * @param homeScore The initial value of {@link #homeScore}
     * @param awayScore The initial value of {@link #awayScore}
     */
    public ActivityScore(int homeScore, int awayScore) {
        this.updateScores(homeScore, awayScore);
    }

    /**
     * The statistic value is a tuple of two integers, where the left value is the {@link #homeScore} and the right
     * value is the {@link #awayScore}
     *
     * @return Returns {@link #currentScore}, which stores the home score and away score
     */
    @Override
    public Pair<Integer, Integer> getValue() {
        return this.currentScore;
    }

    /**
     * Updates the scores of the statistic. Resets the {@link #currentScore} to match the new valus of homeScore
     * and awayScore as well
     *
     * @param homeScore The new value for {@link #homeScore}
     * @param awayScore The new value for {@link #awayScore}
     */
    public void updateScores(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.currentScore = Pair.of(homeScore, awayScore);
    }

    /**
     * @return Returns the {@link #homeScore}
     */
    public int getHomeScore() {
        return homeScore;
    }

    /**
     * @return Returns the {@link #awayScore}
     */
    public int getAwayScore() {
        return awayScore;
    }

    /**
     * @return Returns the {@link #id}
     */
    public Long getId() {
        return id;
    }

    /**
     * Updates the value of {@link #id}
     *
     * @param id The new id value
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public Pair<Integer, Integer> getCurrentScore() {
        return currentScore;
    }
}
