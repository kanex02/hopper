package nz.ac.canterbury.seng302.tab.controller;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CreateBlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserEntity user;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void setup() {

        user = new UserEntity("password", "firstName", "lastName",
                "email@example.com", "1970-01-01", Set.of(), null);

        user.grantAuthority("ROLE_USER");
        user.hashPassword(this.passwordEncoder);

        user.setBlogPosts(new ArrayList<>());

        userRepository.save(user);

        when(userService.getLoggedInUser()).thenReturn(user);
        when(userService.getUserByEmail(anyString())).thenReturn(user);
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com" , roles = "USER")
    void blogPostWithValidTitleAndDescription_onSave_isSuccessful() throws Exception {
        mockMvc.perform(post("/blog/create")
                .param("title", "Title")
                .param("description", "Description")
                .param("blogVisibility", "PUBLIC")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/feed"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com", roles="USER")
    void blogPostWithValidTitleAndDescriptionAndMedia_onSave_isSuccessful() throws Exception {
        MockMultipartFile media = new MockMultipartFile(
                "mediaUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );
        // Create a MockHttpServletRequestBuilder for the multipart request
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/blog/create")
                .file(media) // Include the media file in the request
                .param("title", "Title")
                .param("description", "Description")
                .param("blogVisibility", "PUBLIC")
                .with(csrf());

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/feed"));
    }

    @ParameterizedTest
    @ValueSource(strings= {
            "",
            "a",
            "aa",
            " ",
            "abcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdefabcdefghijklmnopqrstuvwxyzabcdef",
    })
    @Transactional
    @WithMockUser(username = "email@example.com", roles="USER")
    void blogPostWithInvalidTitle_onSave_isUnsuccessful(String title) throws Exception {
        mockMvc.perform(post("/blog/create")
                .param("title", title)
                .param("description", "Description")
                .param("blogVisibility", "PUBLIC")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/createBlogPost.html"));
    }

    @ParameterizedTest
    @ValueSource(strings= {
            "",
            "a",
            "aa",
            " ",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque semper nunc non neque bibendum, eu sagittis libero gravida. Donec faucibus risus nec ex placerat, sed tincidunt metus lacinia. Ut arcu risus, tempus sed cursus et, facilisis eu nisl. In hac habitasse platea dictumst. Nullam facilisis sapien nisi, sed blandit enim dapibus volutpat. Suspendisse potenti. Suspendisse vitae blandit purus. Cras a scelerisque ex, et efficitur urna. In gravida aliquam vestibulum. Sed vel leo in leo volutpat iaculis. Ut lobortis vulputate tempor. Duis eu lectus rhoncus, fermentum massa id, accumsan velit. Nulla at orci vitae eros condimentum pretium non ultrices dolor.\n" +
                    "\n" +
                    "Curabitur tristique a mauris vel faucibus. Etiam ultricies rutrum eros, et pharetra sem lobortis sed. Nunc sodales venenatis nulla in sagittis. Curabitur porta libero vel augue lobortis, quis volutpat tortor elementum. Integer vehicula elementum augue sed rutrum. Maecenas orci turpis, ultricies a felis eget, mattis sodales dui. Phasellus tristique purus sit amet quam finibus efficitur. Vestibulum ornare justo at enim accumsan, id varius est iaculis. Donec tellus sem, interdum at lectus in, tempus mattis felis. Aliquam leo odio, suscipit eget posuere a, bibendum a purus. Donec suscipit purus metus, eu cursus lacus pulvinar et.\n" +
                    "\n" +
                    "Nam aliquet tempus libero ac ornare. Morbi odio leo, blandit eu tortor et, finibus maximus enim. Etiam tristique elit felis. Vestibulum ac metus id purus commodo lobortis vitae vitae odio. Morbi malesuada gravida quam sit amet dapibus. Aenean at turpis ligula. Integer iaculis blandit purus a venenatis. Sed viverra tortor ac tellus mattis aliquam. Pellentesque dictum tellus eu nisi sodales maximus. Integer sed eros nulla. Etiam rhoncus, leo ac rutrum sodales, urna elit dictum sem, a rhoncus diam dui quis dui. Donec molestie libero vitae enim facilisis, nec porta ex suscipit. Curabitur cursus tellus nec est maximus porttitor. Phasellus mauris ex, sollicitudin placerat dolor vel, iaculis luctus leo. Integer consequat erat odio, porttitor porttitor nibh vestibulum sed.\n" +
                    "\n" +
                    "Interdum et malesuada fames ac ante ipsum primis in faucibus. Nam vitae urna vel felis dignissim hendrerit ut ac dui. Vestibulum nec finibus enim. Suspendisse potenti. Proin purus dolor, efficitur eget dolor ac, ullamcorper lacinia leo. Nam vel rutrum elit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed convallis congue turpis sit amet varius.\n" +
                    "\n" +
                    "In pretium ligula id mauris tristique, quis condimentum mauris fermentum. Vivamus varius quam et lacus pharetra, non tempor metus accumsan. Etiam luctus, felis ut congue posuere, nulla neque blandit purus, nec elementum tellus sapien vel augue. Donec vestibulum enim id cursus sodales. Curabitur sed ex vel magna tempor pellentesque. Aliquam sed sem a justo bibendum ultricies id eget felis. Aenean id faucibus nibh. Maecenas sit amet magna sed neque tincidunt bibendum. Mauris in purus purus.\n" +
                    "\n" +
                    "In suscipit at ligula sed facilisis. Integer in tincidunt diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Mauris facilisis arcu quam, eget consequat ante viverra at. Vivamus tempus massa venenatis leo sodales, nec accumsan augue tincidunt. Ut eu gravida sem. Donec enim massa, gravida et elementum vel, lobortis id orci.\n" +
                    "\n" +
                    "Mauris purus leo, blandit id est at, convallis malesuada diam. Quisque gravida nec mauris in aliquet. Sed sed turpis ultricies, laoreet enim ut, placerat neque. Vivamus eget viverra arcu. In orci aliquam."
    })
    @Transactional
    @WithMockUser(username = "email@example.com", roles="USER")
    void blogPostWithInvalidDescription_onSave_isUnsuccessful(String description) throws Exception {
        mockMvc.perform(post("/blog/create")
                .param("title", "Title")
                .param("description", description)
                .param("blogVisibility", "PUBLIC")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/createBlogPost.html"));
    }

}
