package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.pojo.LocationProperties;
import nz.ac.canterbury.seng302.tab.service.LocationAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * This is a controller which is responsible for return suggested locations once called upon.
 * The {@link RequestMapping} annotation defines the path to listen on.
 */
@RestController
@RequestMapping("locationSuggest")
public class LocationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);
    @Autowired
    LocationAPIService locationAPIService;

    /**
     * Suggests locations based on a query parameter.
     * @param query The query to autocomplete.
     * @return A JSON string containing a list of {@link LocationProperties} objects.
     */
    @GetMapping
    public String locationSuggestions(@RequestParam(value = "q", required = false) String query) {
        if (!StringUtils.hasText(query)) {
            return "[]";
        }
        List<LocationProperties> locations = locationAPIService.suggestions(query, 5);

        // Serialize Java as a JSON output
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectMapper mapper = new ObjectMapper();

            mapper.writeValue(out, locations);

            return out.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "[]";
        }
    }
}
