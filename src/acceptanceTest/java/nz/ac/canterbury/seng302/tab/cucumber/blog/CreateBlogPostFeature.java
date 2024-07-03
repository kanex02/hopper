package nz.ac.canterbury.seng302.tab.cucumber.blog;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.blog.CreateBlogPostController;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.IOException;
import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WithMockUser(username = "email@example.com" , roles = "USER")
@SpringBootTest
@DataJpaTest
public class CreateBlogPostFeature {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    CreateBlogPostController createBlogPostController;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogPostRepository blogPostRepository;

    ResultActions result;

    MockMultipartHttpServletRequestBuilder requestBuilder;

    @Given("I am on the \"Create Blog Post\" page")
    public void iAmOnTheCreateBlogPostPage() throws Exception {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        // Make sure that the page works
        mockMvc.perform(
                MockMvcRequestBuilders.get("/blog/create")
        ).andExpect(status().isOk());

        requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart("/blog/create").with(csrf());
    }
    @Given("I enter the title {string}")
    public void iEnterTheTitle(String title) {
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.param("title", title);
    }
    @Given("I enter the description {string}")
    public void iEnterTheDescription(String description) {
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.param("description", description);
    }
    @Given("I upload a video")
    public void iUploadAVideo() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "cat.mp4",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/videos/cat.mp4")
        );
        requestBuilder = requestBuilder.file(file);
    }
    @When("I submit the blog post")
    public void iSubmitTheBlogPost() throws Exception {
        blogPostRepository.deleteAll();
        result = this.mockMvc.perform(requestBuilder);
    }
    @Then("The blog post is created successfully")
    public void theBlogPostIsCreateSuccessfully() throws Exception {
        result.andExpect(status().is3xxRedirection());

        Assertions.assertTrue(blogPostRepository.findAll().iterator().hasNext());
    }

    @And("I set the visibility to {string}")
    public void iSetTheVisibilityTo(String visibility) {
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.param("blogVisibility", visibility);
    }

    @And("I link a challenge")
    public void iLinkAChallenge() {
        Challenge challenge = challengeRepository.findAll().get(0);
        assert challenge.getId() != null;
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.param("linkedChallengeId", challenge.getId().toString());
    }

    @And("I upload an invalid file")
    public void iUploadAnInvalidFile() {
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "cat.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "invalid".getBytes()
        );
        requestBuilder = requestBuilder.file(file);
    }

    @Then("The blog post is not created successfully")
    public void theBlogPostIsNotCreatedSuccessfully() throws Exception {
        Assertions.assertFalse(blogPostRepository.findAll().iterator().hasNext());
    }


    @And("I see an error message for the title")
    public void iSeeAnErrorMessageForTheTitle() throws Exception {
        result.andExpect(model().attributeHasFieldErrors("blogPost", "title"));
    }

    @And("I see an error message for the description")
    public void iSeeAnErrorMessageForTheDescription() throws Exception {
        result.andExpect(model().attributeHasFieldErrors("blogPost", "description"));
    }

    @And("I see an error message for the media")
    public void iSeeAnErrorMessageForTheMedia() throws Exception {
        result.andExpect(flash().attributeExists("imageError"));
    }
}
