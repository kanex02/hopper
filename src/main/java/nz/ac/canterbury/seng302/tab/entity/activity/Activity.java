package nz.ac.canterbury.seng302.tab.entity.activity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for Activity
 */
@Table(name = "activity")
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Activity {
    /**
     * References a location.
     * Please note: each location must be assigned to only ONE activity.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @Valid
    private Location location;

    /**
     * Unique key of the Activity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "activity_id"
    )
    private Long id;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "activity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ActivityEventStatistic<?>> events = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private ActivityScore activityScore;

    /**
     * The description of the activity
     */
    @Column(
            name = "description",
            nullable = false
    )
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9\s]{1,150}$",
            message = "Invalid description"
    )
    private String description;

    /**
     * The start date and time of the activity. Must be after the creation date of the associated {@link #team}
     * (if present) and before the {@link #endTime}. This is a {@link TemporalType#TIMESTAMP} as it requires
     * both a date and a time.
     */
    @Column(
            name = "start_time",
            nullable = false
    )
    @Temporal(TemporalType.TIMESTAMP)
    @NotBlank(
            message = "Start time is compulsory"
    )
    private String startTime;

    /**
     * The end date and time of the activity. Must be after the creation date of the associated {@link #team}
     * (if present) and after the {@link #startTime}. This is a {@link TemporalType#TIMESTAMP} as it requires
     * both a date and a time.
     */
    @Column(
            name = "end_time",
            nullable = false
    )
    @Temporal(TemporalType.TIMESTAMP)
    @NotBlank(
            message = "End time is compulsory"
    )
    private String endTime;

    /**
     * The type of the activity
     */
    @Column(
            name = "type",
            nullable = false
    )
    @NotNull(
            message = "Type is compulsory"
    )
    @Enumerated(EnumType.STRING)
    private ActivityType type;

    /**
     * An optional team association for the team. If type is {@link ActivityType#GAME} or {@link ActivityType#FRIENDLY}.
     * then this should not be null.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    /**
     * List of lineups for the activity.
     * This is a One-To-Many relationship between Activity and Lineup.
     */
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Lineup> lineups = new ArrayList<>();

    /**
     * The user who created this activity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Empty JPA required constructor
     */
    public Activity() {
    }

    public Activity(String description, String startTime, String endTime, ActivityType type, Team team) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.team = team;
    }

    /**
     * Constructor with all attributes
     *
     * @param description activity description
     * @param startTime   start time of activity
     * @param endTime     end time of activity
     * @param type        type of activity
     * @param team        team performing activity
     * @param lineups     lineups of activity
     */
    public Activity(String description, String startTime, String endTime, ActivityType type, Team team, List<Lineup> lineups) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.team = team;
        this.lineups = lineups;
    }

    /**
     * Checks that the user is allowed to edit this activity. A user can edit the activity if they are the user
     * who created the activity, or a manager of the activity's linked team (if present).
     * @param user
     * @return
     */
    public boolean canEdit(UserEntity user) {
        if (this.user.getId().equals(user.getId())) {
            return true;
        }

        if (this.team == null) {
            return false;
        }

        return this.team.isManager(user);
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Add a lineup to an activity
     *
     * @param lineup lineup to add
     */
    public void addLineup(Lineup lineup) {
        lineups.add(lineup);
    }

    /**
     * Method to clear the activity lineups list.
     */
    public void clearLineup() { lineups.clear(); }

    public List<Lineup> getLineups() { return lineups; }

    public List<ActivityEventStatistic<?>> getEvents() {
        return events;
    }

    public void setEvents(List<ActivityEventStatistic<?>> events) {
        this.events = events;
    }

    public ActivityScore getActivityScore() {
        return activityScore;
    }

    public void setActivityScore(ActivityScore activityScore) {
        this.activityScore = activityScore;
    }

    /**
     * Adds any event statistic to the activity's list of events, and registers this with the statistic itself
     * These can then be sorted by calling {@code this.getEvents().sort(null)}
     * @param event the event to be added
     */
    public void addEvent(ActivityEventStatistic<?> event) {
        this.events.add(event);
        event.setActivity(this);
    }

    public LocalDateTime getStartTimeAsLocalDateTime() {
        return LocalDateTime.parse(startTime);
    }

    public LocalDateTime getEndTimeAsLocalDateTime() {
        return LocalDateTime.parse(endTime);
    }


    /**
     * Gets a list of the {@link #events} in sorted order by time
     *
     * @return Returns the list of events sorted by time
     */
    public List<ActivityEventStatistic<?>> getSortedEvents() {
        return this.events.stream()
                .sorted()
                .toList();
    }

    /**
     * Returns whether an activity is before another one.
     * @param other activity to compare
     * @return -1 if this activity is before the activity to compare, 0 if the times are equal, or 1 if this is after the other
     */
    public int compareByDate(Activity other) {
        LocalDateTime a1Start = LocalDateTime.parse(this.getStartTime(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime a2Start = LocalDateTime.parse(other.getStartTime(), DateTimeFormatter.ISO_DATE_TIME);
        if (a1Start.isBefore(a2Start)) {
            return -1;
        } else if (a1Start.isEqual(a2Start)) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Returns string of the formatted date
     *
     * @return formatted string of the date
     */
    public String convertDateFormat(String date) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(date, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        return dateTime.format(outputFormatter);
    }
}
