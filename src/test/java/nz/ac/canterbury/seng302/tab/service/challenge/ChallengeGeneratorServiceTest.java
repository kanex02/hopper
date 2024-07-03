package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.challenge.ChallengeFactory;
import nz.ac.canterbury.seng302.tab.entity.challenge.GeneralChallengeTemplate;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.GeneralChallengeTemplateRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.QuantifiableChallengeFactoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ChallengeGeneratorServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private QuantifiableChallengeFactoryRepository quantifiableChallengeFactoryRepository;
    @Mock
    private GeneralChallengeTemplateRepository generalChallengeTemplateRepository;
    private ChallengeGeneratorService challengeGeneratorService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        challengeGeneratorService = Mockito.spy(
                new ChallengeGeneratorService(
                        challengeRepository,
                        quantifiableChallengeFactoryRepository,
                        generalChallengeTemplateRepository,
                        new Random()
                )
        );
    }

    @Test
    void cannotGenerateDuplicateChallenges() {
        UserEntity user = new UserEntity();

        // generate the same number over and over
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };

        int[] distribution = new int[]{1, 1};
        var factory1 = new GeneralChallengeTemplate(1, "Title1", "Desc1");
        var factory2 = new GeneralChallengeTemplate(1, "Title2", "Desc2");
        List<ChallengeFactory> possibilities = new ArrayList<>(List.of(factory1, factory2));
        Mockito.when(challengeGeneratorService.getPossibleFactories(user, 1)).thenReturn(possibilities);
        Mockito.when(challengeRepository.save(Mockito.any(Challenge.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Challenge> generated = challengeGeneratorService.generateChallengesForDay(
                user,
                LocalDateTime.now().plusDays(1L),
                random,
                distribution
        );

        Assertions.assertEquals(2, new HashSet<>(generated).size());
    }
}
