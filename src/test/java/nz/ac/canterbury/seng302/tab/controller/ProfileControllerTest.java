package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileControllerTest {

    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    private UserEntity user;

    public ProfileControllerTest() {
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userService.setPasswordEncoder(passwordEncoder);
        user = new UserEntity("password", "firstName", "lastName", "email@example.com", "1970-01-01", Set.of(), null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        doNothing().when(emailService).sendUpdatePasswordNotificationEmail(any(UserEntity.class));
    }

    @Test
    @WithMockUser
    void handlePasswordChange_shouldRedirect_whenOldPasswordIsCorrectAndPasswordsMatch() throws Exception {
        // Arrange
        Long userId = user.getId();
        String oldPassword = "password";
        String newPassword = "newPassword1!";
        String retypePassword = "newPassword1!";

        // Act & Assert
        mockMvc.perform(post("/editProfile/{userId}/changePassword", userId)
                        .with(csrf())
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("retypePassword", retypePassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/editProfile/%d", userId)));

        verify(emailService, times(1)).sendUpdatePasswordNotificationEmail(any(UserEntity.class));
    }

    @Test
    @WithMockUser
    void handlePasswordChange_shouldNotRedirect_whenOldPasswordIncorrect() throws Exception {
        // Arrange
        Long userId = user.getId();
        String oldPassword = "incorrectPassword";
        String newPassword = "newPassword";
        String retypePassword = "newPassword";

        // Act & Assert
        mockMvc.perform(post("/editProfile/{userId}/changePassword", userId)
                        .with(csrf())
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("retypePassword", retypePassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/editProfile/%d/changePassword", userId)));
    }

    @Test
    @WithMockUser
    void handlePasswordChange_shouldNotRedirect_whenPasswordsDontMatch() throws Exception {
        // Arrange
        Long userId = user.getId();
        String oldPassword = "password";
        String newPassword = "newPassword";
        String retypePassword = "incorrectPassword";

        // Act & Assert
        mockMvc.perform(post("/editProfile/{userId}/changePassword", userId)
                        .with(csrf())
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("retypePassword", retypePassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/editProfile/%d/changePassword", userId)));
    }

    @Test
    @WithMockUser
    void handlePasswordChange_shouldNotUpdatePassword_whenOldPasswordIncorrect() throws Exception {
        // Arrange
        Long userId = user.getId();
        String oldPassword = "incorrectPassword";
        String newPassword = "newPassword";
        String retypePassword = "newPassword";

        // Act & Assert
        mockMvc.perform(post("/editProfile/{userId}/changePassword", userId)
                .with(csrf())
                .param("oldPassword", oldPassword)
                .param("newPassword", newPassword)
                .param("retypePassword", retypePassword));

        assertTrue(userService.checkPasswordMatches(userId, "password"));
    }

    @Test
    @WithMockUser
    void handlePasswordChange_shouldNotUpdatePassword_whenPasswordsDontMatch() throws Exception {
        // Arrange
        Long userId = user.getId();
        String oldPassword = "password";
        String newPassword = "newPassword";
        String retypePassword = "incorrectPassword";

        // Act & Assert
        mockMvc.perform(post("/editProfile/{userId}/changePassword", userId)
                .with(csrf())
                .param("oldPassword", oldPassword)
                .param("newPassword", newPassword)
                .param("retypePassword", retypePassword));
        assertTrue(userService.checkPasswordMatches(userId, "password"));
    }
}