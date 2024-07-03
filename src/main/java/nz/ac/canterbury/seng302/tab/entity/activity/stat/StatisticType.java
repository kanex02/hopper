package nz.ac.canterbury.seng302.tab.entity.activity.stat;

/**
 * An enum that represents the different types of statistics that exist
 */
public enum StatisticType {

    FACT("Fact"),
    SCORE_EVENT("Score"),
    SUBSTITUTION("Substitution");

    private final String label;

    StatisticType(String label) {
        this.label = label;
    }

    /**
     * Gets the label of the statistic type, which is just a nice string to display for the
     * statistic type from Thymeleaf. Note that this is different to the string needed for {@link StatisticType#valueOf(String)},
     * and what is returned by {@link StatisticType#toString()}.
     *
     * @return Returns the label of the statistic type
     */
    public String getLabel() {
        return this.label;
    }
}
