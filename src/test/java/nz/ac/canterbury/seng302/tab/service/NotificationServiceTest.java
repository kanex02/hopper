package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Notification;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.notification.NotificationService;
import org.junit.jupiter.api.Assertions;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    public static final String REDIRECT_400 = "redirect:/400";

    @Mock
    private ChallengeRepository challengeService;

    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    @Mock
    private ClubService clubService;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    void userNotification_redirectNotification_userRedirectUrlReturned() {
        Notification notification = mock(Notification.class);
        UserEntity relatedUser = mock(UserEntity.class);

        when(relatedUser.getId()).thenReturn(1L);
        when(userService.getUserById(Mockito.anyLong())).thenReturn(relatedUser);
        when(notification.getChallengeId()).thenReturn(null);
        when(notification.getRelatedUser()).thenReturn(relatedUser);

        String result = notificationService.redirectNotification(notification);
        Assertions.assertEquals("redirect:/user/1", result);
    }

    @Test
    void teamNotification_redirectNotification_teamRedirectUrlReturned() {
        Notification notification = mock(Notification.class);
        Team relatedTeam = mock(Team.class);

        when(relatedTeam.getId()).thenReturn(2L);
        when(teamService.getTeamById(Mockito.anyLong())).thenReturn(Optional.of(relatedTeam));
        when(notification.getChallengeId()).thenReturn(null);
        when(notification.getRelatedTeam()).thenReturn(relatedTeam);

        String result = notificationService.redirectNotification(notification);
        Assertions.assertEquals("redirect:/teams/2", result);
    }

    @Test
    void clubNotification_redirectNotification_clubRedirectUrlReturned() {
        Notification notification = mock(Notification.class);
        Club relatedClub = mock(Club.class);

        when(relatedClub.getId()).thenReturn(3L);
        when(clubService.findById(Mockito.anyLong())).thenReturn(relatedClub);
        when(notification.getChallengeId()).thenReturn(null);
        when(notification.getRelatedClub()).thenReturn(relatedClub);

        String result = notificationService.redirectNotification(notification);
        Assertions.assertEquals("redirect:/club/3", result);
    }

    @Test
    void defaultNotification_redirectNotification_defaultRedirectUrlReturned() {
        Notification notification = mock(Notification.class);

        when(notification.getChallengeId()).thenReturn(null);
        String result = notificationService.redirectNotification(notification);
        Assertions.assertEquals("redirect:/", result);
    }
    @Test
    void givenInvalidChallenge_whenRedirectNotification_redirectTo400() {
        Mockito.when(challengeService.findChallengeById(Mockito.anyLong())).thenReturn(null);

        Notification notification = new Notification("description", List.of(), null, null, null, 1L, LocalDateTime.now());
        String redirectPath = notificationService.redirectNotification(notification);

        Assertions.assertEquals(REDIRECT_400, redirectPath);
    }

    @Test
    void givenInvalidRelatedUser_whenRedirectNotification_redirectTo400() {
        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(null);
        UserEntity user = new UserEntity();
        user.setId(1L);

        Notification notification = new Notification("description", List.of(), user, null, null, null, LocalDateTime.now());
        String redirectPath = notificationService.redirectNotification(notification);

        Assertions.assertEquals(REDIRECT_400, redirectPath);
    }

    @Test
    void givenInvalidRelatedTeam_whenRedirectNotification_redirectTo400() {
        Mockito.when(teamService.getTeamById(Mockito.anyLong())).thenReturn(Optional.empty());
        Team team = new Team("team");
        team.setId(1L);

        Notification notification = new Notification("description", List.of(), null, null, team, null, LocalDateTime.now());
        String redirectPath = notificationService.redirectNotification(notification);

        Assertions.assertEquals(REDIRECT_400, redirectPath);
    }

    @Test
    void givenInvalidRelatedClub_whenRedirectNotification_redirectTo400() {
        Mockito.when(clubService.findById(Mockito.anyLong())).thenReturn(null);
        Club club = new Club(null, null);
        club.setId(1L);

        Notification notification = new Notification("description", List.of(), null, club, null, null, LocalDateTime.now());
        String redirectPath = notificationService.redirectNotification(notification);

        Assertions.assertEquals(REDIRECT_400, redirectPath);
    }


}
