package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository that stores all the registered users
 * Used to execute queries on users
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * Retrieves a user with the specified email and password.
     *
     * @param email    the email of the user to retrieve
     * @param password the password of the user to retrieve
     * @return the UserEntity object with the specified email and password, or null if not found
     */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password = :password")
    UserEntity getUserByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password
    );

    /**
     * Retrieves a user with the specified ID.
     *
     * @param id the ID of the user to retrieve
     * @return the UserEntity object with the specified ID, or null if not found
     */
    @Query("SELECT u FROM UserEntity u WHERE u.id = :id")
    UserEntity getUserById(
            @Param("id") Long id
    );

    /**
     * Searches users in persistence whose first name OR last name contain the search parameter
     *
     * @param name The search parameter
     * @return Returns the matching list of users
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.firstName) LIKE CONCAT('%', LOWER(:name), '%') " +
            "OR LOWER(u.lastName) LIKE CONCAT('%', LOWER(:name), '%')" +
            "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:name), '%')" +
            "ORDER BY u.firstName, u.lastName")
    List<UserEntity> findUserByName(
            @Param("name") String name
    );



    /**
     * Retrieves all users.
     *
     * @return a List of all UserEntity objects, or an empty List if there are no users
     */
    List<UserEntity> findAll();
    
    /**
     * Retrieves user with given email ignoring the case
     * @param email email of user to retrieve
     * @return the UserEntity object with given email, or null if not found
     */
    UserEntity findByEmailIgnoreCase(String email);

}