package nz.ac.canterbury.seng302.tab.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.user.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling password reset links.
 */

@Controller
@Validated
public class PasswordResetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Get mappping for the send reset password email form page when an error occurs
     *
     * @param model The Model of the page
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String resetPassEmailPage(Model model, ConstraintViolationException e) {
        LOGGER.info("GET /resetEmailPage");
        // querying the database every time is definitely not the best way to do this
        if(e != null){
            model.addAttribute("errorMessage", "Must be a well-formed email address");
        }
        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        return "resetPassEmail";
    }

    /**
     * Get mappping for the send reset password email form page
     *
     * @param model The Model of the page
     */
    @GetMapping("/resetPassEmail")
    public String resetPassEmailPage(Model model) {
        LOGGER.info("GET /resetEmailPage");
        // querying the database every time is definitely not the best way to do this
        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        return "resetPassEmail";
    }

    /**
     * Post mapping for the password reset links.
     *
     * @param model The Model of the page
     * @param email of the user to send the reset link to
     */
    @PostMapping("/resetPassEmail")
    public String submitForgotPasswordForm(
            Model model,
            @RequestParam(name = "enterEmail")
            @Email(
                    regexp = "^[\\p{L}\\p{N}.!#$%&'*+/=?^_`{|}~-]+@[\\p{L}\\p{N}]{1,62}(\\.[\\p{L}\\p{N}]{1,62})+$"
            )
            String email
    ) {
        LOGGER.info("POST /resetPassEmail");
        UserEntity user = userService.getUserByEmail(email);
        if (user != null) {
            passwordResetService.createPasswordResetToken(user);
        }
        return "checkResetPasswordEmail";
    }

    /**
     * Get mapping for the reset password page
     *
     * @param model The Model of the page
     * @param linkedToken the id of the user's token for reset
     */
    @GetMapping("/resetPassword/{linkedToken}")
    public String resetPasswordPage(
            Model model,
            @PathVariable UUID linkedToken
    ) {
        LOGGER.info("GET /resetPassword");

        Optional<UserEntity> userResult = passwordResetService.acceptToken(linkedToken);
        if (userResult.isPresent()) {
            model.addAttribute("resetPasswordTokenId", linkedToken);
            return "resetPassword";
        }
        return "error/498";
    }

    /**
     * Post mapping for the reset password page
     *
     * @param tokenId the id of the user's token for reset
     * @param newPassword the new entered password
     * @param retypePassword the retyped password to check against the new entered password
     */
    @PostMapping("/resetPassword")
    public String handlePasswordReset(
            @RequestParam("resetPasswordTokenId") UUID tokenId,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("retypePassword") String retypePassword,
            RedirectAttributes redirectAttributes
    ) throws IllegalArgumentException {

        Optional<String> inputErrors = passwordResetService.tryPasswordReset(tokenId, newPassword, retypePassword);
        if (inputErrors.isPresent()) {
            LOGGER.info("Unable to reset password with error {}", inputErrors.get());
            redirectAttributes.addFlashAttribute("errorMessage", inputErrors.get());
            return "redirect:/resetPassword/" + tokenId.toString();
        }
        LOGGER.info("Successfully reset password");
        // Redirect the user to edit profile page and display a success message
        redirectAttributes.addFlashAttribute("confirmationMessage", "Password updated successfully");
        return "redirect:/login";
    }
}
