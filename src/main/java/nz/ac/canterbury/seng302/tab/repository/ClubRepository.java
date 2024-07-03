package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.club.Club;
import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Long> {
    
    Club findById(long id);
    
    void deleteById(long id);
    
    Club findClubByName(String name);
}