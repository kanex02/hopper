package nz.ac.canterbury.seng302.tab.entity.challenge;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * A template for a general challenge. This is a challenge that has a specific goal and a set number of hops
 */
@Entity
public class GeneralChallengeTemplate implements ChallengeFactory {

    /**
     * Internally generated ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The number of hops that this challenge will have. This is the number of hops that the user will receive when
     * completing the challenge.
     */
    private final int hops;

    /**
     * The title of the challenge. This is a string that will be displayed to the user to describe the challenge.
     */
    private final String title;

    /**
     * The goal of the challenge. This is a string that will be displayed to the user to describe the challenge.
     */
    private final String goal;

    /**
     * JPA-required no args constructor, do not use this constructor!
     */
    public GeneralChallengeTemplate() {
        this(1, "", "");
    }

    /**
     * Creates a quantifiable challenge factory with no associated sport.
     *
     * @param hops  The hops count of the challenge
     * @param title The title of the challenge
     * @param goal  The goal of the challenge
     */
    public GeneralChallengeTemplate(int hops, String title, String goal) {
        this.hops = hops;
        this.title = title;
        this.goal = goal;
    }

    /**
     * @return The hops count of the challenge
     */
    public int getHops() {
        return hops;
    }

    /**
     * @return The title of the challenge
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The goal of the challenge
     */
    public String getGoal() {
        return goal;
    }

    @Override
    public Challenge create(int hops, UserEntity userGeneratedFor, LocalDateTime endDate) {
        return new Challenge(
                userGeneratedFor,
                endDate,
                title,
                goal,
                hops
        );
    }

    @Override
    public boolean canCreate(int hops, UserEntity user) {
        return ChallengeFactory.super.canCreate(hops, user)
                && hops == this.hops;
    }
}
