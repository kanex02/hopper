package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.InvalidFormException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
class GlobalDefaultExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String uploadSizeErrorHandler(HttpServletRequest req, Exception e) {
        LOGGER.error("Request: {} raised {}", req.getRequestURL(), e);
        return "error/413";
    }

    @ExceptionHandler(InvalidFormException.class)
    public String illegalArgumentHandler(HttpServletRequest req, Exception e) {
        LOGGER.error("Request: {} raised {}", req.getRequestURL(), e);
        return "error/400";
    }

}