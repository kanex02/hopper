package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Service for finding and using {@link ActivityStatisticFactory}s
 */
@Service
public class ActivityStatisticFactories {

    /**
     * Maps event type names to factories
     */
    private final Map<String, ActivityStatisticFactory<?>> factories = new HashMap<>();

    private final UserService userService;

    /**
     * Creates an instance of this service class. Do not call this; Spring manages the lifecycle of this class
     *
     * @param userService The user service to use
     */
    public ActivityStatisticFactories(UserService userService) {
        this.userService = userService;
        registerFactory("substitution", new SubstitutionEventFactory());
    }

    /**
     * Register a factory name to a factory
     *
     * @param type    The type name of the factory to register
     * @param factory The factory to be registered
     */
    public void registerFactory(String type, ActivityStatisticFactory<?> factory) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null");
        }

        if (factories.containsKey(type)) {
            throw new IllegalArgumentException("Cannot register duplicate event type '" + type + "'");
        }

        factories.put(type, factory);
    }

    /**
     * Gets the event statistic factory for a named type
     *
     * @param type The name of the factory to get
     * @return Returns the factory if found.
     * @throws NoSuchElementException Thrown if the type is not a known name of a factory
     */
    public ActivityStatisticFactory<?> getFactory(String type) {
        if (factories.containsKey(type)) {
            return factories.get(type);
        }

        throw new NoSuchElementException("Invalid type '" + type + "'");
    }

    /**
     * Finds and parses a factory named by the {@code type}
     *
     * @param type The name of the type of the factory to get for parsing
     * @param raw  The raw data to parse
     * @return Returns the parsed ActivityEventStatistic, that is represented by {@code raw}
     * @throws IllegalArgumentException Thrown if an error occurs during parsing
     * @throws NoSuchElementException   Thrown if the type is not a known name of a factory
     */
    public ActivityEventStatistic<?> parseWithType(
            String type,
            Map<String, String> raw
    ) throws NoSuchElementException, IllegalArgumentException {
        ActivityStatisticFactory<?> factory = getFactory(type);

        return factory.parse(raw, userService);
    }


}
