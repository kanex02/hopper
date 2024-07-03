package nz.ac.canterbury.seng302.tab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.pojo.Feature;
import nz.ac.canterbury.seng302.tab.pojo.FeatureCollection;
import nz.ac.canterbury.seng302.tab.pojo.LocationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A service to access location recommendations from Geoapify.
 */
@Service
public class LocationAPIService {

    public static final Logger logger = LoggerFactory.getLogger(LocationAPIService.class);

    @Autowired
    private Environment environment;

    /**
     * Returns a list of {@link LocationProperties} from the Geoapify autocompletion API.
     * @param query The string to autocomplete.
     * @param limit The maximum number of results to return.
     * @return A list of suggestions.
     */
    public List<LocationProperties> suggestions(String query, int limit) {
        try {
            // Gets the API key the environment variables.
            String key = environment.getProperty("LOCATION_API_KEY");

            // Set up the connection
            String urlString = "https://api.geoapify.com/v1/geocode/autocomplete" +
                    "?apiKey=" + key +
                    "&text=" + query.replace(" ", "%20") +
                    "&limit=" + limit +
                    "&type=amenity";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Gets an input stream from the connection and maps it to a FeatureCollection
            ObjectMapper objectMapper = new ObjectMapper();
            FeatureCollection locations = objectMapper.readValue(conn.getInputStream(), FeatureCollection.class);
            List<LocationProperties> locationPropertiesList = locations.getFeatures().stream().map(Feature::getProperties).toList();

            // If a suggestion is in New Zealand, shuffle around some data until it is correct, as the API return incorrect data.
            return locationPropertiesList.stream().map(locationProperties -> {
                if (Objects.equals(locationProperties.getCountryCode(), "nz")) {
                    locationProperties.setSuburb(locationProperties.getCity());
                    locationProperties.setCity(locationProperties.getCounty());
                }
                return locationProperties;
            }).toList();

        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return new ArrayList<>();
        }
    }
}
