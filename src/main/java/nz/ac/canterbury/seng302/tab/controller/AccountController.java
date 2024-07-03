package nz.ac.canterbury.seng302.tab.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Code largely based on example provided by Morgan English.
 *
 * @author <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/-/tree/master/src/main/java/security/example">Morgan English</a>
 */
@Controller
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * The admin page that only admins can see
     *
     * @param model The {@link Model} of the page
     * @return Returns the path to the admin HTML page
     */
    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        LOGGER.info("GET /admin");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        model.addAttribute("authorities", authentication.getAuthorities());
        return "admin";
    }

    /**
     * Makes the user into an admin
     *
     * @param request {@link HttpServletRequest} to make the user into an admin
     * @return Returns where the user should be redirected to after making this post request
     */
    @PostMapping("make-admin")
    public String giveUserAdminRole(HttpServletRequest request) {
        LOGGER.info("POST /make-admin");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserEntity user = userService.getUserByEmail(currentPrincipalName);
        user.grantAuthority("ROLE_ADMIN");
        userService.updateUser(user);

        // Update current authentication to include new authority (otherwise user will have to log-in again)
        // Create a new Authentication with Username and Password (authorities here are optional as the following
        // function fetches these anyway)
        var token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
        // Authenticate the token properly with the TabAuthenticationProvider
        Authentication authenticationToken = authenticationManager.authenticate(token);
        // Check if the authentication is actually authenticated
        // TODO: any username/password is accepted so this is never false
        if (authenticationToken.isAuthenticated()) {
            // Add the authentication to the current security context (Stateful)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            request.getSession().setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
        }

        return "redirect:/admin";
    }

}