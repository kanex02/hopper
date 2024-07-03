package nz.ac.canterbury.seng302.tab.service.notification;

/**
 * An interface for a notification event that is emitted by the event emitter
 */
public interface NotificationEvent {
    String getEventType();  // returns the type of event, e.g., "ChallengeShared"
}