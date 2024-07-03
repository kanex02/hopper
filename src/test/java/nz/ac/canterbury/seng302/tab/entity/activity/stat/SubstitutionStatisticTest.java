package nz.ac.canterbury.seng302.tab.entity.activity.stat;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SubstitutionStatisticTest {
    @Test
    void givenOriginalPlayerAndNewPlayer_thenBuildsCorrectly() {
        UserEntity originalPlayer = new UserEntity();
        originalPlayer.setFirstName("Original");
        originalPlayer.setLastName("Player");
        UserEntity newPlayer = new UserEntity();
        newPlayer.setFirstName("New");
        newPlayer.setLastName("Player");

        var builder = new SubstitutionStatistic.SubstitutionBuilder();

        SubstitutionStatistic substitutionStatistic = builder
                .withPlayers(originalPlayer, newPlayer)
                .withTime(1)
                .buildAndValidate();

        Assertions.assertEquals(originalPlayer, substitutionStatistic.getOriginalPlayer());
        Assertions.assertEquals(newPlayer, substitutionStatistic.getNewPlayer());
        Assertions.assertEquals(1, substitutionStatistic.getTime());
    }

    @Test
    void nullPlayers_onBuild_throws() {
        var builder = new SubstitutionStatistic.SubstitutionBuilder()
                .withPlayers(null, null)
                .withTime(100L);
        Assertions.assertThrows(IllegalStateException.class, builder::buildAndValidate);
    }

//    @ParameterizedTest
//    @ValueSource(
//            longs = {
//                    -2,
//                    -1000,
//                    Integer.MIN_VALUE,
//                    Long.MIN_VALUE
//            }
//    )
//    void invalidTime_onBuild_throws(long time) {
//
//        UserEntity originalPlayer = new UserEntity();
//        originalPlayer.setFirstName("Original");
//        originalPlayer.setLastName("Player");
//        UserEntity newPlayer = new UserEntity();
//        newPlayer.setFirstName("New");
//        newPlayer.setLastName("Player");
//
//        var builder = new SubstitutionStatistic.SubstitutionBuilder()
//                .withPlayers(originalPlayer, newPlayer);
//
//        builder.withTime(time);
//
//        Assertions.assertThrows(IllegalStateException.class, builder::buildAndValidate);
//    }
}
