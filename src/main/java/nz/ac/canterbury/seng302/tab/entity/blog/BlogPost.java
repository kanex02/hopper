package nz.ac.canterbury.seng302.tab.entity.blog;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.TimeFormatterService;
import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a blog post
 */
@Entity
@Table(name = "blog_post")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BlogPost {

    @Column(name = "title")
    @Size(min=3, message = "Title is too short. Must be at least 3 characters.")
    @Size(max=100, message = "Title is too long. Must be less than 100 characters.")
    @NotBlank(message = "Title cannot be empty.")
    private String title;

    @Column(name = "description", length = 3000)
    @Size(max=3000, message = "Description is too long. Must be less than 3000 characters.")
    @Size(min=3, message = "Description is too short. Must be at least 3 characters.")
    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Author of the post
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    // Club posting on behalf of insertable=false, updatable=false
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clubProxy_id")
    private Club clubProxy;

    // Team posting on behalf of
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamProxy_id")
    private Team teamProxy;

    // Club we are posting to (based on the blogVisibility)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clubTarget_id")
    private Club clubTarget;

    // Team we are posting to (based on the blogVisibility)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamTarget_id")
    private Team teamTarget;

    // Who sees the post
    @Enumerated(EnumType.STRING)
    @Column
    private BlogVisibility blogVisibility;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column
    private LocalDateTime date;

    @Column
    private String fileName;

    // Ensures that you cannot post as both a club and team as well as only have one target club/team
    @PrePersist
    @PreUpdate
    public void validateProxiesAndTargets() {
        if (clubTarget != null && teamTarget != null) {
            throw new IllegalArgumentException("Both clubTarget and teamTarget cannot be non-null. At least one should be null.");
        }
        if (clubProxy != null && teamProxy != null) {
            throw new IllegalArgumentException("Both clubProxy and teamProxy cannot be non-null. At least one should be null.");
        }
    }

    @Column
    private boolean deletable = true;

    /**
     * Creates a new blog post
     * @param title The title of the blog post
     * @param description The description of the blog post
     */
    public BlogPost(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Creates a new blog post
     *
     * @param title       The title of the blog post
     * @param description The description of the blog post
     * @param author      The author of the blog post
     * @param date        The date of the blog post
     */
    public BlogPost(String title, String description, UserEntity author, LocalDateTime date) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.date = date;
    }

    /**
     * JPA requires an empty constructor
     */
    public BlogPost() {}

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Challenge getChallenge() { return challenge; }

    public void setChallenge(Challenge challenge) {this.challenge = challenge;}

    public Club getClubProxy() { return clubProxy; }

    public void setClubProxy(Club club) { this.clubProxy = club; }

    public Team getTeamProxy() { return teamProxy; }

    public void setTeamProxy(Team team) { this.teamProxy = team; }

    public Club getClubTarget() { return clubTarget; }

    public void setClubTarget(Club clubTarget) { this.clubTarget = clubTarget; }

    public Team getTeamTarget() { return teamTarget; }

    public void setTeamTarget(Team teamTarget) { this.teamTarget = teamTarget; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public BlogVisibility getBlogVisibility() { return blogVisibility; }

    public void setBlogVisibility(BlogVisibility blogVisibility) { this.blogVisibility = blogVisibility; }

    public UserEntity getAuthor() { return author; }

    public void setAuthor(UserEntity user) { this.author = user; }

    public boolean isChallengeCompletion() {
        return !deletable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Returns the image path of the club's profile picture
     *
     * @return club's profile picture path relative to the system
     */
    public String getFilePath() {
        if (this.fileName == null) {
            return null;
        }
        return MediaConfig.getInstance().getFullPath(
                String.format("/blog-posts/%d/%s", this.id, this.fileName)
        ).toString();
    }

    /**
     * Returns the file name of the image
     * @return the file name of the image
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Returns the type of the file, e.g. png, jpg, etc.
     * @return the type of the file
     */
    public String getMediaType() {
        if (this.fileName == null) {
            return null;
        }

        String type = this.fileName.substring(this.fileName.lastIndexOf(".") + 1);
        if (type.equals("png") || type.equals("jpg") || type.equals("jpeg") || type.equals("svg")) {
            return "IMAGE";
        } else if (type.equals("mp4")) {
            return "VIDEO";
        } else {
            return null;
        }
    }

    /**
     * Returns string of the formatted date
     *
     * @return formatted string of the date
     */
    public String convertDateFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        return date.format(formatter);
    }

    /**
     * Returns string of the formatted time since post
     *
     * @return formatted string of the time
     */
    public String getFormattedTimeSincePost() {
        return TimeFormatterService.getFormattedTimeSinceCreation(date);
    }

}
