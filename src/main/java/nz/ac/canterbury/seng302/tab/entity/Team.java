package nz.ac.canterbury.seng302.tab.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Entity class of a Team with a name and sport
 */
@Entity
@Table(name = "team")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    @NotBlank(message = "Team name is mandatory")
    @Size(min = 3, message = "Team name must be at least 3 characters")
    @Size(max = 30, message = "Team name must be less than 30 characters")
    @Pattern(regexp = "^(?=.*[\\p{L}\\p{N}])[.{} \\p{L}\\p{N}]+$", message = "Team name must contain a letter or number and only consist of letters, numbers, dots, and curly braces")
    private String teamName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sport_id", referencedColumnName = "sport_id")
    private Sport sport;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToMany
    @JoinTable(
            name = "team_managers",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> managers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "team_coaches",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> coaches = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> members = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "team_followers",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> teamFollowers = new HashSet<>();

    @OneToMany(
            mappedBy = "team",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Activity> activityList = new ArrayList<>();

    @Column(name = "date_created")
    @Temporal(TemporalType.DATE)
    private String dateCreated;

    /**
     * Name of the image -- image path is src/main/resources/static/team-profiles/{teamId}/
     */
    @Column
    private String image;

    /**
     * Will be the token for the user to input to join the team
     */
    @Column
    private String joinToken;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "teamProxy")
    private final List<BlogPost> userBlogPosts = new ArrayList<>();

    /**
     * JPA required no-args constructor
     */
    protected Team() {
    }

    /**
     * Creates a new Team object
     *
     * @param teamName name of team
     */
    public Team(String teamName) {
        this.teamName = teamName;
        this.dateCreated = LocalDateTime.now().toString();
    }

    /**
     * Creates a new Team object for use in testing validators
     *
     * @return a Team object with valid parameters
     */
    public static Team createTestTeam() {
        Team team = new Team("testTeamName");
        team.setId(1L);
        Sport teamSport = new Sport("Test Sport");
        teamSport.setId(1L);
        team.setSport(teamSport);
        return team;
    }

    /**
     * Checks if a user is a manager
     *
     * @param user The user to check
     * @return Returns true if the user is a manager of this team, false otherwise
     */
    public boolean isManager(UserEntity user) {
        return this.managers.contains(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    /**
     * Returns the team's join token
     *
     * @return team's join token
     */
    public String getJoinToken() {
        return joinToken;
    }

    /**
     * Set the token associated with the team
     *
     * @param token the token to be associated with the team
     */
    public void setJoinToken(String token) {
        this.joinToken = token;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + teamName + '\'' +
                ", sport='" + sport + '\'' +
                ", joinToken='" + joinToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) &&
            Objects.equals(teamName, team.teamName) &&
            Objects.equals(sport.getId(), team.sport.getId()) &&
            Objects.equals(sport.getName(), team.sport.getName()); //these conditions are severely inadequate
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamName, sport);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePath() {
        if (this.image == null) {
            return null;
        }
        return MediaConfig.getInstance().getFullPath(
                String.format("/team-profiles/%d/%s", this.id, this.image)
        ).toString();
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Adds a user as a manager, unless the team already has the maximum number of managers allowed (3).
     *
     * @param user User to add.
     */
    public void addManager(UserEntity user) throws IllegalStateException {
        if (this.managers.contains(user)) throw new IllegalArgumentException("User is already a manager");

        this.managers.add(user);
    }

    public void addCoach(UserEntity user) {
        this.coaches.add(user);
    }

    public void addMember(UserEntity user) {
        this.members.add(user);
    }

    /**
     * Removes a user as a manager, unless they are the last manager.
     *
     * @param user User to remove.
     */
    public void removeManager(UserEntity user) {
        if (!this.managers.contains(user))
            throw new IllegalArgumentException("User is not currently a manager of this team");
        this.managers.remove(user);
    }

    public void removeCoach(UserEntity user) {
        this.coaches.remove(user);
    }

    public void removeMember(UserEntity user) {
        this.members.remove(user);
    }

    public Set<UserEntity> getManagers() {
        return managers;
    }

    public void setManagers(Set<UserEntity> managers) {
        this.managers = managers;
    }

    public Set<UserEntity> getCoaches() {
        return coaches;
    }

    public void setCoaches(Set<UserEntity> coaches) {
        this.coaches = coaches;
    }

    public Set<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<UserEntity> members) {
        this.members = members;
    }

    /**
     * Gets all users (members, coaches and managers)
     *
     * @return Set of all users
     */
    public Set<UserEntity> getUsers() {
        Set<UserEntity> union = new HashSet<>();
        union.addAll(members);
        union.addAll(coaches);
        union.addAll(managers);

        return union;
    }

    public String getUserRole(UserEntity user) {
        if (managers.contains(user)) {
            return "manager";
        }
        if (coaches.contains(user)) {
            return "coach";
        }
        if (members.contains(user)) {
            return "member";
        }
        return "";
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void addUserPost(BlogPost blogPost) {
        this.userBlogPosts.add(blogPost);
    }

    public List<UserEntity> getAssociatedUsers() {
        return Stream.concat(this.managers.stream(), Stream.concat(this.coaches.stream(), this.members.stream()))
                .toList();
    }
    
    public Set<UserEntity> getTeamFollowers() {
        Set<UserEntity> followers = teamFollowers;
        followers.addAll(members);
        return followers;
    }

    public void addFollower(UserEntity user) {
        teamFollowers.add(user);
    }

    public void removeFollower(UserEntity user) {
        teamFollowers.remove(user);
    }
}
