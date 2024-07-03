package nz.ac.canterbury.seng302.tab.repository.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
class ChallengeRepositoryTest {


    private static final long USER_ID = 100L;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChallengeRepository challengeRepository;

    UserEntity user;

    void setup() {
        user = userRepository.getUserById(USER_ID);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(USER_ID, user.getId());
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_same_user.sql"})
    void givenAllChallengesBelongToUser_onGetForUser_returnsAllChallenges() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> allChallenges = challengeRepository.findAll();

        List<Challenge> challengesForUser = challengeRepository.findChallengesForUser(user);

        Assertions.assertEquals(allChallenges, challengesForUser);
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_different_users.sql"})
    void givenNoChallengesBelongToUser_onGetForUser_returnsEmptyList() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> allChallenges = challengeRepository.findAll();
        List<Challenge> challengesForUser = challengeRepository.findChallengesForUser(user);

        Assertions.assertFalse(allChallenges.isEmpty());
        Assertions.assertTrue(challengesForUser.isEmpty());
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_same_and_different_user.sql"})
    void givenSomeChallengesBelongToUser_onGetForUser_returnsOnlyChallengesBelongingToUser() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> allChallenges = challengeRepository.findAll();
        List<Challenge> challengesForUser = challengeRepository.findChallengesForUser(user);

        Assertions.assertFalse(allChallenges.isEmpty());
        Assertions.assertFalse(challengesForUser.isEmpty());

        Assertions.assertTrue(allChallenges.containsAll(challengesForUser));
        Assertions.assertNotEquals(allChallenges, challengesForUser);
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_same_user.sql"})
    void givenAllChallengesAreNotCompleted_onGetUncompletedForUser_returnsAllChallenges() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> allChallenges = challengeRepository.findAll();
        List<Challenge> challengesForUser = challengeRepository.findChallengeByUserGeneratedForAndIsComplete(user, false);

        Assertions.assertEquals(allChallenges, challengesForUser);
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_same_user.sql"})
    void givenAllChallengesAreNotCompleted_onGetCompletedForUser_returnsNoChallenges() {
        setup(); // hack to get around @BeforeEach executing before @Sql
        List<Challenge> challengesForUser = challengeRepository.findChallengeByUserGeneratedForAndIsComplete(user, true);
        Assertions.assertTrue(challengesForUser.isEmpty());
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_some_completed.sql"})
    void givenSomeChallengesAreCompleted_onGetUncompletedForUser_returnsOnlyUncompletedChallenges() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> challengesForUser = challengeRepository
                .findChallengeByUserGeneratedForAndIsComplete(user, false);

        Assertions.assertFalse(challengesForUser.isEmpty());
        Assertions.assertTrue(challengesForUser.stream().noneMatch(Challenge::isComplete));
    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_some_completed.sql"})
    void givenSomeChallengesAreCompleted_onGetCompletedForUser_returnsOnlyCompletedChallenges() {
        setup(); // hack to get around @BeforeEach executing before @Sql

        List<Challenge> challengesForUser = challengeRepository
                .findChallengeByUserGeneratedForAndIsComplete(user, true);

        Assertions.assertFalse(challengesForUser.isEmpty());
        Assertions.assertTrue(challengesForUser.stream().allMatch(Challenge::isComplete));
    }
}