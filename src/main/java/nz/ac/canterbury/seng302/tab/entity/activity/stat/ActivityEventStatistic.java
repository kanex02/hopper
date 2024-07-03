package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;

import java.util.Set;

/**
 * An activity event statistic is an activity statistic that occurred at a particular time within an activity.
 * <p>
 * Activity event statistics are comparable, and can be ordered by their time.
 * <p>
 * Event statistics also act as a parent class for all event statistics in a JPA database.
 * Uses the {@link InheritanceType#TABLE_PER_CLASS} inheritance strategy
 *
 * @param <T> The type of data recorded in the statistic
 * @see Activity
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "activity_event_statistic")
@DiscriminatorColumn(name = "type")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class ActivityEventStatistic<T> implements ActivityStatistic<T>, Comparable<ActivityEventStatistic<T>> {

    /**
     * The id of the event statistic in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "event_id"
    )
    private Long id;

    /**
     * The associated activity
     */
    @ManyToOne
    @JsonIgnore
    private Activity activity;

    /**
     * The time within a game that the event occurred at
     */
    private long time;

    /**
     * JPA required no-args constructor
     */
    protected ActivityEventStatistic() {
        this.time = -1L;
    }

    /**
     * Constructor for Event statistics that sets the time according to the supplied {@code builder}
     *
     * @param builder Abstract builder for activity events
     */
    protected ActivityEventStatistic(EventBuilder<?> builder) {
        this.time = builder.time;
    }

    /**
     * Compares this statistic to another based on their time
     *
     * @param other The other event statistic
     * @return Returns the result of {@link Long#compare(long, long)}, with the time of this and the other
     * event statistic being the parameters for comparison
     */
    @Override
    public int compareTo(ActivityEventStatistic<T> other) {
        return Long.compare(this.time, other.time);
    }

    /**
     * @return Returns the {@link #time}
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the {@link #time}
     *
     * @param time The new time value
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return Returns the {@link #id}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the {@link #id}
     *
     * @param id The new id value
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the {@link #activity}
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the {@link #activity}
     *
     * @param activity The new activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Abstract Builder class for event statistics.
     *
     * @param <S> The type of the event statistic.
     */
    public abstract static class EventBuilder<S extends ActivityEventStatistic<?>> {

        /**
         * The value to set the {@link ActivityEventStatistic#time} for after the event is built
         */
        protected long time = -1L;

        /**
         * Updates the builder parameter for {@link ActivityEventStatistic#time}
         *
         * @param time The time value
         * @return Returns this instance of the builder
         */
        public final EventBuilder<S> withTime(long time) {
            this.time = time;
            return this;
        }

        /**
         * Construct a new instance of the event statistic according to the current
         * builder parameters. Throws an {@link IllegalStateException} if the builder attempts to construct an
         * invalid statistic.
         *
         * @return a new instance of the event statistic
         * @throws IllegalStateException Thrown if the builder is attempting to create an invalid
         *                               statistic
         * @implNote Subclasses: do not attempt to override this function, instead override {@link #build()}
         */
        public final S buildAndValidate() {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                S statistic = this.build();
                Set<ConstraintViolation<S>> violations = validator.validate(statistic);
                if (violations.isEmpty()) {
                    return statistic;
                } else {
                    throw new IllegalStateException(violations.toString());
                }
            }
        }

        /**
         * Builds and returns a new instance of the statistic. Does not perform any validation
         *
         * @return Returns a new instance of the statistic type, without any validation checks
         */
        protected abstract S build();

    }
}
