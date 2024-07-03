package nz.ac.canterbury.seng302.tab.service.search.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class SearchUserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    "a",
                    "b",
                    "ab",
                    "AB",
                    "12"
            }
    )
    @WithMockUser
    void whenSearchLessThan3Chars_thenFlashErrorIsDisplayed(String search) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/all-profiles")
                                .with(csrf())
                                .param("name", search)
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/all-profiles"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Aaa",
                    "THIS IS A VERY LONNNNG STRINNGGGG",
                    "3cs",
                    "4chr",
                    "chr4",
                    "cs3",
                    "Searching..."
            }
    )
    @WithMockUser
    void whenSearch3OrMoreChars_thenNoErrorIsReturned(String search) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/all-profiles")
                                .with(csrf())
                                .param("name", search)
                )
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }

}
