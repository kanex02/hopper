package nz.ac.canterbury.seng302.tab.service;

/**
 * Service class for validation checks on search inputs
 */
public class SearchService {
    
    /**
     * Checks if the given string contains any emoji characters.
     *
     * @param s The input string to be checked.
     * @return {@code true} if the string contains an emoji character; {@code false} otherwise.
     */
    public boolean containsEmoji(CharSequence s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char hs = s.charAt(i);
            if (isEmojiCharacter(hs, i < length - 1 ? s.charAt(i + 1) : 0)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the given high surrogate (hs) and low surrogate (ls) represent an emoji character.
     *
     * @param hs The high surrogate part of the UTF-16 encoded character.
     * @param ls The low surrogate part of the UTF-16 encoded character (or 0 if not applicable).
     * @return {@code true} if the surrogates represent an emoji character; {@code false} otherwise.
     */
    private boolean isEmojiCharacter(char hs, char ls) {
        if (0xd800 <= hs && hs <= 0xdbff) { // high surrogate
            int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;

            return (0x1F600 <= uc && uc <= 0x1F64F) ||  // Emoticons
                    (0x1F300 <= uc && uc <= 0x1F5FF) ||  // Misc Symbols and Pictographs
                    (0x1F680 <= uc && uc <= 0x1F6FF) ||  // Transport & Map
                    (0x1F700 <= uc && uc <= 0x1F77F);    // Alchemical Symbols
        } else {
            return (0x2600 <= hs && hs <= 0x26FF) ||  // Misc symbols
                    (0x2300 <= hs && hs <= 0x23FF) ||  // Misc Technical
                    (0x2B50 == hs);  // Star
        }
    }

}
