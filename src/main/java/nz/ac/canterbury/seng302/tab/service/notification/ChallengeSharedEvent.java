package nz.ac.canterbury.seng302.tab.service.notification;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;

import java.util.List;

/**
 * A notification event that is emitted when a challenge is shared
 */
public class ChallengeSharedEvent implements NotificationEvent {
    private Long challengeId;
    private List<UserEntity> invitedUsers;
    private UserEntity relatedUser;

    @Override
    public String getEventType() {
        return "ChallengeShared";
    }


    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public List<UserEntity> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<UserEntity> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public UserEntity getRelatedUser() {
        return relatedUser;
    }

    public void setRelatedUser(UserEntity relatedUser) {
        this.relatedUser = relatedUser;
    }
}