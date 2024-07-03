package nz.ac.canterbury.seng302.tab.runner;

import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.schedule.TokenDeletionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runner for setting up token deletion
 */
@Component
public class TokenDeletionSetupRunner implements ApplicationRunner {

    private final TokenDeletionService tokenDeletionService;
    private final TokenRepository tokenRepository;
    private final UserService userService;


    /**
     * Constructs a new runner with required components
     *
     * @param tokenDeletionService Token deletion service component
     * @param tokenRepository      Token repository
     * @param userService          User service component
     */
    public TokenDeletionSetupRunner(TokenDeletionService tokenDeletionService, TokenRepository tokenRepository, UserService userService) {
        this.tokenDeletionService = tokenDeletionService;
        this.tokenRepository = tokenRepository;
        this.userService = userService;
    }

    /**
     * Runs on application start
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        tokenDeletionService.scheduleDeletion(
                TokenType.REGISTRATION,
                token -> {
                    UserEntity user = token.getUser();
                    token.setUser(null);
                    tokenRepository.save(token);
                    userService.delete(user);
                }
        );

        tokenDeletionService.scheduleDeletion(TokenType.PASSWORD_RESET);
    }


}
