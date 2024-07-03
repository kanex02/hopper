package nz.ac.canterbury.seng302.tab.service.club;


import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClubFollowingTest {

    @Autowired
    private ClubRepository clubRepository;

    ClubService clubService = new ClubService();


    UserEntity user = new UserEntity();

    Club club = new Club("Test Club", "Test Description");

    @BeforeEach
    void setup() {
        clubService.setClubRepository(clubRepository);
    }

    @Test
    void noClub_followOrUnfollowClub_nothingHappens() {
        clubService.followOrUnfollowClub(null, user);
        Assertions.assertTrue(user.getFollowingTeams().isEmpty());
    }

    @Test
    void noUser_followOrUnfollowClub_nothingHappens() {
        clubService.followOrUnfollowClub(club, null);
        Assertions.assertTrue(club.getClubFollowers().isEmpty());
    }

    @Test
    void userAlreadyFollowingClub_unfollowClub_followerRemoved() {
        club.getClubFollowers().add(user);
        user.followClub(club);

        clubService.followOrUnfollowClub(club, user);
        Assertions.assertFalse(club.getClubFollowers().contains(user));
        Assertions.assertFalse(user.getFollowingClubs().contains(club));
    }

    @Test
    void userNotFollowingClub_followClub_followerAdded() {
        clubService.followOrUnfollowClub(club, user);
        Assertions.assertTrue(club.getClubFollowers().contains(user));
        Assertions.assertTrue(user.getFollowingClubs().contains(club));
    }

    @Test
    void clubWithMultipleFollowers_followClub_followerAdded() {
        UserEntity user2 = new UserEntity();

        club.getClubFollowers().add(user);
        user.followClub(club);

        clubService.followOrUnfollowClub(club, user2);

        Assertions.assertTrue(club.getClubFollowers().contains(user));
        Assertions.assertTrue(club.getClubFollowers().contains(user2));
    }

    @Test
    void userWithMultipleFollowingClubs_followClub_followerAdded() {
        Club club2 = new Club("new title", "new description");

        user.followClub(club);
        club.getClubFollowers().add(user);

        clubService.followOrUnfollowClub(club2, user);

        Assertions.assertTrue(user.getFollowingClubs().contains(club));
        Assertions.assertTrue(user.getFollowingClubs().contains(club2));
    }
}
