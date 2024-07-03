package nz.ac.canterbury.seng302.tab.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.authentication.InsecurePasswordException;
import nz.ac.canterbury.seng302.tab.controller.EditProfileController;
import nz.ac.canterbury.seng302.tab.controller.api.JsonUser;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;
import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;
import nz.ac.canterbury.seng302.tab.validation.MaximumAge;
import nz.ac.canterbury.seng302.tab.validation.MinimumAge;
import org.hibernate.validator.constraints.Length;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines a user with a first name and last name
 * <p>
 * This is an {@link Entity} in the Database
 */
@Entity
@Table(name = "tab_user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserEntity {

    private static final int INITIAL_HOPS_NEEDED = 70;
    private static final int ADDITIONAL_HOPS_NEEDED_PER_LEVEL = 20;
    private static final int MAX_LEVEL = 19;
    @ManyToMany(mappedBy = "managers")
    private final List<Team> managedTeams = new ArrayList<>();
    @ManyToMany(mappedBy = "coaches")
    private final List<Team> coachedTeams = new ArrayList<>();
    @ManyToMany(mappedBy = "members")
    private final List<Team> associatedTeams = new ArrayList<>();
    /**
     * Unique key of the user, randomly generated and unique
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "user_id")
    private Long id;
    /**
     * Hash of the user's password, encoded using a {@link PasswordEncoder}
     */
    @Column(
            name = "password",
            nullable = false
    )
    @Length(max = 255)
    @NotBlank(groups = EditProfileController.class,
            message = "Please enter a password")
    private String password;
    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;
    @Column(
            name = "first_name",
            nullable = false
    )
    @Pattern(
            regexp = "^(?=.{3,255}$)[\\p{L}\\p{M}']+([-\\s][\\p{L}\\p{M}']+)*$",
            message = "Invalid first name"
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false
    )
    @Pattern(
            regexp = "^(?=.{3,255}$)[\\p{L}\\p{M}']+([-\\s][\\p{L}\\p{M}']+)*$",
            message = "Invalid last name"
    )
    private String lastName;
    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    @Email(
            regexp = "^[\\p{L}\\p{N}.!#$%&'*+/=?^_`{|}~-]+@[\\p{L}\\p{N}]{1,62}(\\.[\\p{L}\\p{N}]{1,62})+$",
            message = "Invalid email address"
    )
    @NotBlank(message = "Please enter your email address")
    private String email;
    @Column
    private boolean isPasswordSecure = false;
    /**
     * The birthdate of the user. Stored in the database as just the date, and not the time.
     */
    @Column(
            name = "date_of_birth",
            nullable = false
    )
    @Temporal(TemporalType.DATE)
    @MinimumAge()
    @MaximumAge()
    private String dateOfBirth;
    @Column(
            name = "profile_picture"
    )
    @Length(max = 255, message = "Your the name of your profile picture is too long!")
    private String profilePicture;
    @ManyToMany(mappedBy = "associatedUsers")
    private Set<Sport> favouriteSports = new HashSet<>();
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @Valid
    private Location location;
    /**
     * List of lineups the user is a part of.
     * This is a One-To-Many relationship between UserEntity and Lineup.
     */
    @OneToMany(mappedBy = "user")
    private List<Lineup> lineups;
    @OneToMany(mappedBy = "creator")
    private List<Club> clubsCreated;
    @OneToMany(mappedBy = "author")
    private List<BlogPost> blogPosts = new ArrayList<>();
    /**
     * The total hops earned by the user.
     */
    @Column(name = "total_hops")
    private Long totalHops = 0L;

    @ManyToOne
    private Cosmetic border;

    @Column
    private Integer level = 1;

    @ManyToMany
    @OrderColumn(name = "display_order")
    private List<Cosmetic> badges;

    @ManyToMany(mappedBy = "notifiedUsers")
    @OrderBy("dateCreated DESC") // Order by date created, newest first
    private List<Notification> notifications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teamFollowers")
    private List<Team> followingTeams = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "follower_mapping",
            joinColumns = @JoinColumn(name = "followed_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<UserEntity> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    private List<UserEntity> following = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "clubFollowers")
    private List<Club> followingClubs = new ArrayList<>();

    /**
     * Constructs a new UserEntity object with a plaintext password.
     * <p>
     * IMPORTANT: Before saving the user to the database, hash their password with {@link UserEntity#hashPassword(PasswordEncoder)}
     *
     * @param password        The user's password in PLAINTEXT
     * @param firstName       The user's first name
     * @param lastName        The user's last name
     * @param email           The email address of the user
     * @param dateOfBirth     The user's date of birth
     * @param favouriteSports The user's favourite sports
     */
    public UserEntity(
            String password,
            String firstName,
            String lastName,
            String email,
            String dateOfBirth,
            Set<Sport> favouriteSports,
            Location location
    ) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.favouriteSports = favouriteSports;
        this.location = location;
    }

    /**
     * JPA required no-args constructor
     */
    public UserEntity() {

    }

    public void confirmEmail() {
        this.grantAuthority("ROLE_EMAIL_VERIFIED");
    }

    public boolean isEmailConfirmed() {
        return this.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMAIL_VERIFIED"));
    }


    /**
     * Hashes the user's password. Note that this MUST be called before adding the user to the database.
     * If not, an {@link InsecurePasswordException} will be thrown when an attempt is made to add the user to the
     * database.
     *
     * @param encoder The {@link PasswordEncoder} to hash the password with.
     */
    public void hashPassword(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
        this.isPasswordSecure = true;
    }

    /**
     * Life cycle event callback to ensure that the password is secure before trying to add this user to the database.
     *
     * @throws InsecurePasswordException Thrown if the password has not been hashed.
     * @see UserEntity#isPasswordSecure()
     */
    @PrePersist
    public void verifyPasswordIsHashed() throws InsecurePasswordException {
        if (!this.isPasswordSecure()) {
            throw new InsecurePasswordException("Password is not hashed, this user cannot be saved to persistence!");
        }
    }

    /**
     * Grants a role to the user, by way of an {@link Authority}
     *
     * @param authority The role to give to the user
     */
    public void grantAuthority(String authority) {
        if (this.userRoles == null) {
            this.userRoles = new ArrayList<>();
        }
        this.userRoles.add(new Authority(authority));
    }

    /**
     * @return Returns the {@link Authority}s of the user
     */
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }

    /**
     * @return Returns the hashed password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the hashed user's password from persistence.
     * <p>
     * This password is assumed already be in a secure format, and will not be hashed further.
     *
     * @param password The new password hash to set. THIS SHOULD NOT BE PLAINTEXT!
     */
    public void setPassword(String password) {
        this.password = password;
        this.isPasswordSecure = true;
    }

    /**
     * Essentially a hack that mirrors {@link UserEntity#getPassword()} to make Spring/Thymeleaf work properly
     *
     * @return Returns the hashed password of the user
     */
    public String getConfirmPassword() {
        return this.getPassword();
    }

    /**
     * @return Returns the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     *
     * @param firstName new name to change to
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Long> getSportIds() {
        return favouriteSports.stream()
                .sorted(Comparator.comparing(Sport::getName))
                .map(Sport::getId)
                .toList();
    }

    /**
     * @return Returns the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     *
     * @param lastName new name to change to
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the full name of the user in the format {@code "{firstname} {lastname}"}
     *
     * @return Returns the full name of the user
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * @return Returns the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email
     *
     * @param email new email to change to
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns a {@link LocalDate} containing the user's Date of Birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the user's date of birth
     *
     * @param dateOfBirth new date of birth to change to
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return Returns the unique ID of the user
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Lineup> getLineups() {
        return lineups;
    }

    /**
     * Gets the profile picture of the user. The profile picture is just the file name - not a path. For example
     * "image.png" not "/user-profiles/1/image.png".
     *
     * @return Returns the filename of the user profile picture
     * @see #getProfilePicturePath()
     */
    public String getProfilePicture() {
        return this.profilePicture;
    }

    /**
     * Sets the profile picture of the user. The profile picture is just the file name - not a path. For example
     * "image.png" not "user-profiles/1/image.png".
     *
     * @param profilePicture filename of the uploaded profile picture
     * @see #getProfilePicturePath()
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * @return Returns a fully qualified path to the user's {@link #profilePicture} on the system. If the current
     * profile picture is null, this will also return null.
     */
    public String getProfilePicturePath() {
        if (this.profilePicture == null) {
            return null;
        }

        // Commented out original implementation as it did not work on create activity page
        // not sure if this would work on staging and production servers or not though

        return MediaConfig.getInstance().getFullPath(
                String.format("/user-profiles/%d/%s", this.id, this.profilePicture)
        ).toString();
    }

    /**
     * Gets the favourite sports of the user
     */
    public Set<Sport> getFavouriteSports() {
        return favouriteSports;
    }

    public void setFavouriteSports(Set<Sport> favouriteSports) {
        this.favouriteSports = favouriteSports;
    }

    /**
     * Gets a user's favourite sports as list of strings
     *
     * @return list of sport name strings, sorted alphabetically
     */
    public List<String> getFavouriteSportNamesList() {
        List<String> sportNames = new ArrayList<>();
        for (Sport sport : this.favouriteSports) {
            sportNames.add(sport.getName());
        }
        return sportNames.stream().sorted().toList();
    }

    /**
     * Gets a user's favourite sports in string form
     *
     * @return string of comma-separated sport names, sorted alphabetically
     */
    public String getFavouriteSportNamesString() {
        String sportsString = getFavouriteSportNamesList().toString();
        // Strip off the leading "[" and trailing "]"
        return sportsString.substring(1, sportsString.length() - 1);
    }

    /**
     * Adds a favourite sport to the user's list of favourite sports
     *
     * @param favouriteSport sport to be added
     */
    public void addFavouriteSport(Sport favouriteSport) {
        this.favouriteSports.add(favouriteSport);
    }

    /**
     * Removes a favourite sport from the user's list of favourite sports
     *
     * @param favouriteSport sport to be removed
     */
    public void removeFavouriteSport(Sport favouriteSport) {
        this.favouriteSports.remove(favouriteSport);
    }

    /**
     * Checks that the password is secure. That is, the password has been hashed.
     *
     * @return Returns true if the password has been hashed.
     */
    public boolean isPasswordSecure() {
        return this.isPasswordSecure;
    }

    /**
     * Updates the password security status of the user (that is, whether their password has been hashed)
     *
     * @param passwordSecure The new password security status of the user
     * @see UserEntity#isPasswordSecure()
     */
    public void setPasswordSecure(boolean passwordSecure) {
        this.isPasswordSecure = passwordSecure;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Authority> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Authority> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }

    /**
     * Basic equals method for UserEntity. May be woefully inadequate in some cases but we're two days out.
     *
     * @param o The object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;

        LoggerFactory.getLogger(UserEntity.class)
                .debug("This user: {}, that user: {}", this, that);
        boolean probablyInstantiatedWithDefaultConstructor =
                this.firstName == null && that.firstName == null
                        && this.lastName == null && that.lastName == null
                        && this.email == null && that.email == null
                        && this.dateOfBirth == null && that.dateOfBirth == null;

        // If above fields are null then probably both instantiated using the default constructor
        // and in this case must be treated as distinct objects (for the sake of existing testing practices...)
        if (probablyInstantiatedWithDefaultConstructor) {
            return false;
        }

        return Objects.equals(this.id, that.id)
                || (Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(email, that.email)
                && Objects.equals(dateOfBirth, that.dateOfBirth));
    }

    /**
     * Hashes the user based on its eagerly fetched parameters.
     *
     * @return Returns the hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, password, firstName, lastName, dateOfBirth);
    }

    public List<Team> getManagedTeams() {
        return managedTeams;
    }

    public List<Team> getCoachedTeams() {
        return coachedTeams;
    }

    public List<Team> getAssociatedTeams() {
        return associatedTeams;
    }

    public List<Team> getAllTeams() {
        List<Team> allTeams = new ArrayList<>();
        allTeams.addAll(managedTeams);
        allTeams.addAll(coachedTeams);
        allTeams.addAll(associatedTeams);
        return allTeams;
    }

    /**
     * Gets the user's border.
     *
     * @return the border.
     */
    public Cosmetic getBorder() {
        return border;
    }

    /**
     * Sets the border of the user.
     *
     * @param border the border.
     */
    public void setBorder(Cosmetic border) {
        this.border = border;
    }

    public void addBlogPost(BlogPost blogPost) {
        this.blogPosts.add(blogPost);
    }

    public List<BlogPost> getBlogPosts() {
        return this.blogPosts;
    }

    public void setBlogPosts(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    public Long getTotalHops() {
        return totalHops;
    }

    public void setTotalHops(long totalHops) {
        if (totalHops < 0) {
            this.totalHops = 0L;
            return;
        }
        this.totalHops = totalHops;
        setLevelFromHops();
    }

    /**
     * Adds the given hops to the user's total.
     *
     * @param hops The hops to add
     * @return the number of hops added
     */
    public int addHops(int hops) {
        if (hops < 0) {
            return 0;
        }
        this.totalHops += hops;
        setLevelFromHops();
        return hops;
    }

    public List<Club> getClubsCreated() {
        return this.clubsCreated;
    }

    /**
     * Gets the level of the user.
     *
     * @return the level.
     */

    public Integer getLevel() {
        return level;
    }

    /**
     * Sets the level of the user.
     *
     * @param level the level.
     */
    public void setLevel(Integer level) {
        if (level < 1) {
            level = 1;
        }
        if (level > MAX_LEVEL) {
            level = MAX_LEVEL;
        }
        this.level = level;
    }

    /**
     * Automatically sets the level of the user based on their total hops.
     * The relationship between totalHops and level is based on an arithmetic series,
     * which simplifies to a quadratic equation of the form:
     * <p>
     * a * level^2 + b * level + c = 0
     * <p>
     * Where:
     * a = ADDITIONAL_HOPS_NEEDED_PER_LEVEL / 2.0
     * b = 100 - ADDITIONAL_HOPS_NEEDED_PER_LEVEL / 2.0
     * c = -totalHops + INITIAL_HOPS_NEEDED
     */
    public void setLevelFromHops() {
        // Solving the quadratic equation to find the maximum level
        double a = ADDITIONAL_HOPS_NEEDED_PER_LEVEL / 2.0;
        double b = 100 - ADDITIONAL_HOPS_NEEDED_PER_LEVEL / 2.0;
        double c = -(double) totalHops + INITIAL_HOPS_NEEDED;

        // Calculate the discriminant
        double discriminant = Math.pow(b, 2) - 4 * a * c;

        // If discriminant is negative it means no real roots exist (shouldn't happen)
        if (discriminant < 0) {
            this.level = 1;
            return;
        }

        // Calculate the maximum possible level
        double maxPossibleLevel = Math.floor((-b + Math.sqrt(discriminant)) / (2 * a));

        // The formula will give us level - 1, so we need to add 1.
        maxPossibleLevel += 1;

        // Ensure the level is within valid bounds
        this.level = (int) Math.min(Math.max(maxPossibleLevel + 1, 1), MAX_LEVEL);
    }

    /**
     * Gets the total number of hops required for the next level.
     *
     * @return the total number of hops required for the next level.
     */
    public long getTotalHopsRequiredForNextLevel() {
        if (level == MAX_LEVEL) {
            return this.totalHops;
        }

        // Calculate the sum of an arithmetic series
        // Sum = n * (a_1 + a_n) / 2
        int n = level;  // Number of terms
        int a1 = INITIAL_HOPS_NEEDED;  // The first term
        int an = INITIAL_HOPS_NEEDED + (ADDITIONAL_HOPS_NEEDED_PER_LEVEL * (level - 1));  // The last term

        return (long) n * (a1 + an) / 2;
    }

    public List<Cosmetic> getBadges() {
        return badges;
    }

    public void setBadges(List<Cosmetic> badges) {
        this.badges = badges;
    }

    public void addBadge(Cosmetic badge) {
        this.badges.add(badge);
    }

    /**
     * Follows the given user.
     *
     * @param userToFollow The user to follow
     */
    public void follow(UserEntity userToFollow) {
        if (userToFollow == null) {
            return;
        }
        if (following.contains(userToFollow)) {
            return;
        }
        following.add(userToFollow);
        userToFollow.getFollowers().add(this);
    }

    /**
     * Unfollows the given user.
     *
     * @param userToUnfollow The user to unfollow
     */
    public void unfollow(UserEntity userToUnfollow) {
        if (userToUnfollow == null) {
            return;
        }
        if (!following.contains(userToUnfollow)) {
            return;
        }
        following.remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(this);
    }

    public List<UserEntity> getFollowers() {
        return followers;
    }

    public List<UserEntity> getFollowing() {
        return following;
    }

    /**
     * Gets the users that the user is following that are also following the user.
     *
     * @return List of mutual followers
     */
    public List<UserEntity> getMutualFollowers() {
        // EVEN THOUGH SONARLINT COMPLAINS, THIS MUST BE MUTABLE!
        return followers.stream().filter(following::contains).collect(Collectors.toList());
    }


    public List<Team> getFollowingTeams() {
        Set<Team> followingTeamsList = new HashSet<>();
        followingTeamsList.addAll(followingTeams);
        followingTeamsList.addAll(getAllTeams());
        return new ArrayList<>(followingTeamsList);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void addNotifications(Notification notification) {
        this.notifications.add(notification);
    }

    /**
     * Returns a list of the user's friends as a JsonUser list.
     * Another user is a 'friend' if they follow the user, and the user follows them.
     *
     * @return a list of the user's friends.
     */
    public List<JsonUser> getFriends() {
        return this.getMutualFollowers().stream().map(JsonUser::new).toList();
    }

    public List<Club> getFollowingClubs() {
        Set<Club> followingClubsList = new HashSet<>();
        followingClubsList.addAll(followingClubs);
        followingClubsList.addAll(getMemberOfClubs());
        return new ArrayList<>(followingClubsList);
    }

    public Set<Club> getMemberOfClubs() {
        return new HashSet<>(getAllTeams().stream().map(Team::getClub).filter(Objects::nonNull).toList());
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public int getInitialHopsNeeded() {
        return INITIAL_HOPS_NEEDED;
    }

    public int getAdditionalHopsNeededPerLevel() {
        return ADDITIONAL_HOPS_NEEDED_PER_LEVEL;
    }

    public void followClub(Club club) {
        followingClubs.add(club);
    }

    public void unfollowClub(Club club) {
        followingClubs.remove(club);
    }

    public void followTeam(Team team) {
        followingTeams.add(team);
    }

    public void unfollowTeam(Team team) {
        followingTeams.remove(team);
    }

}
