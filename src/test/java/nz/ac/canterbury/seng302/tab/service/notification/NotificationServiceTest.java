package nz.ac.canterbury.seng302.tab.service.notification;

import nz.ac.canterbury.seng302.tab.entity.Notification;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        Mockito.reset(notificationRepository);
    }

    @Test
    void notificationEntity_onSave_callsRepositorySave() {
        Notification mockNotification = new Notification();
        notificationService.save(mockNotification);
        verify(notificationRepository, times(1)).save(mockNotification);
    }

    @Test
    void sharedChallengeDetails_onGenerateNotification_assertGeneratedFields() {
        Long challengeId = 1L;
        String message = "Challenge shared!";
        UserEntity relatedUser = new UserEntity();
        List<UserEntity> sharedWithUsers = List.of(new UserEntity());

        notificationService.generateNotificationForSharedChallenge(message, sharedWithUsers, relatedUser, challengeId);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Notification generatedNotification = captor.getValue();

        assertNotNull(generatedNotification);
        assertEquals(message, generatedNotification.getDescription());
        assertEquals(challengeId, generatedNotification.getChallengeId());
    }

}
