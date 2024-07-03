package nz.ac.canterbury.seng302.tab.service.blog;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogVisibility;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

class BlogServiceTest {
    private BlogPostRepository blogPostRepositoryMock;
    private ClubService clubServiceMock;
    private TeamService teamServiceMock;
    private MediaService mediaServiceMock;
    private UserEntity userEntityMock;
    private BlogPostService blogPostService;

    private Challenge challenge;

    private MultipartFile file;

    private Team team;

    private Club club;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        challenge = new Challenge(userEntityMock, LocalDateTime.now(), "Challenge", "Goal", 30);
        blogPostRepositoryMock = Mockito.mock(BlogPostRepository.class);
        clubServiceMock = Mockito.mock(ClubService.class);
        teamServiceMock = Mockito.mock(TeamService.class);
        mediaServiceMock = Mockito.mock(MediaService.class);
        userEntityMock = Mockito.mock(UserEntity.class);
        UserService userServiceMock = Mockito.mock(UserService.class);

        team = new Team("Test");
        club = new Club("Club", "Test");
        Set<UserEntity> authorSet = new HashSet<>();
        authorSet.add(userEntityMock);
        team.setManagers(authorSet);
        club.setCreator(userEntityMock);

        Mockito.doReturn(userEntityMock).when(userServiceMock).getLoggedInUser();

