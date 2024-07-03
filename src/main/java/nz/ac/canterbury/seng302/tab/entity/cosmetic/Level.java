package nz.ac.canterbury.seng302.tab.entity.cosmetic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * A class to store a level, that references a cosmetic reward.
 */
@Entity
public class Level {
    @Id
    private Long id;

    @OneToOne
    private Cosmetic reward;

    /**
     * JPA required no-args constructor.
     */
    public Level() {}

    /**
     * Creates a level.
     * @param id id of the level
     * @param reward the reward for the level
     */
    public Level(Long id, Cosmetic reward) {
        this.id = id;
        this.reward = reward;
    }

    /**
     * Gets the reward for the level.
     * @return the reward for the level
     */
    public Cosmetic getReward() {
        return reward;
    }

    /**
     * Gets the id of the level.
     * @return the id of the level
     */
    public Long getId() {
        return id;
    }
}
