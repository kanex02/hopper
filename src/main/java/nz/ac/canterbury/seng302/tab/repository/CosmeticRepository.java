package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CosmeticRepository extends CrudRepository<Cosmetic, Long> {

    /**
     * Finds all cosmetics
     *
     * @return a list of all cosmetics
     */
    List<Cosmetic> findAll();

    /**
     * Finds a cosmetic by its id
     *
     * @param id the id of the cosmetic
     * @return the cosmetic with the given id
     */
    Cosmetic getById(Long id);
}
