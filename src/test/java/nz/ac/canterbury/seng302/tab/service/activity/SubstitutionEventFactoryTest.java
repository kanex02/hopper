package nz.ac.canterbury.seng302.tab.service.activity;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

class SubstitutionEventFactoryTest {

    private static final long ORIGINAL_PLAYER_ID = 69L;
    private static final long NEW_PLAYER_ID = 420L;

    private static final UserEntity ORIGINAL_PLAYER = new UserEntity(
            "Password1@",
            "The OG",
            "Gamer",
            "original@example.com",
            LocalDateTime.now().minusYears(20).toString(),
            Set.of(),
            null
    );

    private static final UserEntity NEW_PLAYER = new UserEntity(
            "Password1@",
            "The Brand New",
            "And Exciting",
            "brave.new.world@example.com",
            LocalDateTime.now().minusYears(22).toString(),
            Set.of(),
            null
    );

    private static UserService mockedUserService;

    private ActivityStatisticFactory<SubstitutionStatistic> factory = new SubstitutionEventFactory();


    @BeforeAll
    static void setupServiceMocks() {

        mockedUserService = Mockito.mock(UserService.class);


        ORIGINAL_PLAYER.setId(ORIGINAL_PLAYER_ID);


        NEW_PLAYER.setId(NEW_PLAYER_ID);

        Mockito.when(mockedUserService.getUserById(ORIGINAL_PLAYER_ID))
                .thenReturn(ORIGINAL_PLAYER);

        Mockito.when(mockedUserService.getUserById(NEW_PLAYER_ID))
                .thenReturn(NEW_PLAYER);


    }

    @ParameterizedTest
    @ValueSource(
            longs = {
                    30L,
                    10000L,
                    Integer.MAX_VALUE,
                    Long.MAX_VALUE
            }
    )
    void validSubstitutionData_onParse_returnsCorrectEntity(long time) {
        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.TIME_KEY, String.valueOf(time));
        data.put(SubstitutionEventFactory.ORIGINAL_PLAYER_ID_KEY, String.valueOf(ORIGINAL_PLAYER_ID));
        data.put(SubstitutionEventFactory.NEW_PLAYER_ID_KEY, String.valueOf(NEW_PLAYER_ID));

        AtomicReference<SubstitutionStatistic> statRef = new AtomicReference<>(null);

        Assertions.assertDoesNotThrow(() -> statRef.set(factory.parse(data, mockedUserService)));

        SubstitutionStatistic stat = statRef.get();

        Assertions.assertEquals(time, stat.getTime());
        Assertions.assertEquals(NEW_PLAYER, stat.getNewPlayer());
        Assertions.assertEquals(ORIGINAL_PLAYER, stat.getOriginalPlayer());
    }

    @Test
    void substitutionDataNoPlayers_onParse_throws() {
        long time = 30L;

        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.TIME_KEY, String.valueOf(time));

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.parse(data, mockedUserService));
    }

    @Test
    void substitutionDataNoOriginalPlayer_onParse_throws() {
        long time = 30L;

        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.TIME_KEY, String.valueOf(time));
        data.put(SubstitutionEventFactory.ORIGINAL_PLAYER_ID_KEY, String.valueOf(ORIGINAL_PLAYER_ID));

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.parse(data, mockedUserService));
    }

    @Test
    void substitutionDataNoNewPlayer_onParse_throws() {
        long time = 30L;

        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.TIME_KEY, String.valueOf(time));
        data.put(SubstitutionEventFactory.NEW_PLAYER_ID_KEY, String.valueOf(NEW_PLAYER_ID));

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.parse(data, mockedUserService));
    }

    @Test
    void substitutionDataNoTime_onParse_throws() {
        long time = 30L;

        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.NEW_PLAYER_ID_KEY, String.valueOf(NEW_PLAYER_ID));
        data.put(SubstitutionEventFactory.ORIGINAL_PLAYER_ID_KEY, String.valueOf(ORIGINAL_PLAYER_ID));

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.parse(data, mockedUserService));
    }

    @Test
    void substitutionDataUnknownNewPlayerId_onParse_throws() {
        long time = 30L;
        Map<String, String> data = new HashMap<>();

        data.put(SubstitutionEventFactory.TIME_KEY, String.valueOf(time));
        data.put(SubstitutionEventFactory.NEW_PLAYER_ID_KEY + 1, String.valueOf(NEW_PLAYER_ID));
        data.put(SubstitutionEventFactory.ORIGINAL_PLAYER_ID_KEY, String.valueOf(ORIGINAL_PLAYER_ID));

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.parse(data, mockedUserService));
    }
}
