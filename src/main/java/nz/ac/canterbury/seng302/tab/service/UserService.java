package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class that provides operations related to the users in persistence.
 */
@Service
public class UserService implements PaginatedService<UserEntity>, UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Default no-args constructor
     */
    public UserService() {
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void delete(UserEntity user) {
        LOGGER.info("Deleting user");
        userRepository.delete(user);
    }

    /**
     * Searches users in persistence whose first name OR last name contain the search parameter, escaping SQL wildcards
     *
     * @param name The search parameter
     * @return Returns the matching list of users
     */
    public List<UserEntity> getUsersByName(String name) {
        return userRepository.findUserByName(name);
    }

    /**
     * Retrieves a user by email and password.
     *
     * @param email    the email address of the user to retrieve
     * @param password the password of the user to retrieve
     * @return the user with the specified email and password, or null if not found
     */
    public UserEntity getUserByEmailAndPassword(String email, String password) {
        return userRepository.getUserByEmailAndPassword(email, password);
    }

    /**
     * Retrieves the user with the given email address, ignoring the case
     *
     * @param email email address to search for
     * @return user with given email address, or null if no user is found
     */
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Find the user based on the given id
     *
     * @param id id of the user
     * @return user with the given id, or null if not found
     */
    public UserEntity getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    /**
     * Updates a user.
     *
     * @param user the UserEntity object to update
     * @return the updated user entity
     */
    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    /**
     * Loads a user by username (which is the email address).
     *
     * @param username the email address of the user to load
     * @return a UserDetails object representing the loaded user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.getUserByEmail(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
        }

        throw new UsernameNotFoundException(String.format("UserEntity '%s' not found!", username));
    }

    @Override
    public List<UserEntity> search(String query) {
        // Escape wildcards
        String escapedName = query.replace("%", "\\%").replace("_", "\\_");
        return this.getUsersByName(escapedName);
    }

    /**
     * Filters a list of users based on the provided sports array.
     * Returns a new list containing only the users whose sport matches any of the sports in the array.
     *
     * @param users A list of UserEntity objects to be filtered.
     * @param query A string of sport names to filter the users by.
     * @return A filtered list of users containing only the users whose sport matches any of the sports in the array.
     */
    @Override
    public List<UserEntity> filterSports(List<UserEntity> users, String query) {
        if (query != null) {
            Set<String> requestSet = new HashSet<>(Arrays.asList(query.split(",")));

            return users.stream().filter(userEntity -> {
                Set<String> sportSet = new HashSet<>(userEntity.getFavouriteSports().stream().map(Sport::getName).toList());
                sportSet.retainAll(requestSet);
                return !sportSet.isEmpty();
            }).toList();
        }
        return users;
    }

    /**
     * Filters a list of users based on the provided city query.
     *
     * @param users A list of UserEntity objects to be filtered.
     * @param query A string of city names to filter the users by.
     * @return A filtered list of users containing only the users whose city matches any of the city in the array.
     */
    @Override
    public List<UserEntity> filterCities(List<UserEntity> users, String query) {
        if (query != null) {
            return users.stream().filter(user -> {
                Location location = user.getLocation();
                if (location == null) {
                    return false;
                }
                return Arrays.asList(query.split(",")).contains(location.getCity());
            }).toList();
        }
        return users;
    }

    /**
     * Check that an entered raw password matches the stored hashed password
     *
     * @param userId          id of user to check
     * @param enteredPassword raw password to check
     * @return true or false
     */
    public boolean checkPasswordMatches(Long userId, String enteredPassword) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Compare the entered old password with the stored encoded password
            return passwordEncoder.matches(enteredPassword, user.getPassword());
        }
        return false;
    }

    /**
     * Validates and updates the password
     *
     * @param user             User to update
     * @param password         New password
     * @param repeatedPassword New password repeated
     */
    public Optional<String> updatePassword(UserEntity user, String password, String repeatedPassword) {
        LOGGER.info("Updating password");

        Optional<String> inputErrors = PasswordValidator.getInstance().isPasswordValid(
                user, password, repeatedPassword
        );

        if (inputErrors.isPresent()) {
            return inputErrors;
        }

        // Sets plain-text password
        user.setPassword(password);
        // Sets security flag false
        user.setPasswordSecure(false);
        // Hashes new passwprd
        user.hashPassword(this.passwordEncoder);
        // Saves changes
        this.updateUser(user);

        emailService.sendUpdatePasswordNotificationEmail(user);
        return inputErrors;
    }

    /**
     * Returns the currently logged in user
     *
     * @return currently logged in user
     */
    public UserEntity getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return getUserByEmail(email);
    }

    /**
     * Returns the number of users in the database
     *
     * @return number of users
     */
    public long getCount() {
        return userRepository.count();
    }

    /**
     * Returns whether loggedInUser is following selectedUser
     *
     * @param loggedInUser the user who is logged in
     * @param selectedUser the user who is selected
     * @return true if loggedInUser is following selectedUser, false otherwise
     */
    public boolean isUserFollowing(UserEntity loggedInUser, UserEntity selectedUser) {
        return loggedInUser.getFollowing().contains(selectedUser);
    }

    /**
     * Returns a list of users from a string representation of user IDs.
     * @param users String representation of user IDs
     * @return List of users
     */
    public List<UserEntity> getUsersFromUserIdArrayString(String users) {
        List<Long> userIds = ActivityService.convertStringToLongList(users);
        return userIds.stream().map(this::getUserById).filter(Objects::nonNull).toList();
    }

}
