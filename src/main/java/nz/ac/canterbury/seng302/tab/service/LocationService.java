package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private LocationRepository locationRepository;

    public LocationService() {}

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Adds a team to persistence
     * @param location object to persist
     * @return the saved team object
     */
    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Gets all the saved locations
     * @return list of locations
     */
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Checks if a location exists in the database, if it does not it will be added
     * @param location location to check with the database
     * @return the location that was found or added
     */
    public Location checkLocationExists(Location location) {
        Location foundLocation = locationRepository.findByAllFields(
                location.getAddressLine1(),
                location.getAddressLine2(),
                location.getSuburb(),
                location.getCity(),
                location.getPostcode(),
                location.getCountry()
        );
        if (foundLocation == null) { foundLocation = addLocation(location); }
        return foundLocation;
    }

    /**
     * Gets all cities that have specific teams
     * @param teams the list of teams to search for
     * @return a list of city names
     */
    public List<String> getAllCityNamesWithTeams(List<Team> teams) {
        return this.getAllLocations()
                .stream()
                .filter(location -> teams.stream()
                        .anyMatch(team -> team.getLocation().equals(location)))
                .map(Location::getCity)
                .distinct()
                .toList();
    }
    
    /**
     * Gets all the saved location cities
     * @return list of city names
     */
    public List<String> getAllCityNames() {
        return this.getAllLocations()
                .stream()
                .map(Location::getCity)
                .distinct()
                .toList();
    }
}
