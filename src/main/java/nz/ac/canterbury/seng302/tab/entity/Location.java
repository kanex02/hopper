package nz.ac.canterbury.seng302.tab.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.entity.club.Club;

import java.util.List;
import java.util.StringJoiner;


/**
 * Entity class of a Location
 */
@Entity
@Table(name = "location")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//@ValidLocationFields
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "location")
    private List<Team> teams;

    @OneToMany(mappedBy = "location")
    private List<UserEntity> user;

    @OneToMany(mappedBy = "location")
    private List<Club> club;

    @Column
    @Pattern(regexp = "^(?![ .,/-])(?!.*[ #.,/-]$)[\\p{L}\\p{N} #.,/-]{0,100}$", message = "Please enter a valid address.")
    private String addressLine1;

    @Column
    @Pattern(regexp = "^(?![ .,/-])(?!.*[ #.,/-]$)[\\p{L}\\p{N} #.,/-]{0,100}$", message = "Please enter a valid address.")
    private String addressLine2;

    @Column
    @Pattern(regexp = "^(?![ &.'-])(?!.*[ &.'-]$)[\\p{L} &.'-]{0,100}$", message = "Please enter a valid suburb.")
    private String suburb;

    @Column
    @NotBlank(message = "City cannot be empty.")
    @Pattern(regexp = "^(?![ &.'-])(?!.*[ &.'-]$)[\\p{L} &.'-]{0,100}$", message = "Please enter a valid city.")
    private String city;

    @Column
    @Pattern(regexp = "^(?![ -])(?!.*[ -]$)[\\p{L}\\p{N} -]{0,20}$", message = "Please enter a valid postcode.")
    private String postcode;

    @Column
    @NotBlank(message = "Country cannot be empty.")
    @Pattern(regexp = "^(?![ '-])(?!.*[ '-]$)[\\p{L} '-]{0,100}$", message = "Please enter a valid country.")
    private String country;

    /**
     * JPA required no-args constructor
     */
    public Location() {
    }

    /**
     * Creates a new Location object
     *
     * @param addressLine1 address
     * @param addressLine2 address 2
     * @param suburb       suburb
     * @param city         city
     * @param postcode     postcode
     * @param country      country
     */
    public Location(String addressLine1, String addressLine2, String suburb, String city, String postcode, String country) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.suburb = suburb;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        var result = new StringJoiner(", ");
        if (addressLine1 != null && !addressLine1.isBlank()) {
            result.add(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.isBlank()) {
            result.add(addressLine2);
        }
        if (suburb != null && !suburb.isBlank()) {
            result.add(suburb);
        }

        result.add(city);
        result.add(country);

        if (postcode != null && !postcode.isBlank()) {
            result.add(postcode);
        }

        return result.toString();
    }

    /**
     * Returns whether this location is valid, purely based on the city and country.
     * This is used to display old locations where the address is not valid anymore.
     */
    public boolean isValid () {
        return city != null && country != null && !city.isBlank() && !country.isBlank();
    }

}
