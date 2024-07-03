package nz.ac.canterbury.seng302.tab.service.schedule;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Service that managed background threads for deleting tokens when they expire
 */
@Service
public class TokenDeletionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenDeletionService.class);
    private final ScheduledExecutorService scheduledExecutorService;
    private final TokenRepository tokenRepository;

    /**
     * Constructs a token deletion service with a repository and executor service
     *
     * @param scheduledExecutorService The schedule executor to use for managing threads and scheduling background tasks
     * @param tokenRepository          Token repository access
     */
    public TokenDeletionService(
            ScheduledExecutorService scheduledExecutorService,
            TokenRepository tokenRepository
    ) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Schedules a token type to be checked and deleted periodically. The time period is proportional to
     * {@link TokenType#getExpirationSeconds()}.
     *
     * @param type The token type to schedule.
     */
    public void scheduleDeletion(TokenType type) {
        this.scheduleDeletion(type, t -> {});
    }

    /**
     * Schedules a token type to be checked and deleted periodically. The time period is proportional to
     * {@link TokenType#getExpirationSeconds()}.
     *
     * @param type                  The token type to schedule.
     * @param tokenDeletionCallback Optional callback that is invoked on tokens that will be deleted.
     *                              The token is not deleted when this callback is invoked, and the callback
     *                              should not additionally delete the token.
     */
    public void scheduleDeletion(TokenType type, Consumer<Token> tokenDeletionCallback) {
        this.scheduledExecutorService.scheduleAtFixedRate(
                () -> deleteTokens(type, tokenDeletionCallback),
                0L,
                1L,
                TimeUnit.MINUTES
        );
    }

    /**
     * Runs an update of the token deletion. Deletes all expired tokens of the given type,
     * and runs the callback function on those tokens which should be deleted.
     *
     * <h1>THIS METHOD IS ONLY EXPOSED FOR TESTING PURPOSES! DO NOT CALL!</h1>
     *
     * @param type                  The token type to schedule.
     * @param tokenDeletionCallback Callback that is invoked on tokens that will be deleted.
     *                              The token is not deleted when this callback is invoked, and the callback
     *                              should not additionally delete the token.
     */
    public void deleteTokens(TokenType type, Consumer<Token> tokenDeletionCallback) {
        LOGGER.info("Updating token deletion for {}", type);
        List<Token> toDelete = new ArrayList<>();
        tokenRepository.findAllByType(type).forEach(
                token -> {
                    if (token.isExpired()) {
                        LOGGER.info("Deleting a token with type {}", type);
                        tokenDeletionCallback.accept(token);
                        toDelete.add(token);
                    }
                }
        );
        tokenRepository.deleteAll(toDelete);
    }

}
