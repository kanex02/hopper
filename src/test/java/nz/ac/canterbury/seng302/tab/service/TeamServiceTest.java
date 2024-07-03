package nz.ac.canterbury.seng302.tab.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private Team team;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSortedUsers() {
        String password = "password";
        String dateOfBirth = "2000-01-01";
        Set<Sport> favouriteSports = Collections.emptySet();
        Location location = new Location(); // Or provide proper location details

        UserEntity manager = new UserEntity(password, "Alice", "Johnson", "alice@example.com", dateOfBirth, favouriteSports, location);
        UserEntity coach = new UserEntity(password, "Bob", "Smith", "bob@example.com", dateOfBirth, favouriteSports, location);
        UserEntity member = new UserEntity(password, "Charlie", "Adams", "charlie@example.com", dateOfBirth, favouriteSports, location);

        when(team.getUsers()).thenReturn(Set.of(manager, coach, member));
        when(team.getUserRole(manager)).thenReturn("manager");
        when(team.getUserRole(coach)).thenReturn("coach");
        when(team.getUserRole(member)).thenReturn("member");

        List<UserEntity> sortedUsers = teamService.getSortedUsers(team);

        assertEquals(Arrays.asList(manager, coach, member), sortedUsers);
    }
}
