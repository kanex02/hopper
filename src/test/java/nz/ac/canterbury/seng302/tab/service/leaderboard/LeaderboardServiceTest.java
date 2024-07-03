package nz.ac.canterbury.seng302.tab.service.leaderboard;

import nz.ac.canterbury.seng302.tab.pojo.TopHopperDTO;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

@DataJpaTest
@Sql(scripts = {"/sql/challenge/challenges_some_completed_this_week.sql"})
class LeaderboardServiceTest {


    @Autowired
    ChallengeRepository challengeRepository;

    LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        leaderboardService = new LeaderboardService(challengeRepository);
    }

    @Test
    void givenSomeChallengesCompletedThisWeek_onGetTopHoppersForLastWeek_returnsTop20Hoppers() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForLastWeek();
        Assertions.assertEquals(20, topHopperDTOList.size());

    }

    @Test
    void givenSomeChallengesCompletedThisWeek_onGetTopHoppersForLastMonth_returnsTop20Hoppers() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForLastMonth();
        Assertions.assertEquals(20, topHopperDTOList.size());

    }


    @Test
    void givenSomeChallengesCompletedThisWeek_onGetTopHoppersForAllTime_returnsTop20Hoppers() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForAllTime();
        Assertions.assertEquals(20, topHopperDTOList.size());

    }

    @Test
    void givenSomeChallengesCompleted_onGetTopHoppers_returnsHoppersOrderedByHopsAndLastCompletedChallengeDescending() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForAllTime();
        List<TopHopperDTO> sortedTopHopperDTOList = topHopperDTOList.stream().sorted((hopper1, hopper2) -> {
            if (Objects.equals(hopper1.getTotalHops(), hopper2.getTotalHops())) {
                return hopper1.getLatestChallengeCompletion().compareTo(hopper2.getLatestChallengeCompletion());
            } else {
                return hopper2.getTotalHops().compareTo(hopper1.getTotalHops());
            }
        }).toList();

        Assertions.assertEquals(sortedTopHopperDTOList, topHopperDTOList);

    }

    @Test
    @Sql(scripts = {"/sql/challenge/challenges_all_with_different_users.sql"})
    void givenNoChallengesCompleted_onGetTopHoppers_returnsEmptyList() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForAllTime();
        Assertions.assertTrue(topHopperDTOList.isEmpty());

    }

    @Test
    void givenNullCutoffDate_whenGetTopHoppersForCutoffDate_returnsTopHoppersForAllTime() {

        List<TopHopperDTO> topHopperDTOList = leaderboardService.getTopHoppersForCutoffDate(null);
        Assertions.assertEquals(20, topHopperDTOList.size());
    }

}
