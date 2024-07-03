package nz.ac.canterbury.seng302.tab.entity.challenge;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantifiableChallengeFactoryTest {

    private QuantifiableChallengeFactory factory;
    private UserEntity userEntity;
    private LocalDateTime endDate;
    private Sport sport;
    private Set<Sport> favouriteSports;

    @BeforeEach
    void setUp() {
        sport = new Sport("Running");
        favouriteSports = new HashSet<>();
        userEntity = Mockito.mock(UserEntity.class);
        Mockito.when(userEntity.getFavouriteSports()).thenReturn(favouriteSports);
        endDate = LocalDateTime.now().plusDays(7);

        factory = new QuantifiableChallengeFactory(1.5, "Run", "Run for %d kilometers", sport);
    }

    @Test
    void testCreateChallenge() {
        favouriteSports.add(sport);
        Challenge challenge = factory.create(10, userEntity, endDate);
        assertEquals("Run", challenge.getTitle());
        assertEquals("Run for 15 kilometers", challenge.getGoal());
    }

    @Test
    void testCannotCreateChallengeWhenSportNotFavourite() {
        assertFalse(factory.canCreate(10, userEntity));
    }

    @Test
    void testCanCreateChallengeWhenSportIsFavourite() {
        favouriteSports.add(sport);
        assertTrue(factory.canCreate(10, userEntity));
    }
}
