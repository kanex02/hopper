package nz.ac.canterbury.seng302.tab.controller.blog;

import nz.ac.canterbury.seng302.tab.controller.ControllerUtils;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for deleting a post
 */
@Controller
public class DeleteBlogPostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteBlogPostController.class);

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Value("${app.is_local}")
    private boolean isLocal;

    /**
     * Post mapping for deleting a post by the given id
     *
     * @param postId id of the post to delete
     * @return thymeleaf string, 404 page if post does not exist and redirect to homepage if delete is successful
     */
    @PostMapping("/post/delete")
    public String deletePost(
            @RequestParam(name = "postId") Long postId,
            @RequestHeader(value = "referer", required = false) final String referrerURL
    ) {
        LOGGER.info("POST /post/delete");

        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        BlogPost post = blogPostService.findById(postId);

        if (post != null && post.isDeletable()) {
            blogPostService.deletePostById(postId, user);
        } else {
            return "error/404";
        }

        return ControllerUtils.referrerPath(referrerURL, isLocal);
    }
}
