package nz.ac.canterbury.seng302.tab.runner;

import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Level;
import nz.ac.canterbury.seng302.tab.repository.CosmeticRepository;
import nz.ac.canterbury.seng302.tab.repository.LevelRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runner for creating the default data for {@link Cosmetic}s and levels.
 */
@Component
public class CosmeticRunner implements ApplicationRunner {

    private final CosmeticRepository cosmeticRepository;

    private final LevelRepository levelRepository;

    /**
     * Constructor for the border runner.
     *
     * @param cosmeticRepository the border repository
     * @param levelRepository    the level repository
     */
    public CosmeticRunner(CosmeticRepository cosmeticRepository, LevelRepository levelRepository) {
        this.cosmeticRepository = cosmeticRepository;
        this.levelRepository = levelRepository;
    }

    /**
     * Creates the default borders if they do not exist,
     * and defines the default leveling rewards.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        List<Cosmetic> borders = List.of(
                new Cosmetic(1L, "/images/borders/basicBlue.png", CosmeticType.BORDER),
                new Cosmetic(2L, "/images/borders/paint.png", CosmeticType.BORDER),
                new Cosmetic(3L, "/images/borders/glitch.png", CosmeticType.BORDER),
                new Cosmetic(4L, "/images/borders/wood.png", CosmeticType.BORDER),
                new Cosmetic(5L, "/images/borders/greenery.png", CosmeticType.BORDER),
                new Cosmetic(6L, "/images/borders/submarine.png", CosmeticType.BORDER),
                new Cosmetic(7L, "/images/borders/prison.png", CosmeticType.BORDER)
        );

        cosmeticRepository.saveAll(borders);

        List<Cosmetic> badges = List.of(
                new Cosmetic(8L, "/images/badges/gym_bunnies.png", CosmeticType.BADGE),
                new Cosmetic(9L, "/images/badges/VR_bunnies.png", CosmeticType.BADGE),
                new Cosmetic(10L, "/images/badges/bloated_bunny.png", CosmeticType.BADGE),
                new Cosmetic(11L, "/images/badges/hopper(hyper)trophy.png", CosmeticType.BADGE),
                new Cosmetic(12L, "/images/badges/ALL_HAIL_THE_GOLDEN_CARROT.png", CosmeticType.BADGE),
                new Cosmetic(13L, "/images/badges/cosmic_bunny.png", CosmeticType.BADGE),
                new Cosmetic(14L, "/images/badges/carrotsabre_bunny.png", CosmeticType.BADGE),
                new Cosmetic(15L, "/images/badges/polished_egg.png", CosmeticType.BADGE),
                new Cosmetic(16L, "/images/badges/gemmed_egg.png", CosmeticType.BADGE),
                new Cosmetic(17L, "/images/badges/gilded_egg.png", CosmeticType.BADGE),
                new Cosmetic(18L, "/images/badges/lepus_maximus.png", CosmeticType.BADGE)
        );

        cosmeticRepository.saveAll(badges);

        if (levelRepository.count() == 0) {

            List<Level> levels = List.of(
                    new Level(1L, null),
                    new Level(2L, borders.get(0)),
                    new Level(3L, badges.get(0)),
                    new Level(4L, borders.get(1)),
                    new Level(5L, badges.get(1)),
                    new Level(6L, borders.get(2)),
                    new Level(7L, badges.get(2)),
                    new Level(8L, borders.get(3)),
                    new Level(9L, badges.get(3)),
                    new Level(10L, borders.get(4)),
                    new Level(11L, badges.get(4)),
                    new Level(12L, borders.get(5)),
                    new Level(13L, badges.get(5)),
                    new Level(14L, borders.get(6)),
                    new Level(15L, badges.get(6)),
                    new Level(16L, badges.get(7)),
                    new Level(17L, badges.get(8)),
                    new Level(18L, badges.get(9)),
                    new Level(19L, badges.get(10))
            );
            levelRepository.saveAll(levels);
        }
    }
}
