package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    private LocationService locationServiceSpy;
    private static final Location location1 = new Location("London", "Gambia");
    private static final Location location2 = new Location("Jakarta", "Zimbabwe");
    private static final Location location3 = new Location("Beijing", "Belgium");
    private static final ArrayList<Location> locations = new ArrayList<>(){{
        add(location1);
        add(location2);
        add(location3);
    }};

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        locationServiceSpy = Mockito.spy(new LocationService(locationRepository));
    }


    @Test
    void citiesWithNoTeams_getAllCities_emptyListReturned () {
        Mockito.doReturn(locations).when(locationServiceSpy).getAllLocations();
        assertEquals(0, locationServiceSpy.getAllCityNamesWithTeams(new ArrayList<>()).size());
    }

    @Test
    void citiesWithTeams_getAllCities_correctListReturned () {
        Mockito.doReturn(locations).when(locationServiceSpy).getAllLocations();
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(
                new Team("Team1") {{
                    setLocation(location1);
                }}
        );
        teams.add(
                new Team("Team2") {{
                    setLocation(location2);
                }}
        );
        List<String> response = locationServiceSpy.getAllCityNamesWithTeams(teams);
        assertTrue(response.contains("London"));
        assertTrue(response.contains("Jakarta"));
        assertFalse(response.contains("Beijing"));
    }

    @Test
    void validLocation_checkLocation_existingLocationChecked() {
        Location existingLocation = new Location("New York", "USA");
        Mockito.when(locationRepository.findByAllFields(
                existingLocation.getAddressLine1(),
                existingLocation.getAddressLine2(),
                existingLocation.getSuburb(),
                existingLocation.getCity(),
                existingLocation.getPostcode(),
                existingLocation.getCountry()
        )).thenReturn(existingLocation);

        Location result = locationServiceSpy.checkLocationExists(existingLocation);

        assertEquals(existingLocation, result);
        Mockito.verify(locationServiceSpy, Mockito.never()).addLocation(existingLocation);
    }

    @Test
    void invalidLocation_checkLocation_newLocationAdded() {
        Location newLocation = new Location("Sydney", "Australia");
        Mockito.when(locationRepository.findByAllFields(
                newLocation.getAddressLine1(),
                newLocation.getAddressLine2(),
                newLocation.getSuburb(),
                newLocation.getCity(),
                newLocation.getPostcode(),
                newLocation.getCountry()
        )).thenReturn(null);
        Mockito.when(locationServiceSpy.addLocation(newLocation)).thenReturn(newLocation);

        Location result = locationServiceSpy.checkLocationExists(newLocation);

        assertEquals(newLocation, result);
        Mockito.verify(locationServiceSpy).addLocation(newLocation);
    }


}