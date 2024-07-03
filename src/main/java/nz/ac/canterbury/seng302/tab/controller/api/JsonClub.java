package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.club.Club;

/**
 * Represents a JSON Club with relevant properties(id, name, picture, picturePath).
 */
public class JsonClub {
    /**
     * The ID of the club.
     */
    @JsonProperty
    private long id;

    /**
     * The name of the club.
     */
    @JsonProperty
    private String name;

    /**
     * The picture path of the club
     */
    @JsonProperty
    private String picturePath;

    /**
     * The profile picture of the club
     */
    @JsonProperty
    private String picture;

    /**
     * Creates a new instance of JsonClub with the provided properties.
     *
     * @param club The club to Jsonify.
     */
    public JsonClub(Club club){
        this.id = club.getId();
        this.name = club.getName();
        this.picturePath = club.getImagePath();
        this.picture = club.getImageName();
    }

    /**
     * Returns the ID of the club.
     *
 * @return The ID of the club.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the name of the club.
     *
     * @return The name of the club.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the profile picture of the club.
     *
     * @return The path to the profile picture of the club.
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * Returns the profile picture of the club.
     *
     * @return The profile picture of the club.
     */
    public String getPicture() {
        return picture;
    }
}
