package nz.ac.canterbury.seng302.tab.service.cosmetic;


import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Level;
import nz.ac.canterbury.seng302.tab.repository.LevelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class LevelServiceTest {
     @Mock
     private LevelRepository levelRepository;

     @InjectMocks
     private LevelService levelService;

     @Test
     void givenUnlockedLevelsWithSomeBorders_whenGetAllUnlockedBorders_returnsAllBorders() {
         List<Cosmetic> borders = List.of(
                 new Cosmetic(1L, "test1", CosmeticType.BORDER),
                 new Cosmetic(7L, "test2", CosmeticType.BORDER),
                 new Cosmetic(3L, "test3", CosmeticType.BORDER),
                 new Cosmetic(5L, "test4", CosmeticType.BORDER)
         );

         List<Level> levels = List.of(
                 new Level(1L, null),
                 new Level(2L, borders.get(0)),
                 new Level(3L, borders.get(1)),
                 new Level(4L, null),
                 new Level(5L, borders.get(2)),
                 new Level(6L, borders.get(3))
         );

         Mockito.when(levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(6L)).thenReturn(levels);
         List<Cosmetic> unlockedBorders = levelService.getUnlockedBorders(6);


         Assertions.assertIterableEquals(borders, unlockedBorders);

     }


    @Test
    void givenUnlockedLevelsWithoutBorders_whenGetUnlockedBorders_returnsEmpty() {

        List<Level> levels = List.of(
                new Level(1L, null),
                new Level(2L, null),
                new Level(3L, null),
                new Level(4L, null),
                new Level(5L, null),
                new Level(6L, null)
        );

        Mockito.when(levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(6L)).thenReturn(levels);
        List<Cosmetic> unlockedBorders = levelService.getUnlockedBorders(6);

        Assertions.assertTrue(unlockedBorders.isEmpty());

    }

    @Test
    void givenUnlockedLevelsWithAllBorders_whenGetAllUnlockedBorders_returnsAllBorders() {
        List<Cosmetic> borders = List.of(
                new Cosmetic(1L, "test1", CosmeticType.BORDER),
                new Cosmetic(7L, "test2", CosmeticType.BORDER),
                new Cosmetic(3L, "test3", CosmeticType.BORDER),
                new Cosmetic(5L, "test4", CosmeticType.BORDER)
        );

        List<Level> levels = List.of(
                new Level(1L, borders.get(0)),
                new Level(2L, borders.get(1)),
                new Level(3L, borders.get(2)),
                new Level(4L, borders.get(3))
        );

        Mockito.when(levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(4L)).thenReturn(levels);
        List<Cosmetic> unlockedBorders = levelService.getUnlockedBorders(4);


        Assertions.assertIterableEquals(borders, unlockedBorders);

    }

}