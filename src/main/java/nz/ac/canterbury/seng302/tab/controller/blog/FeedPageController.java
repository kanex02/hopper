package nz.ac.canterbury.seng302.tab.controller.blog;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.env.Environment;

@Controller
public class FeedPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedPageController.class);

    private final BlogPostService blogPostService;

    public FeedPageController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Gets the feed page
     *
     * @param model the model
     * @return the feed page
     */
    @GetMapping("/feed") // pog
    public String getFeedPage(Model model) {
        LOGGER.info("GET /feed");
        UserEntity user = (UserEntity) model.getAttribute("user");
        model.addAttribute("allPosts", blogPostService.getAllPostsForUser(user));
        model.addAttribute("tab", "all");
        return "blog/feedMain";
    }

    /**
     * Gets the feed page for following posts
     *
     * @param model the model
     * @return the feed page
     */
    @GetMapping("/feed/following")
    public String getFollowingFeedPage(Model model) {
        LOGGER.info("GET /feed/following");
        UserEntity user = (UserEntity) model.getAttribute("user");
        model.addAttribute("allPosts", blogPostService.getFollowedBlogPosts(user));
        model.addAttribute("tab", "following");
        return "blog/feedMain";
    }
}
