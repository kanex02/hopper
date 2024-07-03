package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SportRepository extends CrudRepository<Sport, Long> {

    @Query("SELECT s FROM Sport s ORDER BY s.name")
    Set<Sport> findAll();

    /**
     * Finds a sport by its name.
     * @param name
     * @return
     */
    List<Sport> findByName(String name);

    Optional<Sport> findById(Long id);
}
