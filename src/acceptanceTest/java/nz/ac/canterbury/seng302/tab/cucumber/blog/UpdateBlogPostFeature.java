package nz.ac.canterbury.seng302.tab.cucumber.blog;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.blog.CreateBlogPostController;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
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
import java.time.LocalDateTime;

import static nz.ac.canterbury.seng302.tab.cucumber.ContextConfiguration.authenticateTestUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "email@example.com" , roles = "USER")
@SpringBootTest
@DataJpaTest
public class UpdateBlogPostFeature {

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

    @Autowired
    private BlogPostService blogPostService;

    private BlogPost blogPost;

    private UserEntity user;

    private String userEmail = "test@example.com";

    ResultActions result;

    MockMultipartHttpServletRequestBuilder requestBuilder;

    private String VALID_TITLE = "A valid title";
    private String VALID_DESCRIPTION = "A valid description";

    @Given("I have a blog post with title {string} and description {string}")
    public void iHaveABlogPost(String title, String description) {
        blogPostRepository.deleteAll();
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        blogPost = new BlogPost(title, description);
        user = userService.getUserByEmail(userEmail);
        blogPost.setAuthor(user);
        blogPostRepository.save(blogPost);
    }

    @Given("I have a challenge blog post with title {string} and description {string}")
    public void iHaveAChallengeBlogPost(String title, String description) {
        authenticateTestUser(authenticationManager, passwordEncoder, userService);
        user = userService.getUserByEmail(userEmail);
        blogPostRepository.deleteAll();
        blogPost = new BlogPost(title, description);

        Challenge challenge = new Challenge(user, LocalDateTime.now().plusDays(1L), "A valid challenge title", "A valid challenge goal", 40);
        challengeRepository.save(challenge);
        blogPost.setChallenge(challenge);

        blogPost.setAuthor(user);
        blogPostRepository.save(blogPost);
    }

    @And("I am the author of the blog post")
    public void iAmTheAuthorOfTheBlogPost() {
        user = userService.getUserByEmail(userEmail);

        blogPost = blogPostRepository.findById(blogPost.getId()).orElse(null);

        Assertions.assertTrue(blogPost.getAuthor().equals(user));
    }

    @And("I am on the edit blog post page")
    public void iAmOnTheEditBlogPostPage() throws Exception {
        // Make sure that the page works
        mockMvc.perform(
                MockMvcRequestBuilders.get("/blog/edit/" + blogPost.getId())
        ).andExpect(status().isOk());

        requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart("/blog/edit/" + blogPost.getId()).with(csrf());
    }

    @When("I enter new information for the blog post")
    public void iEnterNewInformationForTheBlogPost() {
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder
                .with(csrf())
                .param("title", VALID_TITLE)
                .param("description", VALID_DESCRIPTION);
    }

    @And("I click the Edit Post button")
    public void iClickTheEditPostButton() throws Exception {
        result = this.mockMvc.perform(requestBuilder);
    }

    @Then("that blog post is updated")
    public void thatBlogPostIsUpdated() throws Exception {
        result.andExpect(status().is3xxRedirection());

        blogPost = blogPostRepository.findById(blogPost.getId()).orElse(null);

        Assertions.assertEquals("A valid title", blogPost.getTitle());
        Assertions.assertEquals("A valid description", blogPost.getDescription());
    }

    @When("I click the corresponding delete button for that blog post and confirm the deletion")
    public void iClickTheCorrespondingDeleteButtonForThatBlogPostAndConfirmTheDeletion() throws Exception {
        blogPost = blogPostRepository.findById(blogPost.getId()).orElse(null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/post/delete")
                        .with(csrf())
                        .param("postId", String.valueOf(blogPost.getId()))
        );
    }

    @Then("that blog post is deleted")
    public void thatBlogPostIsDeleted() {
        Assertions.assertNull(blogPostRepository.findById(blogPost.getId()).orElse(null));
    }

    @When("I enter invalid title and description for the blog post")
    public void iEnterInvalidInformationForTheBlogPost() {
        String INVALID_TITLE = "A";
        String INVALID_DESCRIPTION = "A";
        requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder
                .with(csrf())
                .param("title", INVALID_TITLE)
                .param("description", INVALID_DESCRIPTION);
    }

    @Then("I see an error message for the invalid fields")
    public void iSeeAnErrorMessageForTheInvalidFields() throws Exception {
        result.andExpect(model().attributeHasFieldErrors("blogPost", "title"));
        result.andExpect(model().attributeHasFieldErrors("blogPost", "description"));
    }

    @And("I am the author of the challenge blog post")
    public void iAmTheAuthorOfTheChallengeBlogPost() {
        user = userService.getUserByEmail(userEmail);
        blogPost.setAuthor(user);

        blogPost = blogPostRepository.findById(blogPost.getId()).orElse(null);

        Assertions.assertTrue(blogPost.getAuthor().equals(user));
    }

    @When("I am on the edit challenge blog post page")
    public void iAmOnTheEditChallengeBlogPostPage() throws Exception {
        // Make sure that the page works
        mockMvc.perform(
                MockMvcRequestBuilders.get("/blog/edit/challenge-post/" + blogPost.getId())
        ).andExpect(status().isOk());

        requestBuilder = (MockMultipartHttpServletRequestBuilder) multipart("/blog/edit/challenge-post/" + blogPost.getId()).with(csrf());
    }

    @Then("I can change the post media")
    public void iCanChangeThePostMedia() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "cat.mp4",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/videos/cat.mp4")
        );
        requestBuilder = requestBuilder.file(file);
        requestBuilder.param("title", VALID_TITLE);
        requestBuilder.param("description", VALID_DESCRIPTION);
    }

    @Then("The post is updated")
    public void thePostIsUpdated() {
        blogPost = blogPostRepository.findById(blogPost.getId()).orElse(null);

        Assertions.assertEquals("cat.mp4", blogPost.getFileName());
    }
}
