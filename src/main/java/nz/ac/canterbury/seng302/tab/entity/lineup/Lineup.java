package nz.ac.canterbury.seng302.tab.entity.lineup;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;

@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Lineup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Role of the user in the lineup. This could be 'STARTER' or 'SUB'.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineupRole role;

    /**
     * No-args JPA constructor
     */
    public Lineup() {}

    /**
     * Constructor for lineup
     * @param activity associated activity
     * @param user associated user
     */
    public Lineup(Activity activity, UserEntity user) {
        this.activity = activity;
        this.user = user;
    }
    
    /**
     * Set the role of a lineup
     *
     * @param role role of the lineup
     */
    public void setLineupRole(LineupRole role) {
        this.role = role;
    }

    public Long getId() {return this.id;}

    public Activity getActivity() {
        return activity;
    }

    public UserEntity getUser() {
        return user;
    }

    public LineupRole getRole() {
        return role;
    }
}
