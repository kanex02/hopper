package nz.ac.canterbury.seng302.tab.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.entity.club.Club;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class of a Sport with a name and id
 */
@Table(name = "sport")
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Sport {

    /**
     * Unique key of the Sport, randomly generated and unique
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "sport_id"
    )
    private Long id;

    /**
     * Name of the sport
     */
    @Column(
            name = "sport_name"
    )
    @NotBlank(message = "Sport name is mandatory")
    @Pattern(regexp = "^(\\p{L}+[ \\p{L}]*)$", message = "Sport must start with a letter and after only contain letters and spaces")
    private String name;

    /**
     * Users associated with this sport
     */
    @ManyToMany()
    @JoinTable(
            name = "user_sports",
            joinColumns = @JoinColumn(name = "sport_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> associatedUsers = new HashSet<>();
    
    @OneToMany(mappedBy = "associatedSport", cascade = CascadeType.ALL)
    private Set<Club> clubs = new HashSet<>();

    /**
     * Adds an associated user when the user adds the sport as one of their favourites
     * @param associatedUser user that added the sport as a favourite
     */
    public void addAssociatedUser(UserEntity associatedUser) {
        this.associatedUsers.add(associatedUser);
    }

    /**
     * Removes an associated user when the user removes the sport as one of their favourites
     * @param associatedUser user that removed the sport as a favourite
     */
    public void removeAssociatedUser(UserEntity associatedUser) {
        this.associatedUsers.remove(associatedUser);
    }

    /**
     * Gets the users that are associated with this sport and have this sport as a favourite sport
     * @return list of associated users
     */
    public Set<UserEntity> getAssociatedUsers() {
        return associatedUsers;
    }

    /**
     * Creates a new Sport object
     */
    public Sport() {}

    /**
     * Creates a new Sport object
     * @param name name of Sport
     */
    public Sport(String name) {
        this.name = name;
    }

    /**
     * @return unique id of the sport
     */
    public Long getId() {
        return id;
    }

    /**
     * @return name of the sport
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name o the sport
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
