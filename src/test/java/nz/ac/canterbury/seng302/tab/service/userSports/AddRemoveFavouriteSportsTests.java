package nz.ac.canterbury.seng302.tab.service.userSports;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 * (Really just the class name for this one)
 */
@Deprecated
@DataJpaTest
class AddRemoveFavouriteSportsTests {

    @InjectMocks
    UserService userService = Mockito.spy(new UserService(Mockito.mock(UserRepository.class)));
    
    SportRepository mockSportRepo = Mockito.mock(SportRepository.class);
    
    @InjectMocks
    SportService sportService = Mockito.spy(new SportService(mockSportRepo));

    private static final String RUGBY_NAME = "Rugby";
    private static final String HOCKEY_NAME = "Hockey";

    UserEntity testUser;
    Set<Sport> allSports;


    @BeforeEach
    void setup() {

        testUser = new UserEntity(
                "password11",
                "Gabbro",
                "Hornfels",
                "gabbro@nomai.net",
                "1994-06-04",
                new HashSet<>(),
                null
        );

        Sport rugby = new Sport(RUGBY_NAME);
        rugby.setId(1L);
        Sport hockey = new Sport(HOCKEY_NAME);
        hockey.setId(2L);
        allSports = Set.of(rugby, hockey);
    
        Mockito.when(mockSportRepo.findById(1L)).thenReturn(Optional.of(rugby));
        Mockito.when(mockSportRepo.findById(2L)).thenReturn(Optional.of(hockey));
    
        Mockito.when(userService.getUserById(Mockito.any())).thenReturn(testUser);
        Mockito.when(sportService.getAllSports()).thenReturn(allSports);
    }

    @Test
    void whenAddingKnownSport_thenUserHasSport() {
        assertFalse(
                testUser.getFavouriteSports().stream()
                        .anyMatch(sport -> allSports.contains(sport))
        );

        Set<String> knownSports = sportService
                .getAllSports()
                .stream()
                .map(sport -> String.valueOf(sport.getId()))
                .collect(Collectors.toSet());

        sportService.updateUserSports(knownSports, testUser);

        assertTrue(testUser.getFavouriteSports().containsAll(allSports));
    }

    @Test
    void whenAddingKnownSport_thenSportIsAssociatedToUser() {
        assertFalse(
                testUser.getFavouriteSports().stream().anyMatch(sport -> allSports.contains(sport))
        );

        Set<String> knownSports = sportService
                .getAllSports()
                .stream()
                .map(sport -> String.valueOf(sport.getId()))
                .collect(Collectors.toSet());

        sportService.updateUserSports(knownSports, testUser);

        assertTrue(allSports.stream().allMatch(sport -> sport.getAssociatedUsers().contains(testUser)));
    }

    @Test
    void userHasSport_removeSport_userNoLongerHasSport() {
        Set<String> knownSports = sportService
                .getAllSports()
                .stream()
                .map(sport -> String.valueOf(sport.getId()))
                .collect(Collectors.toSet());

        sportService.updateUserSports(knownSports, testUser);

        assertTrue(testUser.getFavouriteSports().containsAll(allSports));

        sportService.updateUserSports(new HashSet<>(), testUser);

        assertFalse(
                testUser.getFavouriteSports().stream()
                        .anyMatch(sport -> allSports.contains(sport))
        );
    }

    @Test
    void userHasSport_removeSport_sportNoLongerHasAssociatedUser() {
        Set<String> knownSports = sportService
                .getAllSports()
                .stream()
                .map(sport -> String.valueOf(sport.getId()))
                .collect(Collectors.toSet());

        sportService.updateUserSports(knownSports, testUser);

        assertTrue(allSports.stream().allMatch(sport -> sport.getAssociatedUsers().contains(testUser)));

        sportService.updateUserSports(new HashSet<>(), testUser);

        assertFalse(
                testUser.getFavouriteSports().stream().anyMatch(sport -> allSports.contains(sport))
        );
    }
}
