package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.user.AccountActivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller for handling account activation links.
 */

@Controller
public class AccountActivationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountActivationController.class);

    private static final String CONFIRMATION_MESSAGE = "Your account is now activated! Please log in now.";

    @Autowired
    private AccountActivationService accountActivationService;


    /**
     * Get mapping for the activation links. Handles valid and invalid links.
     *
     * @param linkedToken        The given token in the url link.
     * @param redirectAttributes attributes to be added when redirecting to the login page.
     * @return 404 page if link is invalid (token expired, or doesn't exist), or login page if valid.
     */
    @GetMapping("/activate/{linkedToken}")
    public String activateAccount(
            @PathVariable UUID linkedToken,
            RedirectAttributes redirectAttributes
    ) {
        LOGGER.info("Account activation request received for token: {}", linkedToken);
        if (accountActivationService.acceptToken(linkedToken)) {
            redirectAttributes.addFlashAttribute("confirmationMessage", CONFIRMATION_MESSAGE);

            return "redirect:/login";
        } else {
            return "error/498";
        }
    }

}
