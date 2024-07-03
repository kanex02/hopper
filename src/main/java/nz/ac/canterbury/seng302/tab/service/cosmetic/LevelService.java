package nz.ac.canterbury.seng302.tab.service.cosmetic;

import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Level;
import nz.ac.canterbury.seng302.tab.repository.LevelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling level related logic.
 */
@Service
public class LevelService {
    private final LevelRepository levelRepository;

    public LevelService(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    /**
     * Returns all levels in order.
     *
     * @return a list of all levels
     */
    public List<Level> getAllLevels() {
        return levelRepository.findAllByOrderByIdAsc();
    }

    /**
     * Returns all levels that are unlocked by a level number.
     *
     * @param levelValue the level number
     * @return a list of unlocked levels
     */
    public List<Level> getUnlockedLevels(int levelValue) {
        return levelRepository.findLevelsByIdLessThanEqualOrderByIdAsc((long) levelValue);
    }

    /**
     * Returns the cosmetics that a level has unlocked.
     *
     * @param levelValue the level to check
     * @return a list of unlocked cosmetics
     */
    public List<Cosmetic> getUnlockedCosmetics(int levelValue) {
        List<Level> levels = getUnlockedLevels(levelValue);
        List<Cosmetic> cosmetics = new ArrayList<>();
        levels.forEach(level -> {
            if (level.getReward() != null) {
                cosmetics.add(level.getReward());
            }
        });
        return cosmetics;
    }

    /**
     * Returns the borders that a level has unlocked.
     *
     * @param levelValue the level to check
     * @return a list of unlocked borders
     */
    public List<Cosmetic> getUnlockedBorders(int levelValue) {
        List<Level> levels = getUnlockedLevels(levelValue);
        List<Cosmetic> borders = new ArrayList<>();
        levels.forEach(level -> {
            if (level.getReward() != null && level.getReward().getType().equals(CosmeticType.BORDER)) {
                borders.add(level.getReward());
            }
        });
        return borders;
    }

    /**
     * Returns the badges that a level has unlocked.
     *
     * @param levelValue the level to check
     * @return a list of unlocked badges
     */
    public List<Cosmetic> getUnlockedBadges(int levelValue) {
        List<Level> levels = getUnlockedLevels(levelValue);
        List<Cosmetic> badges = new ArrayList<>();
        levels.forEach(level -> {
            if (level.getReward() != null && level.getReward().getType().equals(CosmeticType.BADGE)) {
                badges.add(level.getReward());
            }
        });
        return badges;
    }
}
