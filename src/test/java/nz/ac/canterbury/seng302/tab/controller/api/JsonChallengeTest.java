package nz.ac.canterbury.seng302.tab.controller.api;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

class JsonChallengeTest {

    @Test
    void givenChallengeWithAllFields_whenParseToJsonChallenge_AllCopied() throws NoSuchFieldException, IllegalAccessException {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFirstName("Ben");
        user1.setLastName("Benson");
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFirstName("Peter");
        user2.setLastName("Peterson");
        UserEntity user3 = new UserEntity();
        user3.setId(3L);
        user3.setFirstName("Jack");
        user3.setLastName("Jackson");

        Challenge challenge = new Challenge(user1, LocalDateTime.now().plusDays(1L), "title", "goal", 10);
        Field field = Challenge.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(challenge, 1L);

        challenge.inviteUsers(List.of(user2, user3));

        JsonChallenge jsonChallenge = new JsonChallenge(challenge);

        Assertions.assertEquals(challenge.getId(), jsonChallenge.getId());
        Assertions.assertEquals(challenge.getTitle(), jsonChallenge.getTitle());
        Assertions.assertEquals(challenge.getGoal(), jsonChallenge.getGoal());
        Assertions.assertEquals(challenge.getHops(), jsonChallenge.getHops());
        Assertions.assertEquals(challenge.getUserGeneratedFor().getId(), jsonChallenge.getUserGeneratedFor().getId());
        Assertions.assertEquals(challenge.getEndDate(), jsonChallenge.getEndDate());
        Assertions.assertArrayEquals(challenge.getInvitedUsers().stream().map(UserEntity::getId).toArray(),
                jsonChallenge.getInvitedUsers().stream().map(JsonUser::getId).toArray());
    }

    @Test
    void challengeWithNoIdAndNoInvitedUsers_whenParsed_FieldsSetToDefaultValue() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFirstName("Ben");
        user1.setLastName("Benson");

        Challenge challenge = new Challenge(user1, LocalDateTime.now().plusDays(1L), "title", "goal", 10);

        JsonChallenge jsonChallenge = new JsonChallenge(challenge);

        Assertions.assertEquals(-1L, jsonChallenge.getId());
        Assertions.assertEquals(challenge.getTitle(), jsonChallenge.getTitle());
        Assertions.assertEquals(challenge.getGoal(), jsonChallenge.getGoal());
        Assertions.assertEquals(challenge.getHops(), jsonChallenge.getHops());
        Assertions.assertEquals(challenge.getUserGeneratedFor().getId(), jsonChallenge.getUserGeneratedFor().getId());
        Assertions.assertEquals(challenge.getEndDate(), jsonChallenge.getEndDate());
        Assertions.assertEquals(List.of(), jsonChallenge.getInvitedUsers());

    }

}