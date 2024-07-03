package nz.ac.canterbury.seng302.tab.service.notification;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Notification;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.NotificationRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for the notifications
 */
@Service
public class NotificationService {

    public static final String REDIRECT_400 = "redirect:/400";
    Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final ChallengeRepository challengeService;

    private final UserService userService;

    private final TeamService teamService;

    private final ClubService clubService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               ChallengeRepository challengeService,
                               UserService userService,
                               TeamService teamService,
                               ClubService clubService) {
        this.notificationRepository = notificationRepository;
        this.challengeService = challengeService;
        this.userService = userService;
        this.teamService = teamService;
        this.clubService = clubService;
    }

    /**
     * Method to return the Notification with a given id, or null
     *
     * @param id to search for
     * @return Notification, or null
     */
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    /**
     * Method to handle the redirection of a notification
     *
     * @param notification the notification to redirect from
     * @return the url to redirect to
     */
    public String redirectNotification(Notification notification) {
        String redirectUrl = redirectNotificationToRelatedEntity(notification);

        Long challengeId = notification.getChallengeId();

        // If it is a notification for a challenge and the challenge exists, redirect to the challenge page.
        if (challengeId != null) {
            if (challengeService.findChallengeById(challengeId) != null) {
                redirectUrl = "redirect:/challenge/view";
            } else {
                logger.error("Challenge with id {} not found", challengeId);
                redirectUrl = REDIRECT_400;
            }
        }

        return redirectUrl;
    }

    /**
     * Method to handle the redirection of a notification to the related entity
     * @param notification the notification to redirect from
     * @return the url to redirect to
     */
    public String redirectNotificationToRelatedEntity(Notification notification) {
        String redirectUrl;

        // Redirect to the related entity page.
        if (notification.getRelatedUser() != null) {

            Long userId = notification.getRelatedUser().getId();
            if (userId != null && userService.getUserById(userId) != null) {
                redirectUrl = "redirect:/user/" + userId;
            } else {
                logger.error("User with id {} not found", userId);
                redirectUrl = REDIRECT_400;
            }

        } else if (notification.getRelatedTeam() != null) {

            Long teamId = notification.getRelatedTeam().getId();
            if (teamId != null && teamService.getTeamById(teamId).isPresent()) {
                redirectUrl = "redirect:/teams/" + teamId;
            } else {
                logger.error("Team with id {} not found", teamId);
                redirectUrl = REDIRECT_400;
            }

        } else if (notification.getRelatedClub() != null) {

            Long clubId = notification.getRelatedClub().getId();
            if (clubId != null && clubService.findById(clubId) != null) {
                redirectUrl = "redirect:/club/" + clubId;
            } else {
                logger.error("Club with id {} not found", clubId);
                redirectUrl = REDIRECT_400;
            }

        } else {
            redirectUrl = "redirect:/";
        }

        return redirectUrl;
    }

    /**
     * Method to save a notification
     *
     * @param notification to save
     */
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    /**
     * Method to generate a notification for a shared challenge
     *
     * @param message     to display
     * @param challengeId the id of the challenge
     */
    @Transactional
    public void generateNotificationForSharedChallenge(String message, List<UserEntity> sharedWithUsers, UserEntity relatedUser, Long challengeId) {
        Notification notification = new Notification(message, sharedWithUsers, relatedUser, null, null, challengeId, LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
