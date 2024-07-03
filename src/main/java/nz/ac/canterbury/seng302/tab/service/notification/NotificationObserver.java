package nz.ac.canterbury.seng302.tab.service.notification;

/**
 * An interface for observers of notification events
 */
public interface NotificationObserver {

    void handleEvent(NotificationEvent event);

}
