package nz.ac.canterbury.seng302.tab.service.notification;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A service that emits events to observers
 */
@Service
public class EventEmitter {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    /**
     * Emits an event to all observers
     *
     * @param event The event to emit
     */
    public void emit(NotificationEvent event) {
        for (NotificationObserver observer : observers) {
            observer.handleEvent(event);
        }
    }
}
