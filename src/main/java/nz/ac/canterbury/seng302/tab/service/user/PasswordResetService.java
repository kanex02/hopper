package nz.ac.canterbury.seng302.tab.service.user;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import nz.ac.canterbury.seng302.tab.service.TokenService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validation.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    /**
     * Checks if a password reset token ID exists for a user, and returns an that user in an
     * optional if it was found.
     *
     * @param tokenId The id of a {@link Token}
     * @return Returns an optional containing a reference to the user associated with the token if found
     */
    public Optional<UserEntity> acceptToken(UUID tokenId) {

        Optional<Token> tokenResult = tokenService.getTokenById(tokenId);

        if (tokenResult.isEmpty()) {
            return Optional.empty();
        }

        Token token = tokenResult.get();
        UserEntity user = token.getUser();

        if (token.isValid() && token.getType() == TokenType.PASSWORD_RESET) {
            return Optional.of(user);
        }
        return Optional.empty();
    }


    /**
     * Creates a password reset token for the given {@code user} and sends the user an email containing the token.
     * This also schedules the token for deletion after an amount of time specified by the token's {@link TokenType}.
     *
     * @param user The user to create the password reset token for
     * @return Returns the created token
     */
    public Token createPasswordResetToken(UserEntity user) {
        Token token = tokenService.createToken(user, TokenType.PASSWORD_RESET);
        emailService.sendPasswordResetLinkEmail(user, token);
        return token;
    }

    /**
     * Attempts to reset the password of a user from a token id.
     *
     * @param tokenId        The reset token ID. The token is deleted if the password is reset successfully.
     * @param newPassword    The new password
     * @param retypePassword The retyped password
     * @return Returns an optional containing any errors encountered while attempting to reset the user's password
     * @throws IllegalArgumentException Thrown if the user has tried to maliciously reset a password with an invalid
     *                                  token
     */
    public Optional<String> tryPasswordReset(UUID tokenId, String newPassword, String retypePassword) throws IllegalArgumentException {

        Optional<Token> tokenResult = tokenService.getTokenById(tokenId);
        if (tokenResult.isEmpty()) {
            throw new IllegalArgumentException("Invalid token!");
        }
        Token token = tokenResult.get();
        if (token.getType() != TokenType.PASSWORD_RESET) {
            throw new IllegalArgumentException("Invalid token!");
        }

        UserEntity user = token.getUser();
        Optional<String> inputErrors = PasswordValidator.getInstance().isPasswordValid(
                user, newPassword, retypePassword
        );

        if (inputErrors.isPresent()) {
            return inputErrors;
        }

        // The old password is correct and passwords match; proceed with the password update process
        userService.updatePassword(user, newPassword, retypePassword);
        tokenService.deleteToken(token);
        return Optional.empty();
    }
}