package nz.ac.canterbury.seng302.tab.service.blog;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for the infinite feed feature of the blog posts page
 */
@Service
public class InfiniteFeedService {


    static final int POSTS_PER_REQUEST = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteFeedService.class);

    private final BlogPostService blogPostService;

    private final Map<Long, List<BlogPost>> allPostsCache = new ConcurrentHashMap<>();

    private final Map<Long, List<BlogPost>> followingPostsCache = new ConcurrentHashMap<>();

    public InfiniteFeedService(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Gets the next set of blog posts for the user.
     *
     * @param user       The user to get blog posts for
     * @param postsSoFar Returns the number of posts that have been retrieved for this user so far
     *                   (clients track this)
     * @return Returns and unmodifiable list of blog posts
     */
    public List<BlogPost> getNextBlogPostsForUser(UserEntity user, int postsSoFar, boolean following) {

        long userID = user.getId();

        Map<Long, List<BlogPost>> cache = following ? followingPostsCache : allPostsCache;

        if (postsSoFar <= 0) {
            LOGGER.debug("Refreshing posts cache");
            cache.remove(userID);
        }

        List<BlogPost> entry;

        if (following) {
            entry = cache.computeIfAbsent(userID, uid -> blogPostService.getFollowedBlogPosts(user));
        } else {
            entry = cache.computeIfAbsent(userID, uid -> blogPostService.getAllPostsForUser(user));
        }

        if (postsSoFar >= entry.size()) {
            LOGGER.debug("Reached the end of infinity");
            return Collections.emptyList();
        }

        List<BlogPost> posts = entry.stream()
                .skip(postsSoFar)
                .limit(POSTS_PER_REQUEST)
                .toList();

        LOGGER.info("Retrieved {} posts for user", posts.size());

        return posts;
    }

}
