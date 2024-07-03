package nz.ac.canterbury.seng302.tab.entity.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * Factory interface that defines methods on how to create {@link Challenge}s.
 */
public interface ChallengeFactory {

    /**
     * Creates a new challenge for the user with the hops count that ends on a given end date
     * <p>
     * Pre-condition:
     * The {@code user} and {@code hops} are valid for creation based on {@link #canCreate(int, UserEntity)}
     * <p>
     * Post-condition:
     * The returned challenge is a new instance of a {@link Challenge} that is associated to the {@code user},
     * gives the {@code hops} as its hops reward, and ends on the {@code endDate}.
     *
     * @param hops             The hops count of the challenge
     * @param userGeneratedFor The user to create the challenge for
     * @param endDate          The end date of the challenge
     * @return Returns a new challenge with the given params and a pre-generated goal.
     */
    Challenge create(int hops, UserEntity userGeneratedFor, LocalDateTime endDate);

    /**
     * Checks that the given hops and user are valid params for challenge creation in this factory. Use to check
     * the pre-condition of {@link #create(int, UserEntity, LocalDateTime)}.
     *
     * @param hops The hops count to check
     * @param user The user to check
     * @return Returns true if these params can be used challenges in this factory
     * @implSpec Always call this method in overrides.
     * @implNote The base validation condition is that hops are positive and the user is non-null.
     */
    default boolean canCreate(int hops, UserEntity user) {
        return hops > 0 && user != null;
    }

}
