package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller for global attributes. These make the attributes accessible from every thymeleaf view.
 */
@ControllerAdvice
public class GlobalAttributeController {
    private final UserService userService;

    private final Environment environment;

    /**
     * Constructor for the global attribute controller
     *
     * @param userService the user service
     * @param environment the environment
     */
    public GlobalAttributeController(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;
    }

    @ModelAttribute("user")
    public UserEntity getUser() {
        return userService.getLoggedInUser();
    }

    @ModelAttribute("basePath")
    public String getBasePath() {
        return environment.getProperty("app.baseUrl");
    }
}
