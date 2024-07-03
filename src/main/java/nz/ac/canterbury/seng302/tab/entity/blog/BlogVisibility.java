package nz.ac.canterbury.seng302.tab.entity.blog;

/**
 * Represents the visibility of a blog post. The user selects a team or club to share to, or can share to the public.
 */
public enum BlogVisibility {
    PUBLIC("PUBLIC"),
    CLUB("CLUB"),
    TEAM("TEAM");

    private final String name;

    BlogVisibility (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
