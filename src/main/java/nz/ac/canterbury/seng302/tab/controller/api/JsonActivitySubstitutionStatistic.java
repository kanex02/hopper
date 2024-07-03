package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;

/**
 * Represents a JSON activity substitution statistic with properties id, original player,
 * substituted player, their name, time in activity, profile picture.
 */
public class JsonActivitySubstitutionStatistic {

    /**
     * The ID of the statistic.
     */
    @JsonProperty
    private long id;

    /**
     * The player substituted off.
     */
    @JsonIgnore
    private UserEntity originalPlayer;

    /**
     * The player substituted on.
     */
    @JsonIgnore
    private UserEntity newPlayer;

    /**
     * The first name of the substitute player.
     */
    @JsonProperty("newPlayerFirstName")
    private String firstName;

    /**
     * The last name of the substitute player.
     */
    @JsonProperty("newPlayerLastName")
    private String lastName;

    /**
     * The time of occurrence in the activity.
     */
    @JsonProperty
    private long time;

    /**
     * The profile picture of the substitute player.
     */
    @JsonProperty
    private String profilePicture;



    /**
     * Creates a new instance of JsonActivitySubstitutionStatistic with the provided properties.
     *
     * @param subStatistic SubstitutionStatistic object to convert to json
     */
    public JsonActivitySubstitutionStatistic(SubstitutionStatistic subStatistic) {
        this.id = subStatistic.getId();
        this.originalPlayer = subStatistic.getOriginalPlayer();
        this.newPlayer = subStatistic.getNewPlayer();
        this.firstName = newPlayer.getFirstName();
        this.lastName = newPlayer.getLastName();
        this.time = subStatistic.getTime();
        this.profilePicture = newPlayer.getProfilePicture();
    }

    public long getId() {
        return id;
    }

    public UserEntity getOriginalPlayer() {
        return originalPlayer;
    }

    public UserEntity getNewPlayer() {
        return newPlayer;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getTime() {
        return time;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Returns the path to the profile picture of the substitute player.
     * If no profile picture is set, null is returned.
     *
     * @return The path to the profile picture, or null if not set.
     */
    public String getProfilePicturePath() {
        if (this.profilePicture == null) {
            return null;
        }

        return String.format("/public/uploads/images/user-profiles/%d/%s", this.newPlayer.getId(), this.profilePicture);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "JsonActivityScoreStatistic{" +
                "subStatisticId='" + id + '\'' +
                ", newPlayerFirstName=" + firstName +
                ", newPlayerLastName=" + lastName +
                ", timeInActivity=" + time +
                ", scorerProfilePicture=" + profilePicture +
                '}';
    }
}