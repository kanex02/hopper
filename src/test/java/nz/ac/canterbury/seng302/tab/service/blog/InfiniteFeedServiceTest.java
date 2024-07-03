package nz.ac.canterbury.seng302.tab.service.blog;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InfiniteFeedServiceTest {


    private static final int MAX_POST_COUNT = InfiniteFeedService.POSTS_PER_REQUEST;
    private static final int EXCESS_POST_COUNT = InfiniteFeedService.POSTS_PER_REQUEST / 2;
    private static final int TOTAL_POST_COUNT = MAX_POST_COUNT + EXCESS_POST_COUNT;

    private final UserEntity user = new UserEntity();
    private BlogPostService mockBlogPostService;
    private InfiniteFeedService infiniteFeedService;

    private List<BlogPost> posts;

    @BeforeEach
    void setup() {
        user.setId(1L);
        mockBlogPostService = Mockito.mock(BlogPostService.class);
        infiniteFeedService = new InfiniteFeedService(mockBlogPostService);

        var post = new BlogPost("Title", "Content", user, LocalDateTime.now());
        posts = new ArrayList<>();
        for (int i = 0; i < TOTAL_POST_COUNT; i++) {
            posts.add(post);
        }
    }

    @Test
    void retrievedNoPosts_onGetNextPosts_returnsMaxPosts() {
        Mockito.when(mockBlogPostService.getAllPostsForUser(user))
                .thenReturn(posts);

        List<BlogPost> nextPosts = infiniteFeedService.getNextBlogPostsForUser(
                user, 0, false
        );

        Assertions.assertEquals(MAX_POST_COUNT, nextPosts.size());
    }

    @Test
    void retrieved1Post_onGetNextPosts_returnsMaxPosts() {
        Mockito.when(mockBlogPostService.getAllPostsForUser(user))
                .thenReturn(posts);

        List<BlogPost> nextPosts = infiniteFeedService.getNextBlogPostsForUser(
                user, 1, false
        );

        Assertions.assertEquals(MAX_POST_COUNT, nextPosts.size());
    }

    @Test
    void retrievedMaxPosts_onGetNextPosts_returnsExcessPosts() {
        Mockito.when(mockBlogPostService.getAllPostsForUser(user))
                .thenReturn(posts);

        List<BlogPost> nextPosts = infiniteFeedService.getNextBlogPostsForUser(
                user, MAX_POST_COUNT, false
        );

        Assertions.assertEquals(EXCESS_POST_COUNT, nextPosts.size());
    }

    @Test
    void retrievedAllPosts_onGetNextPosts_returnsEmptyList() {
        Mockito.when(mockBlogPostService.getAllPostsForUser(user))
                .thenReturn(posts);

        List<BlogPost> nextPosts = infiniteFeedService.getNextBlogPostsForUser(
                user, TOTAL_POST_COUNT, false
        );

        Assertions.assertTrue(nextPosts.isEmpty());
    }

}
