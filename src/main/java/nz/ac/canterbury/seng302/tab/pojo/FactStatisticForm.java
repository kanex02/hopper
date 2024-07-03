package nz.ac.canterbury.seng302.tab.pojo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * A form to store user input when submitting a fact event. Call @Valid on this class to validate the user input.
 * @param description The description string detailing the fact
  * @param time The time within an activity that the event occurred at
 */
public record FactStatisticForm(
    @Pattern(regexp = "(?s)^.{1,255}$")
    @NotNull
    String description,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    String time
) {}
