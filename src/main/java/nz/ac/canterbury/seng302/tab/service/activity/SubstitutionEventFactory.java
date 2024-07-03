package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import nz.ac.canterbury.seng302.tab.service.UserService;

import java.util.Map;

/**
 * Factory implementation for parsing {@link SubstitutionStatistic}s
 */
public class SubstitutionEventFactory implements ActivityStatisticFactory<SubstitutionStatistic> {

    /**
     * Data key for the time
     */
    public static final String TIME_KEY = "sub_time";

    /**
     * Data key for the original player's ID
     */
    public static final String ORIGINAL_PLAYER_ID_KEY = "original_player";

    /**
     * Data key for the new player's ID
     */
    public static final String NEW_PLAYER_ID_KEY = "new_player";


    /**
     * Parses raw form data into a {@link SubstitutionStatistic}.
     *
     * Form data must have the following fields:
     * <pre>
     *     {@value #TIME_KEY}: long >= -1
     *     {@value #ORIGINAL_PLAYER_ID_KEY}: a valid user ID
     *     {@value #NEW_PLAYER_ID_KEY}: a valid user ID
     * </pre>
     *
     *
     * @param raw The raw json object to parse
     * @param userService A user service to retrieve user data from
     * @return Returns a new substitution that is represented by the {@code raw} data
     * @throws IllegalArgumentException Thrown if there is an error in parsing. E.g., a value is missing or invalid.
     */
    @Override
    public SubstitutionStatistic parse(Map<String, String> raw, UserService userService) throws IllegalArgumentException {
        long time;
        UserEntity originalPlayer;
        UserEntity newPlayer;
        try {
            time = Long.parseLong(raw.get(TIME_KEY));
            originalPlayer = userService.getUserById(Long.parseLong(raw.get(ORIGINAL_PLAYER_ID_KEY)));
            newPlayer = userService.getUserById(Long.parseLong(raw.get(NEW_PLAYER_ID_KEY)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid factory data " + raw.toString(), e);
        }

        var builder = new SubstitutionStatistic.SubstitutionBuilder();
        builder.withTime(time);
        builder.withPlayers(originalPlayer, newPlayer);

        return builder.buildAndValidate();
    }
}
