package nz.ac.canterbury.seng302.tab.service.cosmetic;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.repository.CosmeticRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CosmeticServiceTest {

    @Mock
    private CosmeticRepository cosmeticRepository;
    @Mock
    private LevelService levelService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CosmeticService cosmeticService;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setLevel(1);
        user.setBadges(new ArrayList<>());
    }

    @Test
    void givenNotUnlockedBorderId_whenSetBorder_exceptionThrown() {
        Cosmetic lockedBorder = new Cosmetic(1L, "locked", CosmeticType.BORDER);

        List<Cosmetic> unlockedBorders = new ArrayList<>();

        when(levelService.getUnlockedBorders(1)).thenReturn(unlockedBorders);
        when(cosmeticRepository.getById(1L)).thenReturn(lockedBorder);

        assertThrows(IllegalArgumentException.class, () -> {
            cosmeticService.setBorder(user, "1");
        });

        verify(userService, never()).updateUser(any());
    }


    @Test
    void givenIllegalBorderId_whenSetBorder_exceptionThrown() {
        Assertions.assertThrows(NumberFormatException.class, () -> {
            cosmeticService.setBorder(user, "null");
        });
    }

    @Test
    void givenValidBorderId_whenSetBorder_borderSet() {
        List<Cosmetic> borders = List.of(
                new Cosmetic(2L, "test1", CosmeticType.BORDER)
        );

        Mockito.when(levelService.getUnlockedBorders(Mockito.anyInt())).thenReturn(borders);
        Mockito.when(cosmeticRepository.getById(Mockito.anyLong())).thenReturn(borders.get(0));

        cosmeticService.setBorder(user, "2");

        Assertions.assertEquals(borders.get(0), user.getBorder());
    }

    @Test
    void givenUnrecognisedBorderId_whenSetBorder_exceptionThrown() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            cosmeticService.setBorder(user, "99");
        });
    }

    @Test
    void givenNotUnlockedBadgeId_whenSetBadge_exceptionThrown() {
        Cosmetic lockedBadge = new Cosmetic(3L, "locked", CosmeticType.BADGE);

        List<Cosmetic> unlockedBadges = new ArrayList<>();
        List<String> badgeIds = List.of("3");

        when(levelService.getUnlockedBadges(1)).thenReturn(unlockedBadges);
        when(cosmeticRepository.getById(3L)).thenReturn(lockedBadge);

        assertThrows(IllegalArgumentException.class, () -> {
            cosmeticService.setBadges(badgeIds, user);
        });

        verify(userService, never()).updateUser(any());
    }


    @Test
    void givenIllegalBadgeId_whenSetBadge_exceptionThrown() {
        List<String> badgeIds = List.of("null");

        Assertions.assertThrows(NumberFormatException.class, () -> {
            cosmeticService.setBadges(badgeIds, user);
        });
    }

    @Test
    void givenUnrecognisedBadgeId_whenSetBadge_exceptionThrown() {
        List<String> badgeIds = List.of("99");

        Assertions.assertThrows(NullPointerException.class, () -> {
            cosmeticService.setBadges(badgeIds, user);
        });
    }

    @Test
    void givenEmptyBadgeIds_whenSetBadges_badgesCleared() {
        Cosmetic badge = new Cosmetic(4L, "test", CosmeticType.BADGE);
        user.addBadge(badge);

        cosmeticService.setBadges(new ArrayList<>(), user);

        Assertions.assertTrue(user.getBadges().isEmpty());
    }

}
