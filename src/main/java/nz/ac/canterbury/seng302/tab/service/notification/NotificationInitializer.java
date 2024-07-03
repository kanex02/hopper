package nz.ac.canterbury.seng302.tab.service.notification;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationInitializer {

    @Autowired
    private EventEmitter eventEmitter;

    @Autowired
    private ChallengeSharedObserver challengeSharedObserver;

    /**
     * Registers observers with the event emitter
     */
    @PostConstruct
    public void init() {
        eventEmitter.registerObserver(challengeSharedObserver);
        // Register more observers here
    }
}
