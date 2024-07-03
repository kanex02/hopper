package nz.ac.canterbury.seng302.tab.controller.blog;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogVisibility;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Objects;

/**
 * Controller for the create blog post page
 */
@Controller
public class CreateBlogPostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateBlogPostController.class);

    private static final String BLOG_FORM_PAGE = "blog/createBlogPost.html";

    private static final String REDIRECT_BLOG_FORM_PAGE = "redirect:/blog/create";

    @Autowired
    private BlogPostService blogPostService;


    /**
     * Returns the create blog post page
     *
     * @param model The model to add attributes to
     * @return The create blog post page
     */
    @GetMapping("/blog/create")
    public String getCreateBlogPostPage(Model model) {
        LOGGER.info("GET /blog/create");
        model.addAttribute("blogPost", new BlogPost("", ""));
        blogPostService.populateModelWithSocialGroupsOptionsAndChallenges(model);
        model.addAttribute("visibilities", Arrays.asList(BlogVisibility.values()));
        return BLOG_FORM_PAGE;
    }

    /**
     * Creates a blog post
     *
     * @param blogPost              The post being created
     * @param blogPostBindingResult The binding result for the form
     * @param file                  The file to upload
     * @param model                 The model to add attributes to
     * @return The create blog post page if there is an error, otherwise redirects to the blog page
     */
    @PostMapping("/blog/create")
    public String createBlogPost(
            @Valid BlogPost blogPost,
            BindingResult blogPostBindingResult,
            @RequestParam(value = "mediaUpload", required = false) MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        LOGGER.info("POST /blog/create");
        final String IMAGE_ERROR = "imageError";

        if (blogPostBindingResult.hasErrors()) {
            blogPostService.populateModelWithSocialGroupsOptionsAndChallenges(model);
            model.addAttribute("visibilities", Arrays.asList(BlogVisibility.values()));
            model.addAttribute("blogPost", blogPost);
            model.addAttribute("challenge", blogPost.getChallenge());
            model.addAttribute("visibility", blogPost.getBlogVisibility());

            if (blogPost.getTeamTarget() != null) {
                model.addAttribute("target", blogPost.getTeamTarget());
            } else if (blogPost.getClubTarget() != null) {
                model.addAttribute("target", blogPost.getClubTarget());
            }

            if (blogPost.getTeamProxy() != null) {
                model.addAttribute("proxy", blogPost.getTeamProxy());
            } else if (blogPost.getClubProxy() != null) {
                model.addAttribute("proxy", blogPost.getClubProxy());
            }
            return BLOG_FORM_PAGE;
        }

        try {
            blogPostService.saveBlogPost(blogPost, file);
        } catch (MediaService.UploadFailure uploadFailure) {
            // catch the upload failure and add the appropriate error message
            if (Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_IMAGE || Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_VIDEO) {
                redirectAttributes.addFlashAttribute(IMAGE_ERROR, "Invalid file type. Allowed file types are: jpeg, png, svg and mp4.");
            } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                redirectAttributes.addFlashAttribute(IMAGE_ERROR, "Error uploading file: "
                        + uploadFailure.getMessage());
            }
            return REDIRECT_BLOG_FORM_PAGE;
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Error creating blog post", illegalArgumentException);
            redirectAttributes.addFlashAttribute("teamClubError", illegalArgumentException.getMessage());
            return REDIRECT_BLOG_FORM_PAGE;
        }

        return "redirect:/feed";
    }
}
