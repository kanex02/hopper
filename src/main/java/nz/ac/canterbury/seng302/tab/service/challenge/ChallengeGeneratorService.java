package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.challenge.ChallengeFactory;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.GeneralChallengeTemplateRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.QuantifiableChallengeFactoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generator service class for {@link Challenge}s. Provides functionality for generating random challenges
 * for users each week.
 */
@Service
public class ChallengeGeneratorService {

    public static final int HIGH_HOPS_REWARD = 15;
    public static final int MEDIUM_HOPS_REWARD = 10;
    public static final int LOW_HOPS_REWARD = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeGeneratorService.class);
    private static final int[] DEFAULT_HOPS_DISTRIBUTION = new int[]{
            HIGH_HOPS_REWARD, HIGH_HOPS_REWARD,
            MEDIUM_HOPS_REWARD, MEDIUM_HOPS_REWARD, MEDIUM_HOPS_REWARD,
            LOW_HOPS_REWARD, LOW_HOPS_REWARD
    };

    private final ChallengeRepository challengeRepository;

    private final QuantifiableChallengeFactoryRepository quantifiableChallengeFactoryRepository;

    private final GeneralChallengeTemplateRepository generalChallengeTemplateRepository;

    private final Random random;

    public ChallengeGeneratorService(
            ChallengeRepository challengeRepository,
            QuantifiableChallengeFactoryRepository quantifiableChallengeFactoryRepository,
            GeneralChallengeTemplateRepository generalChallengeTemplateRepository,
            @Value("#{new java.util.Random()}") Random random
    ) {
        this.challengeRepository = challengeRepository;
        this.quantifiableChallengeFactoryRepository = quantifiableChallengeFactoryRepository;
        this.generalChallengeTemplateRepository = generalChallengeTemplateRepository;
        this.random = random;
    }

    /**
     * Generates a new set of challenges for the user for this week
     *
     * @param user The user to generate challenges for
     * @return Returns a new list of challenges for that user
     */
    public List<Challenge> generateChallengesForDay(UserEntity user) {
        LocalDateTime endDate = this.getNextChallengeEndDate();
        return this.generateChallengesForDay(user, endDate, random, DEFAULT_HOPS_DISTRIBUTION);
    }

    /**
     * Generates a new set of challenges for the user, with a specified end date, random source, and hops
     * distribution. For each int in the distribution, exactly 1 challenge will be generated with that hops
     * reward.
     *
     * @param user             The user to generate challenges for
     * @param endDate          The end date to set for the challenges
     * @param random           The random source for generation
     * @param hopsDistribution The distribution of hops to use for generating challenges
     * @return Returns a new list of challenges for that user
     */
    public List<Challenge> generateChallengesForDay(
            UserEntity user,
            LocalDateTime endDate,
            Random random,
            int[] hopsDistribution
    ) {
        LOGGER.info("Generating new challenges");
        List<Challenge> generated = new ArrayList<>();

        // memoize hops count
        Map<Integer, List<ChallengeFactory>> memoizedFactories = new HashMap<>();

        for (int hops : hopsDistribution) {
            List<ChallengeFactory> possibilities = memoizedFactories.computeIfAbsent(
                    hops,
                    h -> this.getPossibleFactories(user, h)
            );
            if (!possibilities.isEmpty()) {
                ChallengeFactory factory = possibilities.remove(random.nextInt(possibilities.size()));
                Challenge challenge = challengeRepository.save(factory.create(hops, user, endDate));
                generated.add(challenge);
            }
        }

        return generated;
    }

    /**
     * Gets the set of possible challenge factories for challenges that can be generated by the system for the user
     * given the hops distribution
     *
     * @param user The user that challenges will be generated for
     * @param hops The hops reward for the generated challenge
     * @return Returns a list of possible challenge factories, as determined by {@link ChallengeFactory#canCreate(int, UserEntity)}
     */
    public List<ChallengeFactory> getPossibleFactories(UserEntity user, int hops) {
        List<ChallengeFactory> allFactories = new ArrayList<>(quantifiableChallengeFactoryRepository.findAll());
        allFactories.addAll(generalChallengeTemplateRepository.findAll());
        return allFactories.stream()
                .filter(f -> f.canCreate(hops, user))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * @return Returns a time object which represents next monday at 0:00 UTC.
     */
    public LocalDateTime getNextChallengeEndDate() {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        return nowUtc
                .plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}
