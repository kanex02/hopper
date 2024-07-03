package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;

/**
 * Represents a JSON activity score event with properties id, scorer, team,
 * first name, last name, time in activity, profile picture, score value.
 */
public class JsonActivityScoreEvent {

    /**
     * The ID of the event.
     */
    @JsonProperty
    private long id;

    /**
     * The scorer.
     */
    @JsonIgnore
    private UserEntity scorer;

    /**
     * The team scored for.
     */
    @JsonIgnore
    private Team team;

    /**
     * The first name of the scorer.
     */
    @JsonProperty
    private String firstName;

    /**
     * The last name of the scorer.
     */
    @JsonProperty
    private String lastName;

    /**
     * The time of occurrence in the activity.
     */
    @JsonProperty
    private long time;

    /**
     * The point value of the score event.
     */
    @JsonProperty
    private int pointValue;

    /**
     * The profile picture of the scorer.
     */
    @JsonProperty
    private String profilePicture;



    /**
     * Creates a new instance of JsonActivityScoreEvent with the provided properties.
     *
     * @param scoreEvent ScoreEventStatistic object to convert to json
     */
    @JsonIgnoreProperties("userRoles")
    public JsonActivityScoreEvent(ScoreEventStatistic scoreEvent) {
        this.id = scoreEvent.getId();
        this.scorer = scoreEvent.getScorer();
        this.team = scoreEvent.getTeamScoredFor();
        this.firstName = scorer.getFirstName();
        this.lastName = scorer.getLastName();
        this.time = scoreEvent.getTime();
        this.pointValue = scoreEvent.getPointValue();
        this.profilePicture = scorer.getProfilePicture();
    }

    public long getId() {
        return id;
    }

    public UserEntity getScorer() {
        return scorer;
    }

    public Team getTeam() {
        return team;
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

    public int getPointValue() {
        return pointValue;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Returns the path to the profile picture of the scorer.
     *
     * @return The path to the profile picture, or null if not set.
     */
    public String getProfilePicturePath() {
        return scorer.getProfilePicturePath();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "JsonActivityScoreEvent{" +
                "scoreEventId='" + id + '\'' +
                ", scorerFirstName=" + firstName +
                ", scorerLastName=" + lastName +
                ", timeInActivity=" + time +
                ", scorePointValue=" + pointValue +
                ", scorerProfilePicture=" + profilePicture +
                '}';
    }
}