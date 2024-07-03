package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location, Long> {

    Optional<Location> findById(long id);
    
    @Query("SELECT s FROM Location s ORDER BY s.city")
    List<Location> findAll();

    @Query("SELECT s FROM Location s WHERE s.addressLine1 = ?1 AND s.addressLine2 = ?2 AND s.suburb = ?3 AND s.city = ?4 AND s.postcode = ?5 AND s.country = ?6")
    Location findByAllFields(String addressLine1, String addressLine2, String suburb, String city, String postcode, String country);

}
