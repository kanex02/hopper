package nz.ac.canterbury.seng302.tab.entity.challenge;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * Defines how to create {@link Challenge} factories for 'quantifiable' challenges. Quantifiable challenges are challenges
 * that can be directly related to performing a specific number of actions. For example, "Run 7 laps" is quantifiable
 * but "Try a new recipe" is not.
 * <p>
 * Quantifiable goals linearly relate the hops count to their goal with a multiplier.
 * <p>
 * These factories are stored as entities in the database.
 * ? Possible to move to an in-memory registry?
 */
@Entity
public class QuantifiableChallengeFactory implements ChallengeFactory {

    /**
     * Internally generated id of the factory
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The multiplier for the hops count. The final quantity of the goal is calculated as
     * {@code quantity = hops x multiplier}.
     */
    @Min(0)
    private final double hopsMultiplier;

    /**
     * The title of all {@link Challenge}s generated from this factory
     */
    @NotNull
    @Size(min = 1, max = Challenge.TITLE_MAX_LENGTH)
    @Pattern(regexp = Challenge.TITLE_PATTERN, message = Challenge.TITLE_ERROR_MESSAGE)
    private final String title;

    /**
     * The template string for {@link Challenge} goals generated with more than 1 action quantity.
     */
    @NotNull
    @Size(min = 1, max = Challenge.GOAL_MAX_LENGTH)
    private final String goalTemplate;

    /**
     * An optional sport to be paired with this challenge factory. Adding this sport will require that the user has this
     * sport be associated with users in some way, such as in their favourite sports, the sport of a team they belong to,
     * or the sport of a team they are a part of.
     */
    @ManyToOne
    @Nullable
    private final Sport sport;

    /**
     * JPA-required no args constructor, do not use this constructor!
     */
    protected QuantifiableChallengeFactory() {
        this(1.0, "", "", null);
    }

    /**
     * Creates a quantifiable challenge factory with no associated sport.
     *
     * @param hopsMultiplier {@link #hopsMultiplier}
     * @param title          {@link #title}
     * @param goalTemplate   {@link #goalTemplate}
     */
    public QuantifiableChallengeFactory(
            double hopsMultiplier,
            String title,
            String goalTemplate
    ) {
        this(hopsMultiplier, title, goalTemplate, null);
    }

    /**
     * Primary constructor for this factory class. Creates a quantifiable challenge factory with an associated sport.
     *
     * @param hopsMultiplier {@link #hopsMultiplier}
     * @param title          {@link #title}
     * @param goalTemplate   {@link #goalTemplate}
     * @param sport          {@link #sport}
     */
    public QuantifiableChallengeFactory(
            double hopsMultiplier,
            String title,
            String goalTemplate,
            @Nullable Sport sport
    ) {
        this.hopsMultiplier = hopsMultiplier;
        this.title = title;
        this.goalTemplate = goalTemplate;
        this.sport = sport;
    }

    /**
     * Creates a new quantifiable goal from a given set of valid params.
     *
     * @param hops             The hops count of the challenge
     * @param userGeneratedFor The user to create the challenge for
     * @param endDate          The end date of the challenge
     * @return Returns a new challenge with the given params and a pre-generated goal.
     */
    @Override
    public Challenge create(int hops, UserEntity userGeneratedFor, LocalDateTime endDate) {
        int quantity = this.getQuantityFromHops(hops);

        String goal;
        goal = String.format(goalTemplate, quantity);

        return new Challenge(userGeneratedFor, endDate, title, goal, hops);
    }


    /**
     * Checks that a challenge can be created from the given params.
     * <p>
     * If a sport is specified to this factory, then in order to be valid the given user must be associated with the sport
     * in some way, otherwise it is valid.
     *
     * @param hops The hops count to check
     * @param user The user to check
     * @return Returns true if the given params are valid for factory creation.
     */
    @Override
    public boolean canCreate(int hops, UserEntity user) {
        if (!ChallengeFactory.super.canCreate(hops, user)) {
            return false;
        }

        if (this.sport == null) {
            return true;
        }

        return user.getFavouriteSports().contains(this.sport);
    }

    /**
     * @return Returns {@link #sport}
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * @return Returns {@link #id}
     */
    public Long getId() {
        return id;
    }

    /**
     * @return Returns {@link #hopsMultiplier}
     */
    public double getHopsMultiplier() {
        return hopsMultiplier;
    }

    /**
     * @return Returns {@link #title}
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns {@link #goalTemplate}
     */
    public String getGoalTemplate() {
        return goalTemplate;
    }

    /**
     * Computes the quantity of the goal from the hops count, based on the {@link #hopsMultiplier}
     * <p>
     * Has a minimum returned value of 1.
     *
     * @param hops The hops count
     * @return Returns the quantity of the challenge
     */
    private int getQuantityFromHops(int hops) {
        return Math.max((int) (hops * hopsMultiplier), 1);
    }
}
