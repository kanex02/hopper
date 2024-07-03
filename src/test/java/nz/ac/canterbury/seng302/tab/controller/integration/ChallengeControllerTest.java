package nz.ac.canterbury.seng302.tab.controller.integration;

import nz.ac.canterbury.seng302.tab.controller.challenge.ChallengeController;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import nz.ac.canterbury.seng302.tab.service.notification.EventEmitter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ChallengeControllerTest {

    private final ChallengeRepository challengeRepository;

    private final UserService userService;

    private final ChallengeController challengeController;

    private final UserEntity user1;
    private final UserEntity user2;
    private final UserEntity user3;
    private final UserEntity user4;

    private final Challenge challenge;

    private Challenge savedChallenge;

    private EventEmitter eventEmitter;

    ChallengeControllerTest() {
        user1 = new UserEntity();
        user1.setId(1L);
        user1.setFirstName("Ben");
        user1.setLastName("Benson");

        user2 = new UserEntity();
        user2.setId(2L);
        user2.setFirstName("Peter");
        user2.setLastName("Peterson");

        user3 = new UserEntity();
        user3.setId(3L);
        user3.setFirstName("Jack");
        user3.setLastName("Jackson");

        user4 = new UserEntity();
        user4.setId(4L);
        user4.setFirstName("Nick");
        user4.setLastName("Nixon");

        user1.follow(user2);
        user2.follow(user1);
        user1.follow(user3);
        user3.follow(user1);

        challengeRepository = Mockito.mock(ChallengeRepository.class);

        eventEmitter = Mockito.mock(EventEmitter.class);

        userService = Mockito.spy(UserService.class);

        ChallengeService challengeService = new ChallengeService(challengeRepository, null, null, userService, eventEmitter);

        challengeController = new ChallengeController(challengeService, userService);

        this.challenge = new Challenge(user1, LocalDateTime.now(), "title", "goal", 1);
    }

    @BeforeEach
    void setUp() {
        Mockito.doReturn(user1).when(userService).getLoggedInUser();
        Mockito.when(challengeRepository.findChallengeById(Mockito.anyLong())).thenReturn(challenge);
        Mockito.when(challengeRepository.findChallengeById(Mockito.isNull())).thenReturn(challenge);
        Mockito.doAnswer(invocation -> {
            savedChallenge = invocation.getArgument(0);
            return null;
        }).when(challengeRepository).save(Mockito.any());
        Mockito.doReturn(user2).when(userService).getUserById(2L);
        Mockito.doReturn(user3).when(userService).getUserById(3L);
        Mockito.doReturn(user4).when(userService).getUserById(4L);
    }

    @Test
    void givenAValidChallenge_whenPosting_returnRedirectPath() {
        Assertions.assertEquals("redirect:/", challengeController.shareChallenge("1",
                "[2,3]", "/", null));
        Assertions.assertEquals(Stream.of(user2, user3).collect(Collectors.toSet()), new HashSet<>(savedChallenge.getInvitedUsers()));
    }

    @Test
    void givenChallengeDoesNotExist_whenPosting_return404() {
        Mockito.when(challengeRepository.findChallengeById(Mockito.anyLong())).thenReturn(null);
        Assertions.assertEquals("error/404", challengeController.shareChallenge("1",
                "[2,3]", "/", null));
    }

    @Test
    void givenChallengeIsNotForLoggedInUser_whenPosting_return403() {
        Mockito.doReturn(user2).when(userService).getLoggedInUser();
        Assertions.assertEquals("error/403", challengeController.shareChallenge("1",
                "[1,3]", "/", null));
    }

    @Test
    void givenInvalidChallengeId_whenPosting_return400() {
        Assertions.assertEquals("error/400", challengeController.shareChallenge("NotANumber",
                "[1,3]", "/", null));
    }

    @Test
    void givenInvalidListOfUsers_whenPosting_return400() {
        Assertions.assertEquals("error/400", challengeController.shareChallenge("1",
                "NotAList", "/", null));
    }

    @Test
    void givenEmptyListOfUsers_whenPosting_doesNotThrowErrorAndReturnRedirectPath() {
        Assertions.assertEquals("redirect:/", challengeController.shareChallenge("1",
                "[]", "/", null));
    }

    @Test
    void givenInvitedUserNotFriend_whenPosting_doesNotInviteUser() {
        Assertions.assertEquals("redirect:/", challengeController.shareChallenge("1",
                "[2,3,4]", "/", null));
        Assertions.assertEquals(Stream.of(user2, user3).collect(Collectors.toSet()),
                new HashSet<>(savedChallenge.getInvitedUsers()));
    }


}
