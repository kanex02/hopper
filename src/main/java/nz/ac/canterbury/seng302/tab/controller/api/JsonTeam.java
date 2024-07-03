package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.Team;

/**
 * Represents a JSON Team with relevant properties(id, name, picture, picturePath).
 */
public class JsonTeam {

    /**
     * The ID of the team.
     */
    @JsonProperty
    private long id;

    /**
     * The name of the team.
     */
    @JsonProperty
    private String name;

    /**
     * The picture path of the team
     */
    @JsonProperty
    private String picturePath;

    /**
     * The profile picture of the team
     */
    @JsonProperty
    private String picture;

    /**
     * Creates a new instance of JsonTeam with the provided properties.
     *
     * @param team The team to Jsonify.
     */
    public JsonTeam(Team team) {
        this.id = team.getId();
        this.name = team.getTeamName();
        this.picturePath = team.getImagePath();
        this.picture = team.getImage();
    }

    /**
     * Returns the ID of the team.
     *
     * @return The ID of the team.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the name of the team.
     *
     * @return The name of the team.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the picture path of the team.
     *
     * @return The picture path of the team.
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * Returns the path to the profile picture of the team.
     *
     * @return The path to the profile picture of the team.
     */
    public String getPicture() {
        return picture;
    }
}
