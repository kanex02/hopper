package nz.ac.canterbury.seng302.tab.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "time_created_seconds")
    private long timeCreated;

    @ManyToOne
    private UserEntity user;

    @Column(
            nullable = false,
            name = "type"
    )
    @Enumerated(EnumType.STRING)
    private TokenType type;

    public Token(UserEntity user, TokenType type) {
        this(user, Instant.now().getEpochSecond(), type);
    }

    public Token(UserEntity user, long timeCreated, TokenType type) {
        this.timeCreated = timeCreated;
        this.user = user;
        this.type = type;
    }

    public Token() {
        this(null, TokenType.REGISTRATION);
    }

    @PreRemove
    private void preRemove() {
        this.user = null;
    }

    public boolean isValid() {
        return !this.isExpired();
    }

    public boolean isExpired() {

        if (user.isEmailConfirmed() && type == TokenType.REGISTRATION) {
            return false;
        }

        long oldestTime = Instant.now()
                .minusSeconds(this.getLifeSpan())
                .getEpochSecond();

        return oldestTime >= this.timeCreated;
    }

    /**
     * Gets the lifespan of the token. That is, how long it should exist for before it should be
     * deleted.
     *
     * @return Gets the lifespan from the associated {@link #type}.
     */
    public long getLifeSpan() {
        return this.type.getExpirationSeconds();
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TokenType getType() {
        return type;
    }

}
