package nz.ac.canterbury.seng302.tab.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * Custom {@link AuthenticationFailureHandler} that extends handles authentication failure
 * with custom error messages.
 * <p>
 * This class extends {@link SimpleUrlAuthenticationFailureHandler} as its only purpose is to
 * set different failure URLs under different circumstances.
 */
public class TabFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * Redirects the user to the url {@code /login?error=MESSAGE} where {@code MESSAGE} is the result of
     * {@link AuthenticationException#getMessage()} invoked on {@code exception}
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     *                  request.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        this.setDefaultFailureUrl(String.format("/login?error=%s", exception.getMessage()));
        super.onAuthenticationFailure(request, response, exception);
    }


}
