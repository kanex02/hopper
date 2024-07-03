package nz.ac.canterbury.seng302.tab.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A component to provide the {@link PasswordEncoder} used by this application.
 */
@Component
public class PasswordProvider {

    /**
     * This method creates and returns a new instance of a password encoder using the PasswordEncoderFactories class.
     *
     * @return a new instance of a PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
