package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Notification;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.notification.NotificationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller to handle to redirection of links
 * tp specific related entities (Users, Teams, Clubs, and Challenges).
 */
@Controller
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    private static final String REDIRECT_HOME = "redirect:/";

    @GetMapping("/notification/{id}")
    public String redirectNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        LOGGER.info("GET /notification/{}", id);

        UserEntity user = userService.getLoggedInUser();

        if (user == null) {
            // User not logged in
            return REDIRECT_HOME;
        }

        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            // Invalid id
            return REDIRECT_HOME;
        }

        // Check if the user is allowed to view the notification
        if (!notification.getNotifiedUsers().contains(user)) {
            return REDIRECT_HOME;
        }

        // Mark the notification as read for the current user
        notification.readNotification(user);
        notificationService.save(notification);

        String redirectUrl = notificationService.redirectNotification(notification);

        if (redirectUrl.equalsIgnoreCase("redirect:/challenge/view")) {
            redirectAttributes.addAttribute("notificationChallengeId", notification.getChallengeId());
        }

        return redirectUrl;
    }

    /**
     * Method to handle the redirection of a notification to the related entity
     * @param id the id of the notification to redirect from
     * @return the url to redirect to
     */
    @GetMapping("/notification/relatedEntity/{id}")
    public String redirectNotificationRelatedEntity(@PathVariable Long id) {
        LOGGER.info("GET /notification/relatedEntity/{}", id);

        UserEntity user = userService.getLoggedInUser();

        if (user == null) {
            // User not logged in
            return REDIRECT_HOME;
        }

        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            // Invalid id
            return REDIRECT_HOME;
        }

        // Check if the user is allowed to view the notification
        if (!notification.getNotifiedUsers().contains(user)) {
            return REDIRECT_HOME;
        }

        // Mark the notification as read for the current user
        notification.readNotification(user);
        notificationService.save(notification);

        return notificationService.redirectNotificationToRelatedEntity(notification);
    }
}
