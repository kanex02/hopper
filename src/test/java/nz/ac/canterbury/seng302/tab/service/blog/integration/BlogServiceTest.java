package nz.ac.canterbury.seng302.tab.service.blog.integration;

import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BlogServiceTest {
    @Autowired
    BlogPostRepository blogPostRepository;

    BlogPostService blogPostService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        this.blogPostService = new BlogPostService(blogPostRepository, null, null, null, null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/resources/sql/blog/post_by_followed_user.sql",
            "src/test/resources/sql/blog/post_by_followed_team.sql",
            "src/test/resources/sql/blog/post_by_followed_club.sql"}
    )
    void givenPostFromFollowedUser_getBlogPostByFollowing_returnsPost(String scriptPath) throws IOException {
        executeScript(scriptPath);
        var posts = blogPostService.getFollowedBlogPosts(userRepository.getUserById(1L));
        assertEquals(2, blogPostRepository.findAllByOrderByDateDesc().size());
        assertEquals(1, posts.size());
    }

    @Test
    @Sql(scripts = {"/sql/blog/filter_posts.sql"})
    void givenPostFromFollowedUserTeamClub_getBlogPostByFollowing_returnsFollowedPosts() {
        var posts = blogPostService.getFollowedBlogPosts(userRepository.getUserById(1L));
        assertEquals(4, blogPostRepository.findAllByOrderByDateDesc().size());
        assertEquals(3, posts.size());
    }

    void executeScript(String scriptPath) throws IOException {
        jdbcTemplate.execute(Files.readString(Paths.get(scriptPath)));
    }
}
