package nz.ac.canterbury.seng302.tab.service.media;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.ImageVerificationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Service class that provides functionality for media-related activities
 */
@Service
public class MediaService {
    public static final int MP4_SIZE_LIMIT = 256 * 1024 * 1024;
    Logger logger = LoggerFactory.getLogger(MediaService.class);
    /**
     * Uploads and sets the image of an entity if the image is valid.
     * <p>
     * Convenience methods for users and teams provided by {@link #setTeamImageIfValid(MultipartFile, Team)} and
     * {@link #setUserImageIfValid(MultipartFile, UserEntity)}
     *
     * @param file                The image multipart file uploaded by the user
     * @param imageSetterCallback A setter callback to set the image property of an entity. For example, something like
     *                            a method reference to {@link UserEntity#setProfilePicture(String)}
     * @param imagePathSupplier   A Supplier that can retrieve a full path of an image from an entity. For example,
     *                            something like a method reference to {@link UserEntity#getProfilePicturePath()}.
     *                            This is needed as users and teams upload images to different directories, and no abstraction
     *                            over them currently exists
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     */
    @Nullable
    public void setImageIfValid(
            MultipartFile file,
            Consumer<String> imageSetterCallback,
            Supplier<String> imagePathSupplier
    ) throws UploadFailure {

        if (file.isEmpty() || !ImageVerificationService.verifyImageTypeAndSize(file)) {
            throw new UploadFailure(FailReason.INVALID_IMAGE);
        }

        saveMedia(file, imageSetterCallback, imagePathSupplier);
    }

    /**
     * Uploads and updates the profile image of a user if the image is valid.
     * <p>
     * Note that this does NOT update the user in persistence, so calling {@link UserService#updateUser(UserEntity)} is
     * still necessary.
     *
     * @param file The uploaded profile picture for the user
     * @param user The user of the uploaded picture
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     * @see #setImageIfValid(MultipartFile, Consumer, Supplier)
     */
    public void setUserImageIfValid(MultipartFile file, UserEntity user) throws UploadFailure {
        this.setImageIfValid(file, user::setProfilePicture, user::getProfilePicturePath);
    }

    /**
     * Uploads and updates the media of a blog post if the image is valid.
     * <p>
     * Note that this does NOT update the post in persistence, so saving the blog post is still necessary.
     *
     * @param file The uploaded image for the post
     * @param blogPost The post of the uploaded image
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     * @see #setImageIfValid(MultipartFile, Consumer, Supplier)
     */
    public void setBlogPostImageIfValid(MultipartFile file, BlogPost blogPost) throws UploadFailure {
        this.setImageIfValid(file, blogPost::setFileName, blogPost::getFilePath);
    }

    /**
     * Uploads and updates the profile image of a team if the image is valid
     * <p>
     * Note that this does NOT update the user in persistence, so calling {@link TeamService#(UserEntity)} is
     * still necessary.
     *
     * @param file The profile picture that a user uploaded
     * @param team The team to set the profile image for
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     * @see #setImageIfValid(MultipartFile, Consumer, Supplier)
     */
    public void setTeamImageIfValid(MultipartFile file, Team team) throws UploadFailure {
        this.setImageIfValid(file, team::setImage, team::getImagePath);
    }
    
    /**
     * Uploads and updates the profile image of a club if the image is valid
     * <p>
     * Note that this does NOT update the user in persistence
     *
     * @param file The profile picture that a user uploaded
     * @param club The club to set the profile image for
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     * @see #setImageIfValid(MultipartFile, Consumer, Supplier)
     */
    public void setClubImageIfValid(MultipartFile file, Club club) throws UploadFailure {
        this.setImageIfValid(file, club::setImageName, club::getImagePath);
    }

    /**
     * Tries to upload an image to a team
     *
     * @param file The image file being uploaded
     * @param model the model from the upload controller
     * @param team the {@link Team} that the image is being uploaded to
     * @return true if upload succeeds, false otherwise
     */
    public boolean uploadTeamImage(MultipartFile file, Model model, Team team) {
        try {
            setTeamImageIfValid(file, team);
        } catch (UploadFailure failure) {
            logger.error("Error uploading team image", failure);
            model.addAttribute("imageError", failure.getReason().getMessage());
            return false;
        }

        return true;
    }


    /**
     * Uploads and updates the video of a blog post if it is valid.
     * <p>
     * Note that this does NOT update the blog in persistence
     *
     * @param file The video that a user uploaded
     * @param blogPost The post to set the video for
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     * @see #setVideoIfValid(MultipartFile, Consumer, Supplier)
     */
    public void setBlogPostVideoIfValid(MultipartFile file, BlogPost blogPost) throws UploadFailure {
        this.setVideoIfValid(file, blogPost::setFileName, blogPost::getFilePath);
    }

