package nz.ac.canterbury.seng302.tab.entity.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Represents a challenge's shared user along with its completion status for that user.
 */
@Entity
public class ChallengeCompletionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private boolean isCompleted = false;

    @Column(nullable = true)
    private LocalDateTime completedDate;

    /**
     * Default constructor for JPA; should not be used.
     */
    protected ChallengeCompletionStatus() {
    }

    /**
     * Creates a new challenge completion status mapping for a user.
     *
     * @param challenge     The challenge to create the status for
     * @param user          The user to create the status for
     * @param isCompleted   Whether the user has completed the challenge yet
     */
    public ChallengeCompletionStatus(Challenge challenge, UserEntity user, boolean isCompleted) {
        this.challenge = challenge;
        this.user = user;
        this.isCompleted = isCompleted;
    }


    /**
     * Equals method for ChallengeCompletionStatus, using the user and challenge objects as the unique identifiers.
     * @param o The object to compare to
     * @return true if the objects are to be considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeCompletionStatus that = (ChallengeCompletionStatus) o;
        return Objects.equals(this.user, that.user) &&
                Objects.equals(this.challenge, that.challenge);
    }


    /**
     * Hashcode method for ChallengeCompletionStatus, using the user and challenge objects as the unique identifiers.
     * @return The hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, challenge);
    }

    public Long getId() {
        return id;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public UserEntity getUser() {
        return user;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        completedDate = LocalDateTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }
}