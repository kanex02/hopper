package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SharedChallengeRewardsTest {

    @Mock
    private ChallengeRepository mockChallengeRepository;

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Spy
    private UserEntity originalUser;
    @Spy
    private UserEntity invitedUser1;
    @Spy
    private UserEntity invitedUser2;
    @Spy
    private UserEntity invitedUser3;
    private List<UserEntity> invitedUsers;
    private Challenge testSharedChallenge;

    @BeforeEach
    void setUp() {

        testSharedChallenge = new Challenge(originalUser, LocalDateTime.now(), "TestChallenge", "Break your pelvis swimming!", Challenge.HOPS_UPPER_BOUND);
        Mockito.when(mockChallengeRepository.findChallengeById(Mockito.anyLong())).thenReturn(testSharedChallenge);

        invitedUsers = List.of(
                invitedUser1,
                invitedUser2,
                invitedUser3
        );

        testSharedChallenge.inviteUsers(invitedUsers);
    }

    @Test
    void givenChallengeNotSharedToUser_WhenAwardHops_ExceptionThrown() {
        UserEntity uninvitedUser = new UserEntity();
        Mockito.when(mockUserRepository.getUserById(4L)).thenReturn(uninvitedUser);

        Assertions.assertThrows(IllegalStateException.class, () -> challengeService.awardChallengeHops(1L, 4L));
    }

    @Test
    void givenInvitedUsersNoneCompleted_WhenOriginalUserCompletes_ChallengeMarkedCompleteForUserAndHopsNotAwarded() {

        Mockito.when(mockUserRepository.getUserById(1L)).thenReturn(originalUser);

        challengeService.awardChallengeHops(1L, 1L);

        Assertions.assertTrue(testSharedChallenge.isChallengeCompleteForUser(originalUser));
        Mockito.verify(originalUser, Mockito.never()).addHops(Mockito.anyInt());
    }

    @Test
    void givenAllButOneCompleted_WhenFinalUserCompletes_ChallengeMarkedCompleteAndHopsAwardedToAllAssociatedUsers() {

        Mockito.when(mockUserRepository.getUserById(1L)).thenReturn(originalUser);

        testSharedChallenge.completeForUser(invitedUser1);
        testSharedChallenge.completeForUser(invitedUser2);
        testSharedChallenge.completeForUser(invitedUser3);

        challengeService.awardChallengeHops(1L, 1L);

        Assertions.assertTrue(testSharedChallenge.isComplete());
        Mockito.verify(originalUser, Mockito.times(1)).addHops(testSharedChallenge.getHops());
        for (UserEntity user : invitedUsers) {
            Mockito.verify(user, Mockito.times(1)).addHops(testSharedChallenge.getHops());
        }
    }

    @Test
    void givenNewUserInvitedBeforeAllComplete_WhenInitialUsersComplete_ChallengeMarkedAsCompleteForInitialUsersAndHopsNotAwarded() {

        for (UserEntity user : testSharedChallenge.getInvitedUsers()) {
            testSharedChallenge.completeForUser(user);
        }

        UserEntity newInvitee = new UserEntity();
        testSharedChallenge.inviteUsers(List.of(newInvitee));

        Mockito.when(mockUserRepository.getUserById(1L)).thenReturn(originalUser);
        challengeService.awardChallengeHops(1L, 1L);

        List<UserEntity> initialUsers = List.of(originalUser, invitedUser1, invitedUser2, invitedUser3);
        for (UserEntity user : initialUsers) {
            Assertions.assertTrue(testSharedChallenge.isChallengeCompleteForUser(user));
            Mockito.verify(user, Mockito.never()).addHops(Mockito.anyInt());
        }
    }
}