    /**
     * Uploads and sets the video of an entity if the video is valid.
     *
     * @param file                The video multipart file uploaded by the user
     * @param fileNameSetterCallback A setter callback to set the video property of an entity. For example, something like
     *                            a method reference to {@link UserEntity#setProfilePicture(String)}
     * @param filePathSupplier   A Supplier that can retrieve a full path of a video from an entity. For example,
     *                            something like a method reference to {@link UserEntity#getProfilePicturePath()}.
     *                            This is needed as users and teams upload images to different directories, and no abstraction
     *                            over them currently exists
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     *                       more information about the failure. If the failure was caused by {@link FailReason#UPLOAD_ERROR},
     *                       then the cause {@link Throwable} will also be given.
     */
    @Nullable
    public void setVideoIfValid(
            MultipartFile file,
            Consumer<String> fileNameSetterCallback,
            Supplier<String> filePathSupplier
    ) throws UploadFailure {

        // Check that the file is not empty, is a valid type, and is not bigger than size limit
        if (file.isEmpty() || !Objects.equals(file.getContentType(), "video/mp4") || file.getSize() > MP4_SIZE_LIMIT) {
            throw new UploadFailure(FailReason.INVALID_VIDEO);
        }

        saveMedia(file, fileNameSetterCallback, filePathSupplier);
    }

    /**
     * Saves a media file to the file system
     * @param file The file to save
     * @param fileNameSetterCallback The callback to set the file name of the entity
     * @param filePathSupplier The supplier to retrieve the file path of the entity
     * @throws UploadFailure Thrown if the upload failed for some reason. Contains a {@link FailReason} to pick out
     */
    public void saveMedia(MultipartFile file, Consumer<String> fileNameSetterCallback, Supplier<String> filePathSupplier) throws UploadFailure {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!ImageVerificationService.isPathValid(fileName)) {
            throw new UploadFailure(FailReason.INVALID_IMAGE);
        }

        // update the file name of the entity
        fileNameSetterCallback.accept(fileName);

        // retrieve the full path from the entity
        // the public replacement is some tom-foolery to make retrieval work from the same URL
        Path fullPath = Path.of(filePathSupplier.get().replace("public", "public/public"));
        if (!Files.exists(fullPath)) {
            try {
                Files.createDirectories(fullPath);
            } catch (IOException ex) {
                throw new UploadFailure(FailReason.UPLOAD_ERROR, ex);
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully uploaded file: {}", fullPath);
        } catch (IOException ex) {
            throw new UploadFailure(FailReason.UPLOAD_ERROR, ex);
        }
    }

    /**
     * Custom upload failure exception that is thrown whenever images fail to upload.
     * <p>
     * Stores a reason for the failure and the cause of the failure if it is due to file issues.
     * <p>
     * The message of the exception is set to the message of the reason.
     */
    public static class UploadFailure extends Exception {

        private final FailReason reason;

        /**
         * Creates a new upload failure exception with a reason
         *
         * @param reason The reason for the failure
         */
        public UploadFailure(FailReason reason) {
            this(reason, null);
        }

        /**
         * Creates a new upload failure exception with a reason and a cause
         *
         * @param reason The reason for the failure
         * @param cause  The cause of the exception
         */
        public UploadFailure(FailReason reason, Throwable cause) {
            super(reason.message, cause);
            this.reason = reason;
        }

        /**
         * @return Returns the reason for the exception
         */
        public FailReason getReason() {
            return reason;
        }
    }

    /**
     * Wrappers over strings for messages for failure that can be displayed to the user.
     */
    public enum FailReason {
        /**
         * If given this reason for a failure, then that the means that the failure was caused by the file
         * either being too big or an invalid type. It can also be used if the path contains traversal characters
         * such as {@code ../} or {@code ./} to hopefully prevent path traversal vulnerabilities
         */
        INVALID_IMAGE("Invalid image file type or size. Accepted file types are .png, .jpg, or .svg and all files must be less than 10MB."),

        /**
         * Similar to {@link #INVALID_IMAGE} but for video files.
         */
        INVALID_VIDEO("Invalid video file type or size. Accepted file types are .mp4 and all video files must be less than 256MB."),

        /**
         * If given this reason for a failure, then that means that the failure was caused by a file {@link IOException},
         * and that exception will be stored in {@link Exception#getCause()}
         */
        UPLOAD_ERROR("Error uploading media, please contact a server administrator");

        private final String message;

        FailReason(String message) {
            this.message = message;
        }

        /**
         * @return Returns the failure message
         */
        public String getMessage() {
            return message;
        }
    }

}
