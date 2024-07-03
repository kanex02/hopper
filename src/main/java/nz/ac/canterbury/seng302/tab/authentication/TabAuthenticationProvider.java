package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of {@link AuthenticationProvider} for handling authentication of {@link UserEntity}s in TAB
 * <p>
 * Code largely based on example provided by Morgan English.
 *
 * @author <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/-/tree/master/src/main/java/security/example">Morgan English</a>
 */
@Component
public class TabAuthenticationProvider extends DaoAuthenticationProvider {

    /**
     * Autowired user service for TAB authentication of {@link UserEntity}s
     */
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String BAD_CREDENTIALS_MESSAGE = "Invalid email or password";

    public TabAuthenticationProvider(UserService userService) {
        this.userService = userService;
        super.setUserDetailsService(userService);
        this.setHideUserNotFoundExceptions(true);
    }

    /**
     * Custom authentication implementation.
     *
     * @param authentication The authentication request object. Must have a non-empty username and password.
     * @return Returns a new {@link UsernamePasswordAuthenticationToken} if email and password are valid, and account is active with users
     * authorities
     * @throws AuthenticationException Thrown if the username or email are invalid
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            super.authenticate(authentication);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }
        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }

        UserEntity user = userService.getUserByEmail(email);
        if (user == null) {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }
        if (this.isUserAccountInactive(user)) {
            throw new DisabledException("Your account is not activated yet! Open the link in your email to activate your account.");
        }

        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
    }

    /**
     * Checks if the user has not confirmed their email address yet
     *
     * @param user The user to chek
     * @return Returns true if the user's email is UNCONFIRMED
     */
    public boolean isUserAccountInactive(UserEntity user) {
        return !user.isEmailConfirmed();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
