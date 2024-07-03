package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

/**
 * Service for sending emails to the user
 */
@Service
public class EmailService {

    private final AsyncEmailSender asyncSender;

    @Value("${app.baseUrl}")
    private String baseUrl;

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    /**
     * Constructor for the EmailService. Configures the AsyncEmailSender for future use.
     * @param asyncSender AsyncEmailSender that is used to send emails asynchronously.
     */
    public EmailService(AsyncEmailSender asyncSender) {
        this.asyncSender = asyncSender;
    }

    /**
     * Sends a registration confirmation email to the user
     *
     * @param user  The user to send the email to
     * @param token The user's registration token to send
     */
    public void sendConfirmationEmail(UserEntity user, Token token) {
        Context context = new Context();
        context.setVariable(FIRST_NAME, user.getFirstName());
        context.setVariable(LAST_NAME, user.getLastName());

        context.setVariable("activation_link", baseUrl + "activate/" + token.getId());

        this.asyncSender.sendEmail(user.getEmail(), context, "Registration Confirmation", "confirmation-email");
    }

    /**
     * Sends a password reset link to user
     *
     * @param user The user to send the email to
     */
    public void sendPasswordResetLinkEmail(UserEntity user, Token resetPasswordToken) {
        Context context = new Context();
        context.setVariable(FIRST_NAME, user.getFirstName());
        context.setVariable(LAST_NAME, user.getLastName());
        context.setVariable("resetPasswordLink", baseUrl + "resetPassword/" + resetPasswordToken.getId());
        this.asyncSender.sendEmail(user.getEmail(), context, "Reset Password Link", "password-reset-link-email");
    }

    /**
     * Sends a password change confirmation to the user
     *
     * @param user The user to send email to
     */
    public void sendResetPasswordConfirmationEmail(UserEntity user) {
        Context context = new Context();
        context.setVariable(FIRST_NAME, user.getFirstName());
        context.setVariable(LAST_NAME, user.getLastName());
        this.asyncSender.sendEmail(user.getEmail(), context, "Password Reset Confirmation", "password-reset-confirmation-email");
    }

    /**
     * Sends an email notifying password change to the user
     *
     * @param user The user to send the email to
     */
    public void sendUpdatePasswordNotificationEmail(UserEntity user) {
        Context context = new Context();
        context.setVariable(FIRST_NAME, user.getFirstName());
        context.setVariable(LAST_NAME, user.getLastName());
        this.asyncSender.sendEmail(user.getEmail(), context, "Password Update Notification", "password-update-notification-email");
    }
}
