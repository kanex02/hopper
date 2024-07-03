package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.Cosmetic;
import nz.ac.canterbury.seng302.tab.entity.cosmetic.CosmeticType;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.cosmetic.CosmeticService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    UserService userService;

    @Spy
    CosmeticService cosmeticService;

    @InjectMocks
    UserProfileController userProfileController;

    @Test
    void changeBorder_invalidBorderId_borderNotChanged() {
        Mockito.doReturn(new UserEntity()).when(userService).getLoggedInUser();

        userProfileController.changeBorder("qwerty");

        Assertions.assertNull(userService.getLoggedInUser().getBorder());
    }

    @Test
    void changeBorder_emptyBorderId_borderNotChanged() {
        UserEntity user = new UserEntity();
        Cosmetic border = new Cosmetic(1L, "test", CosmeticType.BORDER);
        user.setBorder(border);
        Mockito.doReturn(user).when(userService).getLoggedInUser();

        userProfileController.changeBorder("");

        Assertions.assertEquals(border, userService.getLoggedInUser().getBorder());
    }

    @Test
    void changeBorder_noUser_noErrors() {
        // Test that there are no errors when there is no user logged in.
        // The exact return value is not checked, as Spring Boot should
        // simply throw a 403 unauthorized before this is called.
        Mockito.doReturn(null).when(userService).getLoggedInUser();


        Assertions.assertDoesNotThrow(() -> {
            userProfileController.changeBorder("1");
        });
    }


}