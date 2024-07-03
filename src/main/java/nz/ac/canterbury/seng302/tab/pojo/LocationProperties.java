package nz.ac.canterbury.seng302.tab.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A POJO to represent location properties in the JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationProperties {
    @JsonProperty("housenumber")
    private String houseNumber;
    @JsonProperty("country_code")
    private String countryCode;
    private String street;
    private String suburb;
    private String city;
    private String postcode;
    private String country;
    private double lon;
    private double lat;
    private String name;
    private String county;

    private String formatted;

    public String getCounty() {
        return county;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
