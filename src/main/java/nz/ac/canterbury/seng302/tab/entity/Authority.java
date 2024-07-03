package nz.ac.canterbury.seng302.tab.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

/**
 * An Authority is a role or permission that a {@link UserEntity} has, such as 'user' or 'admin'.
 * Each authority is owned by a single user, joined on the user id.
 * <p>
 * Code largely based on example provided by Morgan English.
 *
 * @author <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/-/tree/master/src/main/java/security/example">Morgan English</a>
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column()
    private String role;


    /**
     * Required JPA no-args constructor
     */
    protected Authority() {

    }

    /**
     * Constructs an authority with a string role
     *
     * @param role The role of the authority
     */
    public Authority(String role) {
        this.role = role;
    }

    /**
     * @return Returns the role of this authority
     */
    public String getRole() {
        return this.role;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }
}