        String fileName = "images/valid_image.png";
        var location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] pngImageData = Files.readAllBytes(Path.of(location.toURI()));
        file = new MockMultipartFile(fileName, fileName, "image/png", pngImageData);
        userServiceMock = Mockito.mock(UserService.class);

        Mockito.doReturn(userEntityMock).when(userServiceMock).getLoggedInUser();

        blogPostService = new BlogPostService(blogPostRepositoryMock, clubServiceMock,
                teamServiceMock, mediaServiceMock, userServiceMock);

    }
    @Test
    void publicPostWithImage_onSave_isSuccessful() throws MediaService.UploadFailure, IOException {
        BlogPost blogPost = new BlogPost("Title", "Description");
        blogPost.setBlogVisibility(BlogVisibility.PUBLIC);
        MultipartFile media = new MockMultipartFile(
                "mediaUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );

        blogPostService.saveBlogPost(blogPost, media);

        Mockito.verify(blogPostRepositoryMock, Mockito.times(2)).save(Mockito.any());
    }
    @Test
    void publicPostWithVideo_onSave_isSuccessful() throws MediaService.UploadFailure, IOException {
        BlogPost blogPost = new BlogPost("Title", "Description");
        blogPost.setBlogVisibility(BlogVisibility.PUBLIC);
        MockMultipartFile media = new MockMultipartFile(
                "mediaUpload",
                "cat.mp4",
                "video/mp4",
                this.getClass().getResourceAsStream("/videos/cat.mp4")
        );

        blogPostService.saveBlogPost(blogPost, media);

        Mockito.verify(blogPostRepositoryMock, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void publicPostWithInvalidImage_onSave_throwsError() throws MediaService.UploadFailure, IOException {
        BlogPost blogPost = new BlogPost("Title", "Description");
        blogPost.setBlogVisibility(BlogVisibility.PUBLIC);
        MultipartFile media = new MockMultipartFile(
                "mediaUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );

        Mockito.doThrow(new MediaService.UploadFailure(MediaService.FailReason.UPLOAD_ERROR))
                .when(mediaServiceMock).setBlogPostImageIfValid(Mockito.any(), Mockito.any());

        Assertions.assertThrows(MediaService.UploadFailure.class, () -> blogPostService.saveBlogPost(blogPost, media));
    }


    @Test
    void blogPostToTeam_onSave_isSuccessful() throws MediaService.UploadFailure {
        BlogPost blogPost = new BlogPost("Title", "Description");
        blogPost.setBlogVisibility(BlogVisibility.TEAM);
        blogPost.setAuthor(userEntityMock);
        blogPost.setTeamTarget(team);

        Mockito.doReturn(Optional.of(team)).when(teamServiceMock).getTeamById(Mockito.any());
        Mockito.doReturn(new BlogPost()).when(blogPostRepositoryMock).save(Mockito.any());

        Optional<Team> optionalTeam = teamServiceMock.getTeamById(1L);
        optionalTeam.ifPresent(blogPost::setTeamTarget);

        blogPostService.saveBlogPost(blogPost, null);

        Mockito.verify(teamServiceMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(blogPostRepositoryMock, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void blogPostToClub_onSave_isSuccessful() throws MediaService.UploadFailure {
        BlogPost blogPost = new BlogPost("Title", "Description");
        blogPost.setBlogVisibility(BlogVisibility.CLUB);
        blogPost.setAuthor(userEntityMock);
        blogPost.setClubTarget(club);

        Mockito.doReturn(club).when(clubServiceMock).findById(Mockito.any());
        Mockito.doReturn(new BlogPost()).when(blogPostRepositoryMock).save(Mockito.any());

        Club club = clubServiceMock.findById(1L);
        blogPost.setClubTarget(club);

        blogPostService.saveBlogPost(blogPost, null);

        Mockito.verify(clubServiceMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(blogPostRepositoryMock, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void blogPostWithChallenge_onSave_isSuccessful() throws MediaService.UploadFailure {
        BlogPost blogPost = new BlogPost("Title", "Description");
        Challenge challenge = new Challenge(userEntityMock, LocalDateTime.now(), "Challenge title", "Challenge goal", 40);
        blogPost.setBlogVisibility(BlogVisibility.PUBLIC);
        blogPost.setChallenge(challenge);

        blogPostService.saveBlogPost(blogPost, null);

        Assertions.assertEquals(challenge, blogPost.getChallenge());
        Mockito.verify(blogPostRepositoryMock, Mockito.times(2)).save(Mockito.any());
    }
    
    @Test
    void deleteBlogPost_isAuthor_postDeleted() {
        BlogPost post = new BlogPost();
        UserEntity user = new UserEntity();
        
        post.setId(1L);
        post.setAuthor(user);
        
        Mockito.when(blogPostRepositoryMock.findById(anyLong())).thenReturn(Optional.of(post));
        
        blogPostService.deletePostById(1L, user);
    
        Mockito.verify(blogPostRepositoryMock, Mockito.times(1)).deleteById(1L);
    }
    
    @Test
    void deleteBlogPost_notAuthor_postNotDeleted() {
        BlogPost post = new BlogPost();
        UserEntity author = new UserEntity();
        UserEntity notAuthor = new UserEntity();
    
        post.setId(1L);
        post.setAuthor(author);
        
        Mockito.when(blogPostRepositoryMock.findById(anyLong())).thenReturn(Optional.of(post));
    
        blogPostService.deletePostById(1L, notAuthor);
    
        Mockito.verify(blogPostRepositoryMock, Mockito.never()).deleteById(1L);
    }
    
    @Test
    void deleteBlogPost_invalidId_postNotDeleted() {
    
        Mockito.when(blogPostRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
    
        blogPostService.deletePostById(1L, null);
    
        Mockito.verify(blogPostRepositoryMock, Mockito.never()).deleteById(1L);
    }

    @Test
    void createChallengePost_validParameters_challengePostCreated() throws MediaService.UploadFailure {
        blogPostService.createChallengePost("Test title", "Test description", userEntityMock, challenge, file);

        Mockito.verify(blogPostRepositoryMock, Mockito.times(1)).save(Mockito.any(BlogPost.class));
    }

    @Test
    void createChallengePost_nullTitle_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost(null, "description", userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_emptyTitle_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("", "description", userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_whitespaceTitle_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("   ", "description", userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_nullDescription_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("title", null, userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_emptyDescription_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("title", "", userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_whitespaceDescription_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("title", "    ", userEntityMock, challenge, file)
        );
    }

    @Test
    void createChallengePost_noAuthor_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("title", "description", null, challenge, file)
        );
    }

    @Test
    void createChallengePost_noChallenge_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost("title", "description", userEntityMock, null, file)
        );
    }

    @Test
    void createChallengePost_allNull_illegalArgumentThrown() {
        assertThrows(
            IllegalArgumentException.class,
            () -> blogPostService.createChallengePost(null, null, null, null, file)
        );
    }

    @Test
    void updateBlogPost_deleteMedia_isSuccessful() throws MediaService.UploadFailure, IOException {
        BlogPost blogPost = new BlogPost("Title", "Description", userEntityMock, LocalDateTime.now());
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "cat.mp4",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/videos/cat.mp4")
        );
        blogPost.setFileName(file.getOriginalFilename());

        Assertions.assertEquals("cat.mp4", blogPost.getFileName());

        blogPostService.updateBlogPost(blogPost, blogPost, null, "true");

        Assertions.assertNull(blogPost.getFileName());
    }

    @Test
    void updateBlogPost_updateMedia_isSuccessful() throws MediaService.UploadFailure, IOException {
        BlogPost existingPost = new BlogPost("Title", "Description", userEntityMock, LocalDateTime.now());
        MockMultipartFile file = new MockMultipartFile(
                "mediaUpload",
                "cat.mp4",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/videos/cat.mp4")
        );
        existingPost.setFileName(file.getOriginalFilename());

        Assertions.assertEquals("cat.mp4", existingPost.getFileName());

        BlogPost resultingPost = new BlogPost("Title", "Description", userEntityMock, LocalDateTime.now());

        MultipartFile newFile = new MockMultipartFile(
                "mediaUpload",
                "museswipr.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                this.getClass().getResourceAsStream("/images/museswipr.jpg")
        );

        blogPostService.updateBlogPost(existingPost, resultingPost, newFile, "false");

        Mockito.verify(blogPostRepositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(mediaServiceMock, Mockito.times(1)).setBlogPostImageIfValid(Mockito.any(), Mockito.any());
    }

    @Test
    void updateBlogPost_invalidProxy_illegalArgumentThrown() {
        BlogPost blogPost = new BlogPost("Title", "Description", userEntityMock, LocalDateTime.now());
        Club club = new Club("Club", "Test");
        club.setCreator(new UserEntity());
        blogPost.setClubProxy(club);

        assertThrows(
                IllegalArgumentException.class,
                () -> blogPostService.updateBlogPost(blogPost, blogPost, null, "false")
        );
    }

    @Test
    void updateBlogPost_invalidTarget_illegalArgumentThrown() {
        BlogPost blogPost = new BlogPost("Title", "Description", userEntityMock, LocalDateTime.now());
        Team team = new Team("Team");
        Set<UserEntity> users = new HashSet<>();
        users.add(new UserEntity());
        team.setManagers(users);
        blogPost.setTeamTarget(team);

        assertThrows(
                IllegalArgumentException.class,
                () -> blogPostService.updateBlogPost(blogPost, blogPost, null, "false")
        );
    }
}
