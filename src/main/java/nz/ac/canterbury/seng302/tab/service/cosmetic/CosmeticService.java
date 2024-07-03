package nz.ac.canterbury.seng302.tab.service.cosmetic;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.repository.CosmeticRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class that provides operations related to the borders in persistence.
 */
@Service
public class CosmeticService {

    @Autowired
    private LevelService levelService;

    @Autowired
    private UserService userService;

    @Autowired
    private CosmeticRepository cosmeticRepository;

    /**
     * Gets all cosmetics
     *
     * @return a list of all cosmetics
     */
    public List<Cosmetic> getCosmetics() {
        return cosmeticRepository.findAll();
    }

    /**
     * Gets all borders
     *
     * @return a list of all borders
     */
    public List<Cosmetic> getBorders() {
        return cosmeticRepository.findAll().stream().filter(cosmetic -> cosmetic.getType().equals(CosmeticType.BORDER)).toList();
    }

    /**
     * Gets all badges
     *
     * @return a list of all badges
     */
    public List<Cosmetic> getBadges() {
        return cosmeticRepository.findAll().stream().filter(cosmetic -> cosmetic.getType().equals(CosmeticType.BADGE)).toList();
    }

    /**
     * Finds a border by its id
     *
     * @param id the id of the border
     * @return the border with the given id
     */
    public Cosmetic findById(Long id) {
        return cosmeticRepository.getById(id);
    }

    /**
     * Sets the badges for a user
     *
     * @param badgeIds the ids of the badges to set
     * @param user     the user to set the badges for
     */
    public void setBadges(List<String> badgeIds, UserEntity user) {

        List<Cosmetic> newBadgeList = new ArrayList<>();
        for (String badgeId : badgeIds) {
            if (badgeId.equals("")) {
                continue;
            }
            List<Cosmetic> unlockedBadges = levelService.getUnlockedBadges(user.getLevel());
            Cosmetic badgeToAdd = Objects.requireNonNull(this.findById(Long.parseLong(badgeId)));
            if (unlockedBadges.contains(badgeToAdd)) {
                // Set the insert_order for the badge based on the current size of the list
                badgeToAdd.setDisplayOrder(newBadgeList.size());
                newBadgeList.add(badgeToAdd);
            } else {
                throw new IllegalArgumentException("Tried to add a badge that is not unlocked for user");
            }
        }
        user.setBadges(newBadgeList);
        userService.updateUser(user);
    }

    /**
     * Sets the border for a user
     *
     * @param user     the user to set the border for
     * @param borderId the id of the border to set
     */
    public void setBorder(UserEntity user, String borderId) {
        List<Cosmetic> unlockedBorders = levelService.getUnlockedBorders(user.getLevel());
        Cosmetic borderToAdd = Objects.requireNonNull(this.findById(Long.parseLong(borderId)));
        if (unlockedBorders.contains(borderToAdd)) {
            user.setBorder(borderToAdd);
            userService.updateUser(user);
        } else {
            throw new IllegalArgumentException("Tried to add a border that is not unlocked for user");
        }
    }


}
