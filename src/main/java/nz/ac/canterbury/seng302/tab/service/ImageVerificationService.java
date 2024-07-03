package nz.ac.canterbury.seng302.tab.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

/**
 * This class provides a service for verifying that an uploaded image is of a valid type and size.
 */
@Service
public class ImageVerificationService {

    /**
     * Required constructor - private as all methods are static
     */
    private ImageVerificationService() {}

    /**
     * This method is used to verify that the image uploaded is a valid image type and size
     *
     * @param file the image file to be verified
     * @return Returns true if the file is of an acceptable image type and size, otherwise returns false
     **/
    public static boolean verifyImageTypeAndSize(@RequestParam("file") MultipartFile file) {
        final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        if (!Objects.equals(file.getContentType(), "image/png") &&
                !Objects.equals(file.getContentType(), "image/jpeg") &&
                !Objects.equals(file.getContentType(), "image/svg+xml")) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE;
    }

    /**
     * This method is used to verify that the image uploaded is a square image
     *
     * @param file the image file to be verified
     * @return Returns true if the file is a square image, otherwise returns false
     * @throws IOException if the image cannot be read
     **/
    public static boolean verifyImageDimensions(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            int width = image.getWidth();
            int height = image.getHeight();
            return (width == height);
        } catch (IOException e) {
            throw new IOException("Failed to verify image dimensions: " + e.getMessage());
        }
    }

    /**
     * Checks that the given path string does not contain potentially malicious traversal patterns such
     * as ../ and ./
     *
     * @param pathString The path string to check
     * @return Returns {@code true} if the pathString does not contain the traversal patterns
     */
    public static boolean isPathValid(String pathString) {
        return !pathString.contains("../") && !pathString.contains("./");
    }

    /**
     * This method is used to crop a non-square image to a square image
     *
     * @param file the image file to be cropped
     * @return Returns a cropped image file
     * @throws IOException if the image cannot be read
     **/
    public static MultipartFile cropImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            int width = image.getWidth();
            int height = image.getHeight();
            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            BufferedImage croppedImage = image.getSubimage(x, y, size, size);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String formatName = Objects.equals(file.getContentType(), "image/jpeg") ? "jpg" : "png";
            ImageIO.write(croppedImage, formatName, outputStream);
            return new MultipartFile() {
                @Override
                public String getName() {
                    return file.getName();
                }

                @Override
                public String getOriginalFilename() {
                    return file.getOriginalFilename();
                }

                @Override
                public String getContentType() {
                    return file.getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return file.isEmpty();
                }

                @Override
                public long getSize() {
                    return outputStream.toByteArray().length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return outputStream.toByteArray();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(outputStream.toByteArray());
                }

                @Override
                public void transferTo(File file) throws IOException, IllegalStateException {
                    throw new UnsupportedOperationException("Multipart file transfer not supported");
                }
            };
        } catch (IOException e) {
            throw new IOException("Failed to crop image: " + e.getMessage());
        }
    }

}
