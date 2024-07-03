package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SportService  {

    @Autowired
    private SportRepository sportRepository;

    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public Set<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Set<String> getAllSportsNames() {
        return this.getAllSports().stream().map(Sport::getName).collect(Collectors.toSet());
    }

    public Sport findById(Long id) {
        return sportRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Sport id does not exist: " + id));
    }

    /**
     * Adds a sport to persistence
     * @param sport object to persist
     * @return the saved team object
     */
    public Sport addSport(Sport sport) {
        return sportRepository.save(sport);
    }

    /**
     * Updates a user's set of favourite sports
     * @param selectedSportsIds set of sport id strings
     * @param user the user to associate with given sports
     */
    public void updateUserSports(Set<String> selectedSportsIds, UserEntity user) {
        //remove old associations
        Iterator<Sport> iterator = user.getFavouriteSports().iterator();
        while (iterator.hasNext()) {
            Sport sport = iterator.next();
            sport.removeAssociatedUser(user);
            iterator.remove();
            user.removeFavouriteSport(sport);
        }
        //create the new selected associations
        user.setFavouriteSports(new HashSet<>());
        for (String sportId : selectedSportsIds){
            Sport sportToAdd = findById(Long.parseLong(sportId));
            user.addFavouriteSport(sportToAdd);
            sportToAdd.addAssociatedUser(user);
        }
    }

    /**
     * Gets a sport by its name.
     * @param name name to search.
     * @return the sport with the given name or Optional#empty() if none found.
     */
    public Sport getSportByName(String name) {
        return sportRepository.findByName(name).stream().findFirst().orElseThrow();
    }
}
