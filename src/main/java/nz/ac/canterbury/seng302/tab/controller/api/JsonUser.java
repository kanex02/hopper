package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;

/**
 * Represents a JSON user object with properties id, first name, last name, and profile picture.
 */
public class JsonUser {
    
    /**
     * The ID of the user.
     */
    @JsonProperty
    private long id;
    
    /**
     * The first name of the user.
     */
    @JsonProperty
    private String firstName;
    
    /**
     * The last name of the user.
     */
    @JsonProperty
    private String lastName;
    
    /**
     * The profile picture of the user.
     */
    @JsonProperty
    private String profilePicture;
    
    /**
     * Image name of the user's border
     */
    @JsonProperty
    private String borderImageName;
    
    /**
     * Creates a new instance of JsonUser with the provided properties.
     *
     * @param user user object to convert to json
     */
    public JsonUser(UserEntity user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePicture = user.getProfilePicture();
        this.borderImageName = user.getBorder() == null ? null : user.getBorder().getPath();
    }
    
    /**
     * Returns the ID of the user.
     *
     * @return The ID of the user.
     */
    public long getId() {
        return id;
    }
    
    /**
     * Returns the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Returns the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Returns the border name of the user
     *
     * @return The border name of the user
     */
    public String getBorderImageName() { return borderImageName; }
    
    /**
     * Returns the path to the profile picture of the user.
     * If no profile picture is set, null is returned.
     *
     * @return The path to the profile picture, or null if not set.
     */
    public String getProfilePicturePath() {
        if (this.profilePicture == null) {
            return null;
        }
    
        return MediaConfig.getInstance().getFullPath(
            String.format("/user-profiles/%d/%s", this.id, this.profilePicture)
        ).toString();
    }
    
    /**
     * Returns a string representation of the JsonUser object.
     *
     * @return A string representation of the JsonUser object.
     */
    @Override
    public String toString() {
        return "JsonUser{" +
            "id='" + id + '\'' +
            ", firstName=" + firstName +
            ", lastName=" + lastName +
            ", profilePicture=" + profilePicture +
            ", borderImage=" + borderImageName +
            '}';
    }
}