package nz.ac.canterbury.seng302.tab.controller.blog;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import nz.ac.canterbury.seng302.tab.service.blog.InfiniteFeedService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for the infinite blog post feed
 */
@RestController
@RequestMapping("/api/blog")
public class BlogPostRestController {


    private final BlogPostService blogPostService;

    private final UserService userService;
    private final InfiniteFeedService infiniteFeedService;

    public BlogPostRestController(BlogPostService blogPostService, UserService userService, InfiniteFeedService infiniteFeedService) {
        this.blogPostService = blogPostService;
        this.userService = userService;
        this.infiniteFeedService = infiniteFeedService;
    }

    /**
     * End point for retrieving the next set of blog posts
     *
     * @param postsSoFar How many posts the client has requested so far (they keep track of this)
     * @return Returns a JSON string containing a serialized version of the retrieved posts
     */
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getNextBlogPosts(
            @RequestParam(value = "postsSoFar") int postsSoFar,
            @RequestParam(value= "following", defaultValue = "0", required = false) boolean following
    ) {
        UserEntity user = userService.getLoggedInUser();
        List<BlogPost> posts = infiniteFeedService.getNextBlogPostsForUser(user, postsSoFar, following);
        return blogPostService.serializePosts(posts, user);
    }
}
