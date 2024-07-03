package nz.ac.canterbury.seng302.tab.controller.blog;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogVisibility;
import nz.ac.canterbury.seng302.tab.service.UserService;
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

@Controller
public class UpdateBlogPostController {

    public static final String MEDIA_VIOLATION = "mediaViolation";
    public static final String CHALLENGE_POST = "challengePost";
    public static final String ERROR_403 = "error/403";
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBlogPostController.class);
    private static final String EDIT_BLOG_FORM_PAGE = "blog/updateBlogPost.html";
    private static final String REDIRECT_EDIT_BLOG_FROM_PAGE = "redirect:/blog/edit";
    private static final String EDIT_CHALLENGE_POST_FORM = "blog/editChallengePost";
    private static final String TARGET = "target";
    private static final String PROXY = "proxy";
    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    /**
     * Get mapping for the edit blog post form.
     * Returns the edit blog post page
     *
     * @param model The model to add attributes to
     * @return the edit blog post page
     */
    @GetMapping("/blog/edit/{blogPostId}")
    public String getEditBlogPostPage(
            Model model,
            @PathVariable("blogPostId") Long blogPostId
    ) {
        LOGGER.info("GET /blog/edit");

        BlogPost blogPost = blogPostService.getBlogPostById(blogPostId);
        UserEntity user = userService.getLoggedInUser();

        if (blogPost == null || !blogPost.getAuthor().equals(user)) {
            return ERROR_403;
        }

        if (blogPost.isChallengeCompletion()) {
            return "redirect:/blog/edit/challenge-post/" + blogPostId;
        }

        model.addAttribute("blogPost", blogPost);
        model.addAttribute("blogPostId", blogPost.getId());
        model.addAttribute("challenge", blogPost.getChallenge());
        model.addAttribute("visibility", blogPost.getBlogVisibility());

        if (blogPost.getTeamTarget() != null) {
            model.addAttribute(TARGET, blogPost.getTeamTarget());
        } else if (blogPost.getClubTarget() != null) {
            model.addAttribute(TARGET, blogPost.getClubTarget());
        }

        if (blogPost.getTeamProxy() != null) {
            model.addAttribute(PROXY, blogPost.getTeamProxy());
        } else if (blogPost.getClubProxy() != null) {
            model.addAttribute(PROXY, blogPost.getClubProxy());
        } else {
            model.addAttribute(PROXY, blogPost.getAuthor());
        }

        model.addAttribute("visibilities", Arrays.asList(BlogVisibility.values()));
        blogPostService.populateModelWithSocialGroupsOptionsAndChallenges(model);
        return EDIT_BLOG_FORM_PAGE;
    }

    /**
     * Get mapping for the edit challenge post form
     *
     * @param model  The model to be used for rendering the view
     * @param postId the id of the post to be edited
     */
    @GetMapping("/blog/edit/challenge-post/{postId}")
    public String getEditChallengePostPage(
            Model model,
            @PathVariable("postId") Long postId
    ) {

        BlogPost challengePost = blogPostService.getBlogPostById(postId);
        UserEntity user = userService.getLoggedInUser();

        LOGGER.info("GET /blog/edit/challenge-post");

        if (challengePost == null || !challengePost.getAuthor().equals(user)) {
            return ERROR_403;
        }

        model.addAttribute(CHALLENGE_POST, challengePost);
        return EDIT_CHALLENGE_POST_FORM;
    }

    /**
     * Updates a blog post
     *
     * @param resultingPost         The post being created
     * @param blogPostBindingResult The binding result for the form
     * @param file                  The file to upload
     * @param model                 The model to add attributes to
     * @return The create blog post page if there is an error, otherwise redirects to the blog page
     */
    @PostMapping("/blog/edit/{blogPostId}")
    public String updateBlogPost(
            @Valid BlogPost resultingPost,
            BindingResult blogPostBindingResult,
            @RequestParam(value = "mediaUpload", required = false) MultipartFile file,
            @RequestParam(value = "deleteMedia", required = true, defaultValue = "false") String deleteMedia,
            @PathVariable("blogPostId") Long blogPostId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        LOGGER.info("POST /blog/edit");

        BlogPost existingBlogPost = blogPostService.getBlogPostById(blogPostId);

        if (existingBlogPost == null || !existingBlogPost.getAuthor().equals(userService.getLoggedInUser())) {
            return ERROR_403;
        }

        final String IMAGE_ERROR = "imageError";

        if (blogPostBindingResult.hasErrors()) {
            blogPostService.populateModelWithSocialGroupsOptionsAndChallenges(model);
            model.addAttribute("visibilities", Arrays.asList(BlogVisibility.values()));
            model.addAttribute("blogPost", resultingPost);
            model.addAttribute("challenge", resultingPost.getChallenge());
            model.addAttribute("visibility", resultingPost.getBlogVisibility());

            if (resultingPost.getTeamTarget() != null) {
                model.addAttribute(TARGET, resultingPost.getTeamTarget());
            } else if (resultingPost.getClubTarget() != null) {
                model.addAttribute(TARGET, resultingPost.getClubTarget());
            }

            if (resultingPost.getTeamProxy() != null) {
                model.addAttribute(PROXY, resultingPost.getTeamProxy());
            } else if (resultingPost.getClubProxy() != null) {
                model.addAttribute(PROXY, resultingPost.getClubProxy());
            }
            return EDIT_BLOG_FORM_PAGE;
        }

        try {
            blogPostService.updateBlogPost(existingBlogPost, resultingPost, file, deleteMedia);
        } catch (MediaService.UploadFailure uploadFailure) {
            // catch the upload failure and add the appropriate error message
            if (Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_IMAGE || Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_VIDEO) {
                redirectAttributes.addFlashAttribute(IMAGE_ERROR, "Invalid file type. Allowed file types are: jpeg, png, svg and mp4.");
            } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                redirectAttributes.addFlashAttribute(IMAGE_ERROR, "Error uploading file: "
                        + uploadFailure.getMessage());
            }
            return REDIRECT_EDIT_BLOG_FROM_PAGE + existingBlogPost.getId();
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Error creating blog post", illegalArgumentException);
            redirectAttributes.addFlashAttribute("teamClubError", illegalArgumentException.getMessage());
            return REDIRECT_EDIT_BLOG_FROM_PAGE + existingBlogPost.getId();
        }
        return "redirect:/feed";
    }

    /**
     * Updates a challenge post
     *
     * @param postId        the id of the post to be edited
     * @param resultingPost The resulting post
     * @param bindingResult The binding result for the form
     * @param media         The media file to upload
     * @param model         The model to be used for rendering the view
     * @return The path to update challenge post page if there is an error,
     * otherwise redirects to the home page
     */
    @PostMapping("/blog/edit/challenge-post/{postId}")
    public String editChallengePost(
            @PathVariable("postId") Long postId,
            @Valid @ModelAttribute("challengePost") BlogPost resultingPost,
            BindingResult bindingResult,
            @RequestParam(value = "mediaUpload", required = false) MultipartFile media,
            Model model
    ) {

        LOGGER.info("POST /blog/edit/challenge-post");

        BlogPost existingPost = blogPostService.getBlogPostById(postId);
        UserEntity user = userService.getLoggedInUser();

        if (existingPost == null || !existingPost.getAuthor().equals(user)) {
            return ERROR_403;
        }

        if (bindingResult.hasErrors()) {
            existingPost.setDescription(resultingPost.getDescription());
            model.addAttribute(CHALLENGE_POST, existingPost);
            model.addAttribute("org.springframework.validation.BindingResult.challengePost", bindingResult);
            return EDIT_CHALLENGE_POST_FORM;
        }

        try {
            blogPostService.updateBlogPost(existingPost, resultingPost, media, "false");
        } catch (MediaService.UploadFailure uploadFailure) {
            // catch the upload failure and add the appropriate error message
            if (Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_IMAGE) {
                model.addAttribute(MEDIA_VIOLATION, uploadFailure.getMessage());
            } else if (uploadFailure.getReason() == MediaService.FailReason.INVALID_VIDEO) {
                model.addAttribute(MEDIA_VIOLATION, uploadFailure.getMessage());
            } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                model.addAttribute(MEDIA_VIOLATION, "Error uploading file: " + uploadFailure.getMessage());
            }
            model.addAttribute(CHALLENGE_POST, existingPost);
            return EDIT_CHALLENGE_POST_FORM;
        }

        return "redirect:/feed";
    }
}