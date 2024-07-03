package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.TimeFormatterService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a notification for a user.
 */
@Entity(name = "notification")
public class Notification {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="notification_id")
    private Long id;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    /**
     * All users that the notification is for
     */
    @ManyToMany()
    @JoinTable(
            name = "notified_users",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> notifiedUsers = new ArrayList<>();

    /**
     * All users that have not seen or read the notification
     */
    @ManyToMany()
    @JoinTable(
            name = "unread_users",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> unreadUsers = new ArrayList<>();

    /**
     * The user that the notification is related to (i.e. the user that sent the challenge share request)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "related_user")
    private UserEntity relatedUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "related_club")
    private Club relatedClub;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "related_team")
    private Team relatedTeam;

    @Column(name = "challenge_id")
    private Long challengeId;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    /**
     * Method to ensure that the notification only has one relation
     * @throws IllegalStateException if the notification has more than one relation
     */
    @PrePersist
    @PreUpdate
    public void validateOneRelation() {
        if ((relatedUser != null && relatedClub != null) ||
                (relatedUser != null && relatedTeam != null) ||
                    (relatedClub != null && relatedTeam != null)) {
            throw new IllegalStateException("Notification can only have one relation");
        }
    }

    public Notification() {
        // Default constructor for JPA
    }

    /**
     * Constructor for a notification
     * @param description The description of the notification
     * @param notifiedUsers The users that the notification is for
     * @param relatedUser The user that the notification is related to
     * @param relatedClub The club that the notification is related to
     * @param relatedTeam The team that the notification is related to
     * @param challengeId The id of the challenge that the notification is related to
     * @param dateCreated The date that the notification was created
     */
    public Notification(String description, List<UserEntity> notifiedUsers, UserEntity relatedUser, Club relatedClub, Team relatedTeam, Long challengeId, LocalDateTime dateCreated) {
        this.description = description;
        this.relatedUser = relatedUser;
        this.relatedClub = relatedClub;
        this.relatedTeam = relatedTeam;
        this.challengeId = challengeId;
        this.dateCreated = dateCreated;
        this.notifiedUsers.addAll(notifiedUsers);
        this.unreadUsers.addAll(notifiedUsers);
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

    public UserEntity getRelatedUser() {
        return relatedUser;
    }

    public void setRelatedUser(UserEntity relatedUser) {
        this.relatedUser = relatedUser;
    }

    public Club getRelatedClub() {
        return relatedClub;
    }

    public void setRelatedClub(Club relatedClub) {
        this.relatedClub = relatedClub;
    }

    public Team getRelatedTeam() {
        return relatedTeam;
    }

    public void setRelatedTeam(Team relatedTeam) {
        this.relatedTeam = relatedTeam;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public List<UserEntity> getUnreadUsers() {
        return unreadUsers;
    }

    /**
     * Removes user from the unread list when they have read / seen the notificaiton.
     * @param user to remove from the unread list
     */
    public void readNotification(UserEntity user) {
        unreadUsers.remove(user);
    }

    public List<UserEntity> getNotifiedUsers() {
        return notifiedUsers;
    }



    public String getFormattedTimeSinceCreated() {
        return TimeFormatterService.getFormattedTimeSinceCreation(dateCreated);
    }
}
