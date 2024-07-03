package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.cosmetic.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
class LevelRepositoryTest {

    @Autowired
    LevelRepository levelRepository;


    @Test
    @Sql(scripts = {"/sql/level/levels.sql"})
    void givenLevels_getAll_returnsAllOrderedById() {
        List<Level> allLevels = levelRepository.findAllByOrderByIdAsc();
        List<Level> allLevelsSorted = allLevels.stream().sorted((t1, t2) -> {
            if (t1.getId().equals(t2.getId())) {
                return 0;
            } else if (t1.getId() < t2.getId()) {
                return -1;
            } else {
                return 1;
            }
        }).toList();

        Assertions.assertEquals(4, allLevels.size());
        Assertions.assertEquals(allLevelsSorted, allLevels);
    }

    @Test
    @Sql(scripts = {"/sql/level/levels.sql"})
    void givenLevels_getLevelsByIdLessThanEqual_returnsAllLevelsLessThanEqual() {
        List<Level> allLevels = levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(2L);

        Assertions.assertEquals(2, allLevels.size());
        Assertions.assertEquals(1L, allLevels.get(0).getId());
        Assertions.assertEquals(2L, allLevels.get(1).getId());
    }

    @Test
    @Sql(scripts = {"/sql/level/levels.sql"})
    void givenLevels_getLevelsByIdLessThanEqualZero_returnsEmptyList() {
        List<Level> allLevels = levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(0L);

        Assertions.assertTrue(allLevels.isEmpty());
    }

    @Test
    @Sql(scripts = {"/sql/level/levels.sql"})
    void givenLevels_getLevelsByIdLessThanEqualNull_returnsEmptyList() {
        List<Level> allLevels = levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(null);

        Assertions.assertTrue(allLevels.isEmpty());
    }


    @Test
    @Sql(scripts = {"/sql/level/levels.sql"})
    void givenLevels_getLevelsByIdLessThanEqualLargestId_returnsAllLevels() {
        List<Level> allLevels = levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc(4L);

        Assertions.assertEquals(4, allLevels.size());
        Assertions.assertEquals(1L, allLevels.get(0).getId());
        Assertions.assertEquals(2L, allLevels.get(1).getId());
        Assertions.assertEquals(3L, allLevels.get(2).getId());
        Assertions.assertEquals(4L, allLevels.get(3).getId());
    }
}