package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.cosmetic.Level;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository class for {@link Level}s.
 */
public interface LevelRepository extends CrudRepository<Level, Long> {

    /**
     * Finds all levels
     * @return a list of all levels
     */
    List<Level> findAllByOrderByIdAsc();

    /**
     * Finds all level entities unlocked by a level number.
     * @param level the level number
     * @return all unlocked levels
     */
    List<Level> findLevelsByIdLessThanEqualOrderByIdAsc(Long level);
}
