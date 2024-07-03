package nz.ac.canterbury.seng302.tab.service;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeFormatterService {

    /**
     * Returns string of the formatted time since post
     *
     * @param createdDate date of creation
     * @return formatted string of the time
     */
    public static String getFormattedTimeSinceCreation(LocalDateTime createdDate) {
        if (createdDate == null) {
            return null;
        }

        LocalDateTime currentDate = LocalDateTime.now();
        long diffInSeconds = Duration.between(createdDate, currentDate).getSeconds();
        long qty = 0;
        String unit = "";

        if (diffInSeconds < 60) {
            return "Just now";
        } else if (diffInSeconds < 3600) {
            qty = diffInSeconds / 60;
            unit = "minute";
        } else if (diffInSeconds < 86400) {
            qty = diffInSeconds / 3600;
            unit = "hour";
        } else if (diffInSeconds < 604800) {
            qty = diffInSeconds / 86400;
            unit = "day";
        } else if (diffInSeconds < 2419200) {
            qty = diffInSeconds / 604800;
            unit = "week";
        } else if (diffInSeconds < 29030400) {
            qty = diffInSeconds / 2419200;
            unit = "month";
        } else {
            qty = diffInSeconds / 29030400;
            unit = "year";
        }

        return qty + " " + unit + (qty > 1 ? "s" : "") + " ago";
    }



}
