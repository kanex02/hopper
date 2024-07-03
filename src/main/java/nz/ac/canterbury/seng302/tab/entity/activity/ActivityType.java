package nz.ac.canterbury.seng302.tab.entity.activity;

/**
 * An enum that represents the different types of activities that are allowd to exist
 */
public enum ActivityType {

    GAME("Game", "#9be3f6"),
    FRIENDLY("Friendly", "#AFF69B"),
    TRAINING("Training", "#F6D99B"),
    COMPETITION("Competition", "#F69BCC"),
    OTHER("Other", "#BF9BF6");

    private final String label;

    private final String color;

    ActivityType(String label, String color) {
        this.label = label;
        this.color = color;
    }

    /**
     * Gets the label of the activity type, which is just a nice string to display for the
     * activity type. Note that this is different to the string needed for {@link ActivityType#valueOf(String)},
     * and what is returned by {@link ActivityType#toString()}.
     *
     * @return Returns the label of the activity type
     */
    public String getLabel() {
        return this.label;
    }

    public String getColor() {
        return color;
    }
}
