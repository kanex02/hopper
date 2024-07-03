package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.GeneralChallengeTemplateRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.QuantifiableChallengeFactoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChallengeRewardsTest {

    @Mock
    private ChallengeRepository mockChallengeRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private QuantifiableChallengeFactoryRepository quantifiableChallengeFactoryRepository;

    @Mock
    private GeneralChallengeTemplateRepository generalChallengeTemplateRepository;

    private ChallengeService challengeService;

    private ChallengeGeneratorService challengeGeneratorService;

    @Spy
    private UserEntity testUser;

    private Challenge testChallenge;

    @BeforeEach
    void setUp() {

        testChallenge = new Challenge(testUser, LocalDateTime.now(), "TestChallenge", "Break your pelvis swimming!", Challenge.HOPS_UPPER_BOUND);

        MockitoAnnotations.openMocks(this);
        Mockito.when(mockChallengeRepository.findChallengeById(1L)).thenReturn(testChallenge);
        Mockito.when(mockUserRepository.getUserById(1L)).thenReturn(testUser);

        challengeGeneratorService = new ChallengeGeneratorService(
                mockChallengeRepository,
                quantifiableChallengeFactoryRepository,
                generalChallengeTemplateRepository,
                new Random()
        );
        challengeService = new ChallengeService(mockChallengeRepository, mockUserRepository, challengeGeneratorService, null, null);
    }

    @Test
    void givenUserHasChallenge_WhenAwarded_UserAwardedChallengeHops() {

        Assertions.assertEquals(testUser, testChallenge.getUserGeneratedFor());
        long initialHops = testUser.getTotalHops();

        challengeService.awardChallengeHops(1L, 1L);

        Assertions.assertEquals(initialHops + testChallenge.getHops(), testUser.getTotalHops());
        Mockito.verify(testUser, Mockito.times(1)).addHops(Mockito.anyInt());
    }

    @Test
    void givenUserHasCompletedChallenge_WhenAwarded_UserNotAwardedChallengeHopsAndExceptionThrown() {

        testChallenge.complete();

        long initialHops = testUser.getTotalHops();
        Assertions.assertThrows(IllegalStateException.class, () -> challengeService.awardChallengeHops(1L, 1L));

        Assertions.assertEquals(initialHops, testUser.getTotalHops());
        Mockito.verify(testUser, Mockito.times(0)).addHops(Mockito.anyInt());
    }

    @Test
    void givenUserOrChallengeNotFound_WhenAwarded_UserNotAwardedChallengeHopsAndExceptionThrown() {

        long initialHops = testUser.getTotalHops();

        Assertions.assertThrows(NullPointerException.class, () -> challengeService.awardChallengeHops(0L, 0L));

        Assertions.assertEquals(initialHops, testUser.getTotalHops());
        Mockito.verify(testUser, Mockito.times(0)).addHops(Mockito.anyInt());
    }

    @Test
    void givenChallengeNotAvailable_WhenAwarded_UserNotAwardedChallengeHopsAndExceptionThrown() {

        long initialHops = testUser.getTotalHops();

        testChallenge = new Challenge(new UserEntity(), LocalDateTime.now(), "TestChallenge", "Break your pelvis swimming!", Challenge.HOPS_UPPER_BOUND);
        Mockito.when(mockChallengeRepository.findChallengeById(1L)).thenReturn(testChallenge);

        Assertions.assertThrows(IllegalStateException.class, () -> challengeService.awardChallengeHops(1L, 1L));

        Assertions.assertEquals(initialHops, testUser.getTotalHops());
        Mockito.verify(testUser, Mockito.times(0)).addHops(Mockito.anyInt());
    }

}
