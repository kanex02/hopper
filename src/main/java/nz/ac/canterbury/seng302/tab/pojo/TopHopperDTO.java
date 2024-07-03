package nz.ac.canterbury.seng302.tab.pojo;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public class TopHopperDTO {
    private UserEntity user;
    private Long totalHops;

    private LocalDateTime latestChallengeCompletion;

    public TopHopperDTO(UserEntity user, Long totalHops, LocalDateTime latestChallengeCompletion) {
        this.user = user;
        this.totalHops = totalHops;
        this.latestChallengeCompletion = latestChallengeCompletion;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getLatestChallengeCompletion() {
        return latestChallengeCompletion;
    }

    public void setLatestChallengeCompletion(LocalDateTime latestChallengeCompletion) {
        this.latestChallengeCompletion = latestChallengeCompletion;
    }

    /**
     * Checks that two TopHoppers are equal.
     * <p>
     * Two TopHoppers are equal if they are both for the same User
     *
     * @param o Other object to compare to
     * @return Returns true if {@code this} is equal {@code o}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopHopperDTO that = (TopHopperDTO) o;
        return this.user == that.user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    public Long getTotalHops() {
        return totalHops;
    }

    public void setTotalHops(Long totalHops) {
        this.totalHops = totalHops;
    }
}