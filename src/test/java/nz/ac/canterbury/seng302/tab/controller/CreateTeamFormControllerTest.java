package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CreateTeamFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file;

    @BeforeEach
    void setup() throws IOException {
        file = new MockMultipartFile(
                "imageUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );

    }

    @Test
    @WithMockUser
    void formWithNoNameShouldShowError() throws Exception {
        this.mockMvc.perform(
                        multipart("/create_team")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "")
                                .param("name", "Purification")
                                .param("city", "Novigrad")
                                .param("country", "Kingdom of Redania")
                )
                .andExpect(model().attributeHasFieldErrors("team"));
    }

    @Test
    @WithMockUser
    void formWithValidDataShouldRedirect() throws Exception {
        this.mockMvc.perform(
                        multipart("/create_team")
                                .file(file)
                                .with(csrf())
                                .param("teamName", "SKT T1")
                                .param("sport", "1")
                                .param("city", "London")
                                .param("country", "UK")
                )
                .andExpect(redirectedUrlPattern("/teams/*"));
    }


}