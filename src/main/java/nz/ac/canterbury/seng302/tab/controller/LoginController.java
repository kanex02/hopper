package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Provides serverside endpoints for logging in
 */
@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    /**
     *
     * @return Returns the filepath to the login page HTML
     */
    @GetMapping("/login")
    public String getLoginPage(Model model) {
        LOGGER.info("GET /login");
        // querying the database every time is definitely not the best way to do this
        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        return "login";
    }
}
