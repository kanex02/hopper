package nz.ac.canterbury.seng302.tab.entity.cosmetic;

import jakarta.persistence.*;

/**
 * Class to represent a cosmetic item (e.g. a border or a badge) that the user can display on their profile.
 */
@Entity
public class Cosmetic {

    @Id
    private Long id;
    @Column
    private String path;
    @Column
    private CosmeticType type;

    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * JPA required no-args constructor. Do not call.
     */
    public Cosmetic() {
    }

    /**
     * A protected constructor for cosmetics.
     * <p>
     * Only call this to construct concrete implementations.
     *
     * @param id   id of the cosmetic
     * @param path the path to the image
     */
    public Cosmetic(Long id, String path, CosmeticType type) {
        this.id = id;
        this.path = path;
        this.type = type;
    }


    /**
     * Gets the id of the cosmetic
     *
     * @return the id of the cosmetic
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the path to the cosmetic image
     *
     * @return the path to the cosmetic image
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the type of the cosmetic
     *
     * @return the type of the cosmetic
     */
    public CosmeticType getType() {
        return type;
    }

    /**
     * [For badges] Gets the display order of the badge (i.e. 0 = first, 1 = second, 2 = third).
     * While this is a good case for inheritance, which was previously used, it is not worth the effort to refactor back.
     * @return the badge's display order
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * [For badges] Sets the display order of the badge (i.e. 0 = first, 1 = second, 2 = third).
     * While this is a good case for inheritance, which was previously used, it is not worth the effort to refactor back.
     * @param position the badge's display order
     */
    public void setDisplayOrder(Integer position) {
        this.displayOrder = position;
    }
}
