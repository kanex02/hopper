package nz.ac.canterbury.seng302.tab.service.leaderboard;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.pojo.TopHopperDTO;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for hops leaderboards.
 */
@Service
public class LeaderboardService {

    public static final LocalDateTime BEGINNING_OF_TIME = LocalDateTime.of(0, 1, 1, 0, 0);
    /**
     * Repository access for challenges
     */
    private final ChallengeRepository challengeRepository;


    /**
     * Spring constructor for leaderboard service - do not call this directly; autowire or constructor
     * inject an instance of this class instead.
     *
     * @param challengeRepository   The {@link #challengeRepository} for challenge persistence
     */
    @Autowired
    public LeaderboardService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Gets the top hoppers for the current week
     *
     * @return a list of the {@link #TopHopperDTO}s for the current week, ordered by total hops
     */
    public List<TopHopperDTO> getTopHoppersForLastWeek() {
        LocalDateTime lastWeekDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(7);
        return getTopHoppersForCutoffDate(lastWeekDate);
    }

    public TopHopperDTO getUserAsTopHopperForLastWeek(UserEntity user) {
        LocalDateTime lastWeekDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(7);
        Pageable pageable = PageRequest.of(0, 20);
        Page<TopHopperDTO> userHops = challengeRepository.findUserAsTopHopperSinceCutoffDate(pageable, lastWeekDate, user);
        if (userHops.stream().findFirst().isEmpty()) {
            return new TopHopperDTO(user, 0L, BEGINNING_OF_TIME);
        }
        else {
            return userHops.stream().findFirst().orElse(null);
        }

    }

    /**
     * Gets the top hoppers for the current month
     *
     * @return a list of the {@link #TopHopperDTO}s for the current month, ordered by total hops
     */
    public List<TopHopperDTO> getTopHoppersForLastMonth() {
        LocalDateTime lastMonthDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(30);
        return getTopHoppersForCutoffDate(lastMonthDate);
    }

    public TopHopperDTO getUserAsTopHopperForLastMonth(UserEntity user) {
        LocalDateTime lastWeekDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(30);
        Pageable pageable = PageRequest.of(0, 20);
        Page<TopHopperDTO> userHops = challengeRepository.findUserAsTopHopperSinceCutoffDate(pageable, lastWeekDate, user);
        if (userHops.stream().findFirst().isEmpty()) {
            return new TopHopperDTO(user, 0L, BEGINNING_OF_TIME);
        }
        else {
            return userHops.stream().findFirst().orElse(null);
        }
    }

    /**
     * Gets the top hoppers for all time
     *
     * @return a list of the {@link #TopHopperDTO}s for all time, ordered by total hops
     */
    public List<TopHopperDTO> getTopHoppersForAllTime() {
        return getTopHoppersForCutoffDate(BEGINNING_OF_TIME);
    }

    public TopHopperDTO getUserAsTopHopperForAllTime(UserEntity user) {
        Pageable pageable = PageRequest.of(0, 20);
        Page<TopHopperDTO> userHops = challengeRepository.findUserAsTopHopperSinceCutoffDate(pageable, BEGINNING_OF_TIME, user);
        if (userHops.stream().findFirst().isEmpty()) {
            return new TopHopperDTO(user, 0L, BEGINNING_OF_TIME);
        }
        else {
            return userHops.stream().findFirst().orElse(null);
        }
    }

    List<TopHopperDTO> getTopHoppersForCutoffDate(LocalDateTime cutoffDate) {

        if (cutoffDate == null) {
            cutoffDate = BEGINNING_OF_TIME;
        }

        Pageable pageable = PageRequest.of(0, 20);

        Page<TopHopperDTO> topHoppers = challengeRepository.findTopHoppersSinceCutoffDate(pageable, cutoffDate);

        List<TopHopperDTO> resultList = new ArrayList<>();
        for (TopHopperDTO dto: topHoppers) {
            resultList.add(dto);
        }

        return resultList;
    }


}
