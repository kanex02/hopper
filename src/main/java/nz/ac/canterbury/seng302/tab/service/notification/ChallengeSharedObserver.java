package nz.ac.canterbury.seng302.tab.service.notification;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * An observer that listens for challenge shared events and generates a notification
 */
@Service
public class ChallengeSharedObserver implements NotificationObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeSharedObserver.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * Handles the event
     *
     * @param event The event to handle
     */
    @Override
    public void handleEvent(NotificationEvent event) {
        LOGGER.info("Received an event in ChallengeSharedObserver...");
        if (event instanceof ChallengeSharedEvent challengeEvent) {
            LOGGER.info("Processing ChallengeSharedEvent...");
            Long challengeId = challengeEvent.getChallengeId();
            List<UserEntity> invitedUsers = challengeEvent.getInvitedUsers();
            UserEntity relatedUser = challengeEvent.getRelatedUser();
            notificationService.generateNotificationForSharedChallenge("shared a challenge", invitedUsers, relatedUser, challengeId);
        }
    }

}