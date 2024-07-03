package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.user.AccountActivationService;
import nz.ac.canterbury.seng302.tab.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Optional;

/**
 * Controller for register form.
 * Note the @link{Autowired} annotation giving us access to the @lnik{FormService} class automatically
 */
@Controller
public class RegisterFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterFormController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountActivationService accountActivationService;

    private static final String REGISTER_FORM = "registerForm";

    /**
     * Gets form to be displayed
     *
     * @return Returns the Thymeleaf registerForm
     */
    @GetMapping("/register")
    public String form(UserEntity user, Model model) {
        LOGGER.info("GET /register");
        model.addAttribute("userRegister", user);
        model.addAttribute("password", "");
        model.addAttribute("confirmPassword", "");
        return REGISTER_FORM;
    }

    /**
     * Registers the user if all criteria are met
     *
     * @param request         http service request
     * @param user            A user whose fields have been verified by Spring validation
     * @param userResult      A {@link BindingResult} that contains the errors on the entered user data (if present)
     *                        from Spring validation
     * @param password        The user's entered password in PLAINTEXT
     * @param confirmPassword The user's password a second time
     * @param model           The model to add an error message.
     * @return redirect to home page if successfully registered, else refresh register page
     */
    @PostMapping("/register")
    public String submitForm(
            HttpServletRequest request,
            @Valid @ModelAttribute("userRegister") UserEntity user,
            BindingResult userResult,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        LOGGER.info("POST /register");

        if (userService.getUserByEmail(user.getEmail()) != null) {
            userResult.rejectValue("email", "user.email.exists", "Email already exists!");
        }

        Optional<String> inputErrors = PasswordValidator.getInstance().isPasswordValid(user, password, confirmPassword);

        final String PASSWORD_PASSWORD_ERROR = "passwordPasswordError";
        final String PASSWORD_CONFIRM_ERROR = "passwordConfirmError";

        if (inputErrors.isPresent()) {
            switch (inputErrors.get()) {
                case "Password and confirm password cannot be empty." -> {
                    model.addAttribute(PASSWORD_PASSWORD_ERROR, "Password cannot be empty.");
                    model.addAttribute(PASSWORD_CONFIRM_ERROR, "Confirm password cannot be empty.");
                }
                case "Password cannot be empty." -> model.addAttribute(PASSWORD_PASSWORD_ERROR, inputErrors.get());
                case "Confirm password cannot be empty." -> model.addAttribute(PASSWORD_CONFIRM_ERROR, inputErrors.get());
                case "This password is too weak. The password must not contain any other fields from the user profile form, be at least 8 characters long, and contain a variation of different types of characters" -> model.addAttribute(PASSWORD_PASSWORD_ERROR, inputErrors.get());
                case "Passwords do not match." -> model.addAttribute(PASSWORD_CONFIRM_ERROR, inputErrors.get());
                // default shouldn't be possible but just in case
                default -> model.addAttribute(PASSWORD_PASSWORD_ERROR, inputErrors.get());
            }
        }

        if (userResult.hasErrors() || inputErrors.isPresent()) {
            model.addAttribute("userRegister", user);
            return REGISTER_FORM;
        }

        user = userService.updateUser(user);

        user.grantAuthority("ROLE_USER");
        user.hashPassword(this.passwordEncoder);
        user.setFavouriteSports(new HashSet<>());

        Token registrationToken = accountActivationService.createToken(user);

        emailService.sendConfirmationEmail(user, registrationToken);

        return "checkRegistrationEmail";
    }
}
