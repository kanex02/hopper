package nz.ac.canterbury.seng302.tab.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A wrapper for {@link LocationProperties} to fit the JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {
    private String type;
    private LocationProperties properties;

    public String getType() {
        return type;
    }

    public LocationProperties getProperties() {
        return properties;
    }
}