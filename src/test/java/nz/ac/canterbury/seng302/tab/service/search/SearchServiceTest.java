package nz.ac.canterbury.seng302.tab.service.search;

import nz.ac.canterbury.seng302.tab.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SearchServiceTest {
    
    private final SearchService searchService = new SearchService();
    
    @ParameterizedTest
    @ValueSource(
        strings = {
            "👨‍",
            "\uD83D\uDE03"
        }
    )
    void emojis_checkForEmojis_returnsTrue (String text) {
        boolean result = searchService.containsEmoji(text);
        Assertions.assertTrue(result);
    }
    
    @ParameterizedTest
    @ValueSource(
        strings = {
            "not an emoji",
            "text",
            "",
            "   ",
            "0.0"
        }
    )
    void normalString_checkForEmojis_returnsFalse (String text) {
        boolean result = searchService.containsEmoji(text);
        Assertions.assertFalse(result);
    }
    
}
