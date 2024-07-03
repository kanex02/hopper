package nz.ac.canterbury.seng302.tab.service.blog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.controller.api.JsonBlogPost;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogVisibility;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for blog posts and their media
 */
@Service
public class BlogPostService {
    @Autowired
    private ClubService clubService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    String invalidTargetOrProxyErrorMessage = "You must be a manager, coach, or creator of the team / club you are posting for / to.";

    public BlogPostService(BlogPostRepository blogPostRepository, ClubService clubService,
                           TeamService teamService, MediaService mediaService, UserService userService) {
        this.blogPostRepository = blogPostRepository;
        this.clubService = clubService;
        this.teamService = teamService;
        this.mediaService = mediaService;
        this.userService = userService;
    }

    /**
     * Saves a blog post and its media
     * @param blogPost The blog post being created
     * @param file The file to upload
     * @throws MediaService.UploadFailure If the media fails to upload
     */
    public BlogPost saveBlogPost(BlogPost blogPost, MultipartFile file) throws MediaService.UploadFailure {

        UserEntity author = userService.getLoggedInUser();
        blogPost.setAuthor(author);
        author.addBlogPost(blogPost);

        blogPost.setDate(LocalDateTime.now());

        try {
            validate(blogPost, author);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // Adds the blog post to the target club / team entities.
        if (blogPost.getBlogVisibility() == BlogVisibility.CLUB) {
            if (blogPost.getClubTarget() == null) {
                throw new IllegalArgumentException("Club target cannot be null.");
            }
            Club club = blogPost.getClubTarget();
            club.addUserPost(blogPost);
            clubService.save(club);
        } else if (blogPost.getBlogVisibility() == BlogVisibility.TEAM) {
            if (blogPost.getTeamTarget() == null) {
                throw new IllegalArgumentException("Team target cannot be null.");
            }
            Team targetTeam = blogPost.getTeamTarget();
            targetTeam.addUserPost(blogPost);
            teamService.save(targetTeam);
        }

        blogPost.setDeletable(true);
        // Save the blog post into the repository
        blogPost = blogPostRepository.save(blogPost);

        // Save the media, but if it fails, delete the blog post and throw the exception
        try {
            saveMedia(file, blogPost);
        } catch (Exception e) {
            blogPostRepository.delete(blogPost);
            throw e;
        }

        // Update the blog post in the repository
        return blogPostRepository.save(blogPost);
    }

    /**
     * Updates a blog post and its media
     * @param existingPost The blog post being updated
     * @param resultingPost The resulting post
     * @param file The file to upload
     * @throws MediaService.UploadFailure If the media fails to upload
     */
    public void updateBlogPost(BlogPost existingPost, BlogPost resultingPost, MultipartFile file, String deleteMedia) throws MediaService.UploadFailure {

        if (file != null && !file.isEmpty() && deleteMedia.equals("false")) {
            // Try saving media first; if this fails, exception thrown
            saveMedia(file, existingPost);
        }

        if (deleteMedia.equals("true")) {
            existingPost.setFileName(null);
        }

        existingPost.setTitle(resultingPost.getTitle());
        existingPost.setDescription(resultingPost.getDescription());

        if (!existingPost.isChallengeCompletion()) {
            try {
                validate(resultingPost, existingPost.getAuthor());
                existingPost.setChallenge(resultingPost.getChallenge());

                existingPost.setBlogVisibility(resultingPost.getBlogVisibility());

                existingPost.setTeamTarget(resultingPost.getTeamTarget());
                existingPost.setClubTarget(resultingPost.getClubTarget());

                existingPost.setTeamProxy(resultingPost.getTeamProxy());
                existingPost.setClubProxy(resultingPost.getClubProxy());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        // Update the blog post in the repository
        blogPostRepository.save(existingPost);
    }


    /**
     * Blog Post Validator method
     * @param blogPost The blog post to validate
     */
    public void validate(BlogPost blogPost, UserEntity author) {

        validateProxy(blogPost, author);

        validateTarget(blogPost, author);

        validateChallengeWithProxy(blogPost);
    }

    /**
     * Validates the proxy of a blog post
     * @param blogPost The blog post to validate
     * @param author The author of the blog post
     */
    public void validateProxy(BlogPost blogPost, UserEntity author) {
        // Checks that (if proxy present) the proxy group is managed / coached / created by the user
        if (blogPost.getTeamProxy() != null) {
            Team teamProxy = blogPost.getTeamProxy();
            if (!teamProxy.getManagers().contains(author) && !teamProxy.getCoaches().contains(author)) {
                throw new IllegalArgumentException(invalidTargetOrProxyErrorMessage);
            }
        } else if (blogPost.getClubProxy() != null) {
            Club clubProxy = blogPost.getClubProxy();
            if (!clubProxy.getCreator().equals(author)) {
                throw new IllegalArgumentException(invalidTargetOrProxyErrorMessage);
            }
        }
    }

    /**
     * Validates the target of a blog post
     * @param blogPost The blog post to validate
     * @param author The author of the blog post
     */
    public void validateTarget(BlogPost blogPost, UserEntity author) {
        // Checks that (if target present) the target group is managed / coached / created by the user
        if (blogPost.getTeamTarget() != null) {
            Team teamProxy = blogPost.getTeamTarget();
            if (!teamProxy.getManagers().contains(author) && !teamProxy.getCoaches().contains(author)) {
                throw new IllegalArgumentException(invalidTargetOrProxyErrorMessage);
            }
        } else if (blogPost.getClubTarget() != null) {
            Club clubProxy = blogPost.getClubTarget();
            if (!clubProxy.getCreator().equals(author)) {
                throw new IllegalArgumentException(invalidTargetOrProxyErrorMessage);
            }
        }
    }

    /**
     * Validates the challenge of a blog post
     * @param blogPost The blog post to validate
     */
    public void validateChallengeWithProxy(BlogPost blogPost) {
        // Checks that the blog post doesn't have a challenge if it has a team / club proxy
        if ((blogPost.getTeamProxy() != null || blogPost.getClubProxy() != null) && blogPost.getChallenge() != null) {
            throw new IllegalArgumentException("You cannot link a challenge as a team / club.");
        }
    }

    /**
     * Saves the media for a blog post
     * @param file The file to upload
     * @param blogPost The blog post to save the media for
     * @throws MediaService.UploadFailure If the media fails to upload
     */
    private void saveMedia(MultipartFile file, BlogPost blogPost) throws MediaService.UploadFailure {
        if (file != null && !file.isEmpty()) {
            if ("video/mp4".equals(file.getContentType())) {
                mediaService.setBlogPostVideoIfValid(file, blogPost);
            } else {
                mediaService.setBlogPostImageIfValid(file, blogPost);
            }
        }
    }

    public List<BlogPost> getAllPostsForUser(UserEntity user) {
        return blogPostRepository.findAllByOrderByDateDesc().stream()
                .filter(post -> switch (post.getBlogVisibility()) {
                    case PUBLIC -> true;
                    case CLUB -> post.getClubTarget().getUsers().contains(user);
                    case TEAM -> post.getTeamTarget().getAssociatedUsers().contains(user);
                })
                .toList();
    }

    /**
     * Serializes the list of blog posts into a JSON string
     * @param posts The list of posts to serialize
     * @param loggedInUser The current logged-in user
     * @return Returns a JSON list serialized into string form
     */
    public String serializePosts(List<BlogPost> posts, UserEntity loggedInUser) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<JsonBlogPost> jsonBlogPosts = posts.stream()
                .map(rawPost -> new JsonBlogPost(rawPost, loggedInUser))
                .toList();
        try {
            return objectMapper.writeValueAsString(jsonBlogPosts);
        } catch (JsonProcessingException e) {
            return "";
        }

    }

    /**
     * Method to get a specific blog post by a given id.
     * @param id of the blog post to get
     * @return the blog post, else null.
     */
    public BlogPost getBlogPostById(Long id) {
        return blogPostRepository.findById(id).orElse(null);
    }

    /**
     * Method to populate the model with the associated user's created clubs,
     * and managed/coached teams and challenges.
     * @param model to populate
     */
    public void populateModelWithSocialGroupsOptionsAndChallenges(Model model) {

        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Team> teams = Stream.concat(user.getCoachedTeams().stream(), user.getManagedTeams().stream())
                .distinct()
                .toList();

        List<Club> clubs = user.getClubsCreated();


        model.addAttribute("clubOptions", clubs);
        model.addAttribute("teamOptions", teams);
        model.addAttribute("challenges", challengeRepository.findAll());
    }

    /**
     * Delete a post by its id
     *
     * @param id post id
     * @param user user trying to delete the post
     */
    public void deletePostById(Long id, UserEntity user) {
        BlogPost post = blogPostRepository.findById(id).orElse(null);

        if (post != null && post.getAuthor().equals(user)) {
            blogPostRepository.deleteById(id);
        }
    }

    /**
     * Gets the post with the id if it exists
     *
     * @param id id of the post to find
     * @return post if it exists, otherwise null
     */
    public BlogPost findById(Long id) { return blogPostRepository.findById(id).orElse(null); }


    /**
     * Creates and saves a challenge post based on the parameters of the post
     *
     * @param title title of the post
     * @param description description of the post
     * @param author author of the post
     * @param challenge challenge to link to the post
     * @throws IllegalArgumentException if the provided parameters are invalid
     */
    public void createChallengePost(String title, String description, UserEntity author,
                                    Challenge challenge, MultipartFile file) throws MediaService.UploadFailure {

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }

        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }

        if (challenge == null) {
            throw new IllegalArgumentException("Challenge cannot be null.");
        }

        BlogPost blogPost = new BlogPost(title, description, author, LocalDateTime.now());
        blogPost.setChallenge(challenge);
        blogPost.setDeletable(false);
        blogPost.setBlogVisibility(BlogVisibility.PUBLIC);


        blogPostRepository.save(blogPost);

        try {
            saveMedia(file, blogPost);
        } catch (Exception e) {
            blogPostRepository.delete(blogPost);
            throw e;
        }
    }

    /**
     * Gets all blog posts by users, teams, or clubs that the user is following
     * @param user The user to find blog posts for
     * @return A list of blog posts
     */
    public List<BlogPost> getFollowedBlogPosts(UserEntity user) {
        List<BlogPost> allBlogPosts = blogPostRepository.findAllByOrderByDateDesc();
        return allBlogPosts.stream()
                .filter(blogPost ->(
                        // If the blog post is not posted on behalf of a club or team, and the user is following the author
                        blogPost.getClubProxy() == null &&
                                blogPost.getTeamProxy() == null &&
                                user.getFollowing().contains(blogPost.getAuthor()))

                        ||

                        // If the blog post is posted on behalf of a team, and the user is following the team
                        blogPost.getTeamProxy() != null && (blogPost.getTeamProxy().getUsers().contains(user) ||
                                blogPost.getTeamProxy().getTeamFollowers().contains(user))

                        ||

                        // If the blog post is posted on behalf of a club, and the user is following the club
                        blogPost.getClubProxy() != null && (blogPost.getClubProxy().getUsers().contains(user) ||
                                blogPost.getClubProxy().getClubFollowers().contains(user))

                        ||

                        // If it is the user's own post
                        blogPost.getAuthor().equals(user)
                ).toList();
    }
}
