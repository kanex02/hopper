package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.util.Objects;

/**
 * Statistic that records which player was substituted by whom.
 * <p>
 * Stores two {@link nz.ac.canterbury.seng302.tab.entity.UserEntity }
 */
@Entity
@Table(name = "substitution_statistic")
public class SubstitutionStatistic extends ActivityEventStatistic<SubstitutionStatistic.Substitution> {

    /**
     * The substitution of the statistic.
     * <p>
     * Embedded into the columns of this table, rather than a reference to another table.
     */
    @Embedded
    @NotNull
    @Valid
    private Substitution substitution;


    /**
     * JPA required no-args constructor. Creates a new instance of this class using a default {@link SubstitutionBuilder}
     */
    public SubstitutionStatistic() {
        this(new SubstitutionBuilder());
    }

    /**
     * Private builder constructor that creates a new instance of this class based on the parameters
     * provided by the given {@code builder}
     *
     * @param builder The builder to construct this new instance from
     */
    private SubstitutionStatistic(SubstitutionBuilder builder) {
        super(builder);
        this.substitution = builder.substitution;
    }

    /**
     * @return Returns a description of this substitution as its value
     */
    @Override
    public Substitution getValue() {
        return this.substitution;
    }

    /**
     * Gets the original player of the substitution.
     *
     * @return Returns the original player of the substitution, that is, the player that was substituted off
     * @implNote Returns the {@link Substitution#originalPlayer} from this object's {@link #substitution}
     */
    public UserEntity getOriginalPlayer() {
        return this.substitution.originalPlayer;
    }

    /**
     * Gets the original player of the substitution.
     *
     * @return Returns the original player of the substitution, that is, the player that was substituted off
     * @implNote Returns the {@link Substitution#newPlayer} from this object's {@link #substitution}
     */
    public UserEntity getNewPlayer() {
        return this.substitution.newPlayer;
    }

    /**
     * Embeddable substitution object that stores the original and new players in a substitution.
     */
    @Embeddable
    public static class Substitution {

        /**
         * Player that was substituted out
         */
        @OneToOne
        @NotNull
        private UserEntity originalPlayer;

        /**
         * Player that was substituted in
         */
        @OneToOne
        @NotNull
        private UserEntity newPlayer;

        /**
         * Required JPA no args constructor
         */
        protected Substitution() {
            this(null, null);
        }

        public Substitution(UserEntity originalPlayer, UserEntity newPlayer) {
            this.originalPlayer = originalPlayer;
            this.newPlayer = newPlayer;
        }

        /**
         * @return Returns the {@link #originalPlayer}, the player that was substituted off
         */
        public UserEntity getOriginalPlayer() {
            return originalPlayer;
        }

        /**
         * @return Returns the {@link #newPlayer}, the player that was substituted on
         */
        public UserEntity getNewPlayer() {
            return newPlayer;
        }

        /**
         * Checks that two substitutions are equal.
         * <p>
         * Two substitutions are equal if and only if the respective user IDs are equal
         *
         * @param o Other object to compare to
         * @return Returns true if {@code this} is equal {@code o}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Substitution that = (Substitution) o;
            return originalPlayer.getId().equals(that.originalPlayer.getId())
                    && newPlayer.getId().equals(that.newPlayer.getId());
        }

        /**
         * Hashes a substitution event
         *
         * @return The hash of the user ID's
         */
        @Override
        public int hashCode() {
            return Objects.hash(originalPlayer.getId(), newPlayer.getId());
        }
    }

    /**
     * Concrete builder class for {@link SubstitutionStatistic}s
     */
    public static class SubstitutionBuilder extends EventBuilder<SubstitutionStatistic> {

        /**
         * Builder parameter for {@link SubstitutionStatistic#substitution}
         */
        private Substitution substitution;

        /**
         * Shorthand for updating the builder parameter for {@link SubstitutionStatistic#substitution}
         * by just giving two players
         *
         * @param originalPlayer The player that was taken off
         * @param newPlayer      The player put on
         * @return Returns this instance of the builder
         */
        public SubstitutionBuilder withPlayers(UserEntity originalPlayer, UserEntity newPlayer) {
            return withSubstitution(new Substitution(originalPlayer, newPlayer));
        }

        /**
         * Updates the builder parameter for {@link SubstitutionStatistic#substitution}
         *
         * @param substitution The substitution that occurred
         * @return Returns this instance of the builder
         */
        public SubstitutionBuilder withSubstitution(Substitution substitution) {
            this.substitution = substitution;
            return this;
        }

        /**
         * Construct a new instance of the score event statistic according to the current
         * builder parameters after checking that the statistic is valid
         *
         * @return a new instance of the event statistic
         */
        @Override
        protected SubstitutionStatistic build() {
            return new SubstitutionStatistic(this);
        }

    }
}
