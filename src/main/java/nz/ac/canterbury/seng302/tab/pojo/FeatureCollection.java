package nz.ac.canterbury.seng302.tab.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A wrapper for a list of {@link Feature}s to fit the JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureCollection {
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }
}
