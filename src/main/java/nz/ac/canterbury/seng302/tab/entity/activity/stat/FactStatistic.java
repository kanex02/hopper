package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Fact statistic that simply stores a description an event.
 * <p>
 * Stores a description as a {@link String}
 */
@Entity
@Table(name = "fact_statistic")
public class FactStatistic extends ActivityEventStatistic<String> {

    /**
     * Description of the activity, limited to 255 characters (incl. line terminators).
     */
    @Column
    @Pattern(regexp = "(?s)^.{1,255}$")
    @NotNull
    private String description;

    /**
     * JPA required no-args constructor. Creates a new instance of this class using a default {@link FactBuilder}
     */
    public FactStatistic() {
        this(new FactBuilder());
    }

    /**
     * Private builder constructor that creates a new instance of this class based on the parameters
     * provided by the given {@code builder}
     *
     * @param builder The builder to construct this new instance from
     */
    private FactStatistic(FactBuilder builder) {
        super(builder);
        this.description = builder.description;
    }

    /**
     * @return Returns the description of this event as its value
     */
    @Override
    public String getValue() {
        return this.getDescription();
    }

    /**
     * @return Returns the {@link #description}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the {@link #description}
     *
     * @param description The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Concrete builder class for {@link FactStatistic}s
     */
    public static class FactBuilder extends EventBuilder<FactStatistic> {

        /**
         * Builder parameter for {@link FactStatistic#description}
         */
        private String description = "";

        /**
         * Updates the builder parameter for {@link FactStatistic#description}
         *
         * @param description The description of the event
         * @return Returns this instance of the builder
         */
        public FactBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Construct a new instance of the score event statistic according to the current
         * builder parameters
         *
         * @return a new instance of the event statistic
         */
        @Override
        protected FactStatistic build() {
            return new FactStatistic(this);
        }

    }
}
