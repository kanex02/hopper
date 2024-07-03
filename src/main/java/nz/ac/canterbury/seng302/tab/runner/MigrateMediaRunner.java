package nz.ac.canterbury.seng302.tab.runner;

import nz.ac.canterbury.seng302.tab.service.media.MediaConfig;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class MigrateMediaRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrateMediaRunner.class);

    public void run(ApplicationArguments args) {
        // Specify the current directory path
        Path oldDirectoryPath = Paths.get(
                MediaConfig.getInstance().getFullPath("").toString()
                        .replace("media", "images")
                        .replace("public", "public/public"));

        if (!oldDirectoryPath.toFile().exists()) {
            LOGGER.info("Old directory does not exist, skipping migration.");
            return;
        }

        List<String> filePaths = getFileNames(new ArrayList<>(), oldDirectoryPath);


        for (String filePath : filePaths) {
            File oldFile = Paths.get(filePath).toFile();
            File newFile = Paths.get(filePath.replaceFirst("images", "media")).toFile();

            try {
                Files.createDirectories(Path.of(newFile.getParent()));
            } catch (IOException e) {
                LOGGER.warn("Error occurred when trying to create directories: {}", e.toString());
                return;
            }

            try (InputStream inputStream = new FileInputStream(oldFile);
                 OutputStream outputStream = new FileOutputStream(newFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                LOGGER.info("File moved successfully.");
            } catch (IOException e) {
                LOGGER.warn("Error occurred when trying to move a file: {}", e.toString());
                return;
            }
        }


        File oldDirectory = new File(oldDirectoryPath.toString());
        try {
            FileUtils.deleteDirectory(oldDirectory);
        } catch (IOException e) {
            LOGGER.warn("Error occurred when trying to delete the old directory: {}", e.toString());
        }

    }

    /**
     * Recursively gets all file names in a directory.
     * Code adapted from <a href="https://stackoverflow.com/a/24324367">Stack Overflow</a>.
     * @param fileNames List of file names
     * @param dir Directory to get file names from
     * @return List of file names
     */
    private List<String> getFileNames(List<String> fileNames, Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    fileNames.add(path.normalize().toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            LOGGER.info("Error occurred when getting file names: {}", e.toString());
        }
        return fileNames;
    }
}
