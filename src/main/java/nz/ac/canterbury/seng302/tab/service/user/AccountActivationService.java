package nz.ac.canterbury.seng302.tab.service.user;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.TokenService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service module for account activations
 */
@Service
public class AccountActivationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountActivationService.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    /**
     * Accepts a registration token and activates the user's account if the token is valid
     *
     * @param tokenId The id of a {@link Token}
     * @return Returns true if the users token has not expired
     */
    public boolean acceptToken(UUID tokenId) {

        Optional<Token> tokenResult = tokenService.getTokenById(tokenId);

        if (tokenResult.isEmpty()) {
            return false;
        }

        Token token = tokenResult.get();
        UserEntity user = token.getUser();

        if (token.getType() == TokenType.REGISTRATION && token.isValid()) {
            user.confirmEmail();
            userService.updateUser(user);
            tokenService.deleteToken(token);
            return true;
        }
        return false;
    }

    /**
     * Creates a registration token linked to the given user
     *
     * @param user newly registered user
     * @return the token entity
     */
    public Token createToken(UserEntity user) {
        var registrationToken = tokenService.createToken(user, TokenType.REGISTRATION);

        LOGGER.info("Created new user");
        return registrationToken;
    }
}
