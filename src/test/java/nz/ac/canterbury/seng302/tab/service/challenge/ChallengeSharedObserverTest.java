package nz.ac.canterbury.seng302.tab.service.challenge;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.notification.ChallengeSharedEvent;
import nz.ac.canterbury.seng302.tab.service.notification.ChallengeSharedObserver;
import nz.ac.canterbury.seng302.tab.service.notification.NotificationEvent;
import nz.ac.canterbury.seng302.tab.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeSharedObserverTest {

    @Mock
    private NotificationService mockNotificationService;


    @InjectMocks
    private ChallengeSharedObserver challengeSharedObserver;

    @Test
    void challengeSharedEvent_onHandleEvent_assertNotificationServiceCall() {
        Long challengeId = 1L;
        UserEntity relatedUser = new UserEntity();
        List<UserEntity> invitedUsers = List.of(new UserEntity());

        ChallengeSharedEvent challengeSharedEvent = new ChallengeSharedEvent();
        challengeSharedEvent.setChallengeId(challengeId);
        challengeSharedEvent.setInvitedUsers(invitedUsers);
        challengeSharedEvent.setRelatedUser(relatedUser);

        challengeSharedObserver.handleEvent(challengeSharedEvent);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> userCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<UserEntity> relatedUserCaptor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<Long> challengeIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(mockNotificationService, times(1)).generateNotificationForSharedChallenge(messageCaptor.capture(), userCaptor.capture(), relatedUserCaptor.capture(), challengeIdCaptor.capture());

        assertEquals("shared a challenge", messageCaptor.getValue());
        assertEquals(invitedUsers, userCaptor.getValue());
        assertEquals(relatedUser, relatedUserCaptor.getValue());
        assertEquals(challengeId, challengeIdCaptor.getValue());
    }

    @Test
    void nonChallengeSharedEvent_onHandleEvent_noInteractionWithNotificationService() {
        NotificationEvent someOtherEvent = mock(NotificationEvent.class);

        challengeSharedObserver.handleEvent(someOtherEvent);

        verifyNoInteractions(mockNotificationService);
    }

}
