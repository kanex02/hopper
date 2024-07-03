package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.TokenType;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import nz.ac.canterbury.seng302.tab.service.TokenService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import nz.ac.canterbury.seng302.tab.entity.Token;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoginFormIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void createUser() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        String email = "museswipr@gmail.com";

        // create user
        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("password", "SecurePassword123!#%")
                        .param("confirmPassword", "SecurePassword123!#%")
                        .param("firstName", "Download-MuseSwipr")
                        .param("lastName", "On-Steam-Now")
                        .param("email", email)
                        .param("dateOfBirth", "2000-01-01")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
        );

        Optional<Token> token = tokenService.getTokenByUser(userService.getUserByEmail(email), TokenType.REGISTRATION);

        Assertions.assertTrue(token.isPresent());

        mockMvc.perform(
                MockMvcRequestBuilders.get(
                                "/activate/" + token.get()
                                        .getId()
                                        .toString()
                        )
                        .with(csrf())
        );

        verify(emailService, times(1)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    }

    @Test
    void whenLoginAsKnownUser_thenDirectedToUserPage() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .with(csrf())
                                .param("username", "museswipr@gmail.com")
                                .param("password", "SecurePassword123!#%")
                )
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void whenLoginAsUnknownUser_thenErrorIsDisplayed() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .with(csrf())
                                .param("username", "museswipr.is.cool@gmail.com")
                                .param("password", "SecurePassword123!#%")
                )
                .andExpect(redirectedUrlPattern("/login?error=*"));
    }

    @Test
    void whenLoginAsKnownUserWithWrongPassword_thenErrorIsDisplayed() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .with(csrf())
                                .param("username", "museswipr@gmail.com")
                                .param("password", "NotMyPassword123!#%")
                )
                .andExpect(redirectedUrlPattern("/login?error=*"));
    }

    @Test
    void whenLoginWithInvalidEmail_thenErrorIsDisplayed() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .with(csrf())
                                .param("username", "museswipr\\@gmail.com")
                                .param("password", "NotMyPassword123!#%")
                )
                .andExpect(redirectedUrlPattern("/login?error=*"));
    }

}
