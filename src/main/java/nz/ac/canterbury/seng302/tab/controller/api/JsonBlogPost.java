package nz.ac.canterbury.seng302.tab.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * JSON representation of {@link BlogPost}. Used for the infinite feed, and only contains a minimal amount
 * of information that is needed to display to the user.
 *
 * @see BlogPost
 * @see nz.ac.canterbury.seng302.tab.service.blog.InfiniteFeedService
 */
public class JsonBlogPost {

    @JsonProperty
    private final long blogPostId;

    @JsonProperty
    private final String title;

    @JsonProperty
    private final String content;

    @JsonProperty
    private final JsonUser author;

    @JsonProperty
    private final JsonTeam proxyTeam;

    @JsonProperty
    private final JsonClub proxyClub;

    @JsonProperty
    private final String date;

    @JsonProperty
    private final String filePath;

    @JsonProperty
    private final boolean editable;

    @JsonProperty
    private final boolean deleteable;

    @JsonProperty
    private final String mediaType;

    @JsonProperty
    @Nullable
    private final JsonChallenge challenge;

    public JsonBlogPost(BlogPost blogPost, UserEntity loggedInUser) {
        this.blogPostId = blogPost.getId();
        this.title = blogPost.getTitle();
        this.content = blogPost.getDescription();
        this.author = new JsonUser(blogPost.getAuthor());
        if (blogPost.getClubProxy() == null) {
            this.proxyClub = null;
        } else {
            this.proxyClub = new JsonClub(blogPost.getClubProxy());
        }
        if (blogPost.getTeamProxy() == null) {
            this.proxyTeam = null;
        } else {
            this.proxyTeam = new JsonTeam(blogPost.getTeamProxy());
        }
        this.date = blogPost.getFormattedTimeSincePost();
        this.filePath = blogPost.getFilePath();
        this.editable = Objects.equals(blogPost.getAuthor().getId(), loggedInUser.getId());
        this.deleteable = editable && blogPost.isDeletable();
        this.mediaType = blogPost.getMediaType();

        Challenge linkedChallenge = blogPost.getChallenge();
        this.challenge = linkedChallenge == null
                ? null
                : new JsonChallenge(linkedChallenge);

    }

    public long getBlogPostId() {
        return blogPostId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public JsonUser getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isDeleteable() {
        return deleteable;
    }

    public JsonTeam getProxyTeam() {
        return proxyTeam;
    }

    public JsonClub getProxyClub() {
        return proxyClub;
    }
}
