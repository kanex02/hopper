package nz.ac.canterbury.seng302.tab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.stream.Stream;

public class ControllerUtils {

    static final String HOME_REDIRECT = "redirect:/home";
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    private ControllerUtils() {
    }

    /**
     * Gets the redirection command to refer the user back to from a full referrer url.
     * For example, if the user was referred from {@code "https://example.com/test"}, this method
     * will return {@code "redirect:/test"}. If the provided referral url is {@code null} (ie there is no
     * referrer), or the url is not a valid url, then this method returns {@value #HOME_REDIRECT}.
     *
     * @param referrerURL The url the user came from, usually retrieved with {@link org.springframework.web.bind.annotation.RequestBody}.
     *                    May be {@code null}.
     * @return Returns the path of the provided URL, or {@value #HOME_REDIRECT} if {@code referrerURL} is null or invalid.
     */
    public static String referrerPath(@Nullable String referrerURL) {
        return referrerPath(referrerURL, true);
    }

    /**
     * Gets the redirection command to refer the user back to from a full referrer url.
     * For example, if the user was referred from {@code "https://example.com/test"}, this method
     * will return {@code "redirect:/test"}. If the provided referral url is {@code null} (ie there is no
     * referrer), or the url is not a valid url, then this method returns {@value #HOME_REDIRECT}.
     *
     * @param referrerURL The url the user came from, usually retrieved with {@link org.springframework.web.bind.annotation.RequestBody}.
     *                    May be {@code null}.
     * @param isLocal     Optional. Defaults to {@code true}. If {@code false}, then the first element of the path will
     *                    be stripped. This is for deployed instances where we want to remove {@code "/test"} and
     *                    {@code "/prod"} from the path.
     * @return Returns the path of the provided URL, or {@value #HOME_REDIRECT} if {@code referrerURL} is null or invalid.
     */
    public static String referrerPath(@Nullable String referrerURL, boolean isLocal) {
        if (referrerURL == null) {
            return HOME_REDIRECT;
        }

        URI uri;

        try {
            uri = URI.create(referrerURL);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not parse referrer URL: {}", referrerURL);
            return HOME_REDIRECT;
        }

        String path = uri.getPath();
        if (!isLocal) {
            String delim = "/";
            String[] components = path.split(delim);
            path = delim + String.join(
                    "/",
                    Stream.of(components)
                            .skip(2L)
                            .toArray(String[]::new)
            );
        }

        return "redirect:" + path;

    }
}
