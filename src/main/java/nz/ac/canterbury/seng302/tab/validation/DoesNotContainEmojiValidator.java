package nz.ac.canterbury.seng302.tab.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.service.SearchService;

/**
 * Implementation for {@link DoesNotContainEmoji} for {@link CharSequence}
 */
public class DoesNotContainEmojiValidator implements ConstraintValidator<DoesNotContainEmoji, CharSequence> {

    private final SearchService searchService = new SearchService();

    /**
     * Implements the validation logic. The state of value must not be altered.
     * This method can be accessed concurrently.
     */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return !searchService.containsEmoji(value);
    }
}
