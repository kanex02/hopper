package nz.ac.canterbury.seng302.tab.entity.user;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserFollowingTest {

    private UserEntity alice;
    private UserEntity bob;

    @BeforeEach
    public void setUp() {
        alice = new UserEntity();
        bob = new UserEntity();
    }

    @Test
    void validUsers_onFollow_bothUsersUpdated() {
        alice.follow(bob);

        List<UserEntity> aliceFollowing = alice.getFollowing();
        List<UserEntity> bobFollowers = bob.getFollowers();

        assertTrue(aliceFollowing.contains(bob));
        assertTrue(bobFollowers.contains(alice));
    }

    @Test
    void followingUser_onUnfollow_bothUsersUpdated() {
        alice.follow(bob);
        alice.unfollow(bob);

        List<UserEntity> charlieFollowers = bob.getFollowers();
        List<UserEntity> aliceFollowing = alice.getFollowing();

        assertFalse(charlieFollowers.contains(alice));
        assertFalse(aliceFollowing.contains(bob));
    }

    @Test
    void alreadyFollowing_onFollow_noAdditionalFollow() {
        alice.follow(bob);
        alice.follow(bob);

        List<UserEntity> aliceFollowing = alice.getFollowing();
        List<UserEntity> bobFollowers = bob.getFollowers();

        assertEquals(1, aliceFollowing.size());
        assertEquals(1, bobFollowers.size());
    }

    @Test
    void notFollowing_onUnfollow_noChange() {
        assertFalse(alice.getFollowing().contains(bob));
        alice.unfollow(bob);

        List<UserEntity> aliceFollowing = alice.getFollowing();

        assertEquals(0, aliceFollowing.size());
    }

    @Test
    void nullUser_onFollow_noChange() {
        alice.follow(null);

        List<UserEntity> aliceFollowing = alice.getFollowing();
        assertEquals(0, aliceFollowing.size());
    }

    @Test
    void nullUser_onUnfollow_noChange() {
        alice.follow(bob);  // First, make alice follow bob
        alice.unfollow(null);  // Then, try to make alice unfollow a null user

        List<UserEntity> aliceFollowing = alice.getFollowing();
        assertTrue(aliceFollowing.contains(bob));  // alice should still be following bob
        assertEquals(1, aliceFollowing.size());  // Only bob should be in the list
    }

}
