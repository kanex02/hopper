package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    static final long USER_ID = 1L;

    @Mock
    ChallengeGeneratorService mockGenerator;

    @Mock
    ChallengeRepository mockRepository;

    @InjectMocks
    ChallengeService challengeService;

    UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(USER_ID);
    }

    @Test
    void noChallenges_onGetAvailable_triggersGeneration() {
        when(mockRepository.findChallengesForUser(user))
                .thenReturn(List.of());

        List<Challenge> ignored = challengeService.getAllAvailableChallengesForUser(user);

        verify(mockGenerator, Mockito.atLeastOnce())
                .generateChallengesForDay(user);
    }

    @Test
    void allChallengesExpired_onGetAvailable_triggersGeneration() {
        Challenge challenge = new Challenge(
                user,
                LocalDateTime.now().minusWeeks(1L),
                "Title",
                "Goal",
                10
        );
        List<Challenge> challenges = List.of(
                challenge,
                challenge,
                challenge
        );

        Assertions.assertTrue(challenges.stream().allMatch(Challenge::isExpired));

        when(mockRepository.findChallengesForUser(user))
                .thenReturn(challenges);

        List<Challenge> ignored = challengeService.getAllAvailableChallengesForUser(user);

        verify(mockGenerator, Mockito.atLeastOnce())
                .generateChallengesForDay(user);
    }

    @Test
    void allChallengesCompleteAndExpired_onGetAvailable_triggersGeneration() {
        Challenge challenge = new Challenge(
                user,
                LocalDateTime.now().minusWeeks(1L),
                "Title",
                "Goal",
                10
        );
        challenge.complete();
        List<Challenge> challenges = List.of(
                challenge,
                challenge,
                challenge
        );

        Assertions.assertTrue(challenges.stream().allMatch(Challenge::isExpired));
        Assertions.assertTrue(challenges.stream().allMatch(Challenge::isComplete));

        when(mockRepository.findChallengesForUser(user))
                .thenReturn(challenges);

        List<Challenge> ignored = challengeService.getAllAvailableChallengesForUser(user);

        verify(mockGenerator, Mockito.atLeastOnce())
                .generateChallengesForDay(user);
    }

    @Test
    void allChallengesCompleteAndUnexpired_onGetAvailable_triggersNoGeneration() {
        Challenge challenge = new Challenge(
                user,
                LocalDateTime.now().plusDays(1L),
                "Title",
                "Goal",
                10
        );
        challenge.complete();
        List<Challenge> challenges = List.of(
                challenge,
                challenge,
                challenge
        );

        Assertions.assertTrue(challenges.stream().noneMatch(Challenge::isExpired));
        Assertions.assertTrue(challenges.stream().allMatch(Challenge::isComplete));

        when(mockRepository.findChallengesForUser(user))
                .thenReturn(challenges);

        List<Challenge> ignored = challengeService.getAllAvailableChallengesForUser(user);

        verify(mockGenerator, Mockito.never())
                .generateChallengesForDay(user);
    }

}
