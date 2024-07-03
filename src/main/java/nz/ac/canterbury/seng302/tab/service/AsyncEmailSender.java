package nz.ac.canterbury.seng302.tab.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Service class for sending emails asynchronously.
 * <p>
 * This is preferred for sending emails as sending emails is a slow and blocking method.
 */
@Service
public class AsyncEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncEmailSender.class);
    private final JavaMailSender emailSender;

    /**
     * Constructs a new email sender with a constructor injected {@link JavaMailSender}
     * <p>
     * NOTE: You should use {@link org.springframework.beans.factory.annotation.Autowire} or
     * constructor injection for creating this class. Do not invoke this constructor yourself!
     *
     * @param emailSender The {@link JavaMailSender} as a constructor injected field
     */
    public AsyncEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }


    /**
     * Sends an email to the specified email address asynchronously.
     * <p>
     * This is done asynchronously to prevent emailing from blocking the current thread.
     *
     * @param address The email address to send the message to
     * @param ctx     A {@link Context} that describes the message content
     * @return Returns a {@link CompletableFuture<Boolean>} that contains true if the email was sent,
     * and false otherwise.
     */
    @Async("executor")
    public CompletableFuture<Boolean> sendEmail(String address, Context ctx, String subject, String template) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        try {
            helper.setTo(address);
            helper.setSubject(subject);

            templateEngine.addTemplateResolver(this.emailTemplateResolver());

            String html = templateEngine.process(template, ctx);

            helper.setText(html, true);

            emailSender.send(message);

        } catch (MailSendException | MailParseException | MessagingException | MailAuthenticationException e) {
            LOGGER.warn(e.toString());
            // prompt the user to check their email credentials
            return CompletableFuture.completedFuture(false);
        }

        LOGGER.info("Email sent to user");
        return CompletableFuture.completedFuture(true);
    }

    private ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("/templates/emails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setCacheable(false);
        return emailTemplateResolver;
    }

}