package nz.ac.canterbury.seng302.tab.service.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores configuration values related to images from application.properties into a singleton instance.
 */
@Component
@Scope("singleton")
public final class MediaConfig implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaConfig.class);
    private static volatile ApplicationContext applicationContext;
    private final String basePath;

    @Autowired
    MediaConfig(
            @Value("${app.file_upload_offset}") String baseFilePath
    ) {
        this.basePath = String.format("%s/media/", baseFilePath);
    }

    public static synchronized MediaConfig getInstance() {
        if (applicationContext == null) {
            throw new IllegalStateException("Application context has not been set for ImageConfig!");
        }

        return applicationContext.getBean(MediaConfig.class);
    }

    /**
     * Gets a fully-qualified path for an image stored on the server from a given URL, by combining it
     * with the {@link #basePath}
     *
     * @param url The relative URL of an image as stored on the server. For example {@code users/john.png}
     * @return Returns a fully-qualified file path for the given url
     */
    public Path getFullPath(String url) {
        var fullPath = Paths.get(this.basePath, url);
        LOGGER.debug("Retrieving picture = {}", fullPath);
        return fullPath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MediaConfig.applicationContext = applicationContext;
    }
}
