package nz.ac.canterbury.seng302.tab.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.tab.TabApplication;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ContextConfiguration(classes = TabApplication.class)
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class ContextConfiguration {

    /**
     * Use this to authenticate a mock user for acceptance testing within the Cucumber Context.
     * @param authManager the (optionally autowired) {@link AuthenticationManager} for the authentication
     * @param passwordEncoder the (optionally autowired) {@link PasswordEncoder} required for password encoding
     * @param userService the (optionally autowired) {@link UserService} for persisting the authenticated mock user
     * @return the {@link UserEntity} for further manipulation if needed
     */
    public static UserEntity authenticateTestUser(AuthenticationManager authManager, PasswordEncoder passwordEncoder, UserService userService) {
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
        user.setId(1L);
        user.hashPassword(passwordEncoder);
        user.confirmEmail();
        user = userService.updateUser(user);

        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(auth);

        return user;
    }
}
