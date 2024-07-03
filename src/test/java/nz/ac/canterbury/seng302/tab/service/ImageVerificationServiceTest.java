package nz.ac.canterbury.seng302.tab.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageVerificationServiceTest {

    @Test
    void validImage_onCheckValidity_isValid() throws Exception {

        String fileName = "images/valid_image.png";
        var location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] pngImageData = Files.readAllBytes(Path.of(location.toURI()));
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "image/png", pngImageData);

        assertTrue(ImageVerificationService.verifyImageTypeAndSize(multipartFile));
    }

    @Test
    void invalidFileType_onCheckValidity_notValid() throws Exception {
        String fileName = "images/invalid_file.txt";
        URL location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] textData = Files.readAllBytes(Path.of(location.toURI()));
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "text/plain", textData);

        assertFalse(ImageVerificationService.verifyImageTypeAndSize(multipartFile));
    }

    @Test
    void invalidFileSize_onCheckValidity_notValid() throws Exception {
        String fileName = "images/valid_image.png";
        URL location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] pngImageData = Files.readAllBytes(Path.of(location.toURI()));
        byte[] invalidImageData = new byte[pngImageData.length * 100];
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "image/png", invalidImageData);

        assertFalse(ImageVerificationService.verifyImageTypeAndSize(multipartFile));
    }

    @Test
    void squareImage_verifyImageDimensions_returnsTrue() throws IOException, URISyntaxException {
        String fileName = "images/valid_image.png";
        URL location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] pngImageData = Files.readAllBytes(Path.of(location.toURI()));
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "image/png", pngImageData);

        assertTrue(ImageVerificationService.verifyImageDimensions(multipartFile));
    }

    @Test
    void nonSquareImage_verifyImageDimensions_returnsFalse() throws IOException, URISyntaxException {
        String fileName = "images/rectangle.png";
        URL location = this.getClass().getClassLoader().getResource(fileName);
        Assertions.assertNotNull(location);

        byte[] pngImageData = Files.readAllBytes(Path.of(location.toURI()));
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "image/png", pngImageData);

        assertFalse(ImageVerificationService.verifyImageDimensions(multipartFile));
    }

    @Test
    void validPath_isPathValid_returnsTrue() {
        assertTrue(ImageVerificationService.isPathValid("valid/path/"));
    }

    @Test
    void pathWithParentDirectoryTraversal_isPathValid_returnsFalse() {
        assertFalse(ImageVerificationService.isPathValid("../invalid/path"));
    }

    @Test
    void pathWithCurrentDirectoryTraversal_isPathValid_returnsFalse() {
        assertFalse(ImageVerificationService.isPathValid("./invalid/path"));
    }
}
