package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Team repository accessor using Spring's @link{CrudRepository}.
 */
public interface TeamRepository extends CrudRepository<Team, Long> {

    /**
     * Finds a team by its id.
     * @param id
     * @return
     */
    Optional<Team> findById(long id);

    /**
     * Finds all teams in the database.
     * @return
     */
    List<Team> findAll();

    void deleteById(long id);

    /**
     * Searches for teams where their teamName or location is LIKE the given string.
     * Also checks teamName and location concatenated together both ways.
     * Then order by location then teamName alphabetically.
     * @param teamInput
     * @return
     */
    @Query("SELECT t FROM Team t " +
            "LEFT JOIN FETCH t.club c " +
            "WHERE LOWER(t.teamName) LIKE CONCAT('%', LOWER(:teamInput), '%')" +
            "OR (c.name IS NOT NULL AND LOWER(c.name) LIKE CONCAT('%', LOWER(:teamInput), '%'))" +
            "OR LOWER(t.location.city) LIKE CONCAT('%', LOWER(:teamInput), '%')" +
            "OR LOWER(t.location.country) LIKE CONCAT('%', LOWER(:teamInput), '%')" +
            "OR CONCAT(LOWER(t.location.city), ' ', LOWER(t.teamName)) LIKE CONCAT('%', LOWER(:teamInput), '%')" +
            "ORDER BY t.location.city, t.location.country, t.teamName ASC")
    List<Team> findTeamByString(
            @Param("teamInput") String teamInput
    );



    
    /**
     * Retrieves a team object by its associated token using a simple select where query
     * @param token the token associated with the team
     * @return the team object with the specified token, or null if no team is found
     */
    @Query("SELECT t FROM Team t WHERE t.joinToken = :token")
    Team findByToken(@Param("token") String token);
    
    /**
     * Retrieves all invitation tokens from the Team table
     * @return all the tokens from the Team table
     */
    @Query("select joinToken from Team")
    List<String> getAllTokens();

    default List<Team> filterTeamsBySport(
            List<Team> teams,
            Set<String> sports
    ) {
        if (sports.isEmpty()) {
            return this.findTeamByString("");
        }
        return teams.stream()
                .filter(team -> sports.contains(team.getSport().getName()))
                .toList();
    }

}
