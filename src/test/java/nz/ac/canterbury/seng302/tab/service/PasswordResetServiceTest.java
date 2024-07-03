package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.user.PasswordResetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void validToken_acceptToken_userReturned() {
        UUID tokenId = UUID.randomUUID();
        Token token = mock(Token.class);
        UserEntity user = mock(UserEntity.class);

        when(tokenService.getTokenById(tokenId)).thenReturn(Optional.of(token));
        when(token.isValid()).thenReturn(true);
        when(token.getType()).thenReturn(TokenType.PASSWORD_RESET);
        when(token.getUser()).thenReturn(user);

        Optional<UserEntity> result = passwordResetService.acceptToken(tokenId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user, result.get());
    }

    @Test
    void validToken_tryPasswordReset_passwordUpdated() {
        UUID tokenId = UUID.randomUUID();
        Token token = mock(Token.class);
        String email = "test@example.com";
        String password = "Password1@";
        UserEntity user = new UserEntity(
                password,
                "Fabian",
                "Gilson",
                email,
                "1900-01-01",
                Set.of(),
                null
        );

        when(tokenService.getTokenById(tokenId)).thenReturn(Optional.of(token));
        when(token.getType()).thenReturn(TokenType.PASSWORD_RESET);
        when(token.getUser()).thenReturn(user);

        Optional<String> result = passwordResetService.tryPasswordReset(tokenId, "h>IduRs9QS5i2dA", "h>IduRs9QS5i2dA");

        Assertions.assertFalse(result.isPresent());
        verify(userService).updatePassword(user, "h>IduRs9QS5i2dA", "h>IduRs9QS5i2dA");
        verify(tokenService).deleteToken(token);
    }
}
