package nz.ac.canterbury.seng302.tab.entity.club;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;
import nz.ac.canterbury.seng302.tab.validation.DoesNotContainEmoji;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a Club entity
 * The club entity holds information about a club like its ID, name, description, associated sport, and teams.
 * It also keeps track of the user who created the club.
 */
@Entity
@Table(name = "club")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Club {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "club_name")
    @Size(min=3, message = "Name is too short.")
    @Size(max=50, message = "Name is too long.")
    @Pattern(
            regexp = "^([.{} ]*[\\p{L}\\p{N}]+(?:'\\p{L}*)*[.{} ]*)*$",
            message = "Name contains invalid characters."
    )
    @DoesNotContainEmoji(
            message = "The club name may not contain emojis!"
    )
    private String name;
    
    @Column(name = "club_description")
    @Size(max=150, message = "Description is too long.")
    @Size(min=1, message = "Description is too short.")
    @Pattern(
            regexp = "^(.*\\p{L}+.*)*$",
            message = "Description must contain some letters"
    )
    @DoesNotContainEmoji(
            message = "The club description may not contain emojis!"
    )
    private String description;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private Set<Team> teams = new HashSet<>();
    
    @Column(name = "club_image")
    private String imageName;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sport_id", referencedColumnName = "sport_id")
    private Sport associatedSport;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity creator;

    @OneToMany(mappedBy = "clubProxy")
    private final List<BlogPost> userBlogPosts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "club_followers",
        joinColumns = @JoinColumn(name = "club_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> clubFollowers = new HashSet<>();

    /**
     * Protected no-arg constructor required by JPA.
     */
    protected Club() {}
    
    /**
     * Club constructor that constructs a club with name and description
     *
     * @param name name of the club
     * @param description description of the club
     */
    public Club(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
    
    public String getImageName() { return imageName; }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    /**
     * Returns the image path of the club's profile picture
     *
     * @return club's profile picture path relative to the system
     */
    public String getImagePath() {
        if (this.imageName == null) {
            return null;
        }
        return MediaConfig.getInstance().getFullPath(
            String.format("/club-profiles/%d/%s", this.id, this.imageName)
        ).toString();
    }
    
    public Sport getAssociatedSport() { return associatedSport; }
    
    public void setAssociatedSport(Sport sport) {
        this.associatedSport = sport;
    }
    
    public Location getLocation() { return location; }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Long getId() { return id; }
    
    public Set<Team> getTeams() { return teams; }
    
    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }
    
    /**
     * Adds a team to the club's team set and sets this club as the team's club.
     * The team must not already be part of another club.
     * @param team the team to be added to the club
     */
    public void addTeam(Team team) {
        if (team.getClub() != null) return;
        this.teams.add(team);
        team.setClub(this);
    }
    
    /**
     * Removes all teams associated with the current club.
     * This also dissociates the club from the removed teams.
     */
    public void removeAllTeams() {
        for(Team team : new ArrayList<>(teams)) {
            this.removeClubFromTeam(team);
        }
    }

    /**
     * Removes a specific team from the current club.
     * This also dissociates the club from the team.
     *
     * @param team The team to be removed from the club.
     */
    public void removeClubFromTeam(Team team) {
        this.teams.remove(team);
        if (team.getClub() == this) {
            team.setClub(null);
        }
    }

    public UserEntity getCreator() {
        return creator;
    }
    
    public void setCreator(UserEntity user) {
        this.creator = user;
    }

    public void addUserPost(BlogPost blogPost) {
        userBlogPosts.add(blogPost);
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets all users (members of the club's teams) in the club.
     * @return A list of users in the club.
     */
    public List<UserEntity> getUsers() {
        List<UserEntity> members = teams.stream().map(Team::getAssociatedUsers).flatMap(List::stream).toList();
        return Stream.concat(members.stream(), Stream.of(creator)).toList();
    }

    public Set<UserEntity> getClubFollowers() {
        return clubFollowers;
    }

    public void addFollower(UserEntity user) {
        clubFollowers.add(user);
    }

    public void removeFollower(UserEntity user) {
        clubFollowers.remove(user);
    }
}

