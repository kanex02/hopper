package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;

/**
 * Event statistic that stores when a player scored, for what team they scored, and the point value
 * of their score.
 * <p>
 * Stores an {@link Integer} representing the point value of the score as its statistical value
 */
@Entity
@Table(name = "score_event_statistic")
public class ScoreEventStatistic extends ActivityEventStatistic<Integer> {

    /**
     * Point value of the score.
     */
    @NotNull
    private int pointValue;

    /**
     * The player who scored (they must be a registered user)
     */
    @ManyToOne
    @NotNull
    private UserEntity scorer;

    /**
     * The team who the {@link #scorer} scored for. Note that the scorer does not necessarily have to be on this
     * team; it is possible for the scorer to have made an own goal.
     */
    @ManyToOne
    @NotNull
    private Team teamScoredFor;

    /**
     * JPA required no-args constructor. Creates a new instance of this class using a default {@link ScoreEventBuilder}
     */
    public ScoreEventStatistic() {
        this(new ScoreEventBuilder());
    }

    /**
     * Private builder constructor that creates a new instance of this class based on the parameters
     * provided by the given {@code builder}
     *
     * @param builder The builder to construct this new instance from
     */
    private ScoreEventStatistic(ScoreEventBuilder builder) {
        super(builder);
        this.pointValue = builder.pointValue;
        this.scorer = builder.scorer;
        this.teamScoredFor = builder.teamScoredFor;
    }

    /**
     * @return Returns the point value of this event as its statistical value
     */
    @Override
    public Integer getValue() {
        return this.getPointValue();
    }

    /**
     * @return Returns the {@link #pointValue}
     */
    public int getPointValue() {
        return pointValue;
    }

    /**
     * Sets the {@link #pointValue}
     *
     * @param pointValue The new point value
     */
    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

    /**
     * @return Returns the {@link #scorer}
     */
    public UserEntity getScorer() {
        return scorer;
    }

    /**
     * Sets the {@link #scorer}
     *
     * @param scorer The new scorer
     */
    public void setScorer(UserEntity scorer) {
        this.scorer = scorer;
    }

    /**
     * @return Returns the {@link #teamScoredFor}
     */
    public Team getTeamScoredFor() {
        return teamScoredFor;
    }


    /**
     * Sets the {@link #teamScoredFor}
     *
     * @param teamScoredFor The new team that was scored for
     */
    public void setTeamScoredFor(Team teamScoredFor) {
        this.teamScoredFor = teamScoredFor;
    }

    /**
     * Concrete builder class for {@link ScoreEventStatistic}s
     */
    public static class ScoreEventBuilder extends EventBuilder<ScoreEventStatistic> {

        /**
         * Builder parameter for {@link ScoreEventStatistic#pointValue}
         */
        private int pointValue = 0;

        /**
         * Builder parameter for {@link ScoreEventStatistic#scorer}
         */
        private UserEntity scorer = null;

        /**
         * Builder parameter for {@link ScoreEventStatistic#teamScoredFor}
         */
        private Team teamScoredFor = null;

        /**
         * Updates the builder parameter for {@link ScoreEventStatistic#pointValue}
         *
         * @param pointValue The point value
         * @return Returns this instance of the builder
         */
        public ScoreEventBuilder withPointValue(int pointValue) {
            this.pointValue = pointValue;
            return this;
        }

        /**
         * Updates the builder parameter for {@link ScoreEventStatistic#scorer}
         *
         * @param scorer The scorer
         * @return Returns this instance of the builder
         */
        public ScoreEventBuilder withScorer(UserEntity scorer) {
            this.scorer = scorer;
            return this;
        }

        /**
         * Updates the builder parameter for {@link ScoreEventStatistic#teamScoredFor}
         *
         * @param team The team
         * @return Returns this instance of the builder
         */
        public ScoreEventBuilder withTeam(Team team) {
            this.teamScoredFor = team;
            return this;
        }

        /**
         * Construct a new instance of the score event statistic according to the current
         * builder parameters
         *
         * @return a new instance of the event statistic
         */
        @Override
        protected ScoreEventStatistic build() {
            return new ScoreEventStatistic(this);
        }

    }
}
