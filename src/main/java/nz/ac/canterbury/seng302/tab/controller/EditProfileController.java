package nz.ac.canterbury.seng302.tab.controller;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A controller class for handling edit profile related requests.
 */
@Controller
public class EditProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SportService sportService;

    @Autowired
    private MediaService mediaService;
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_EDIT_PROFILE = "redirect:/editProfile/";
    private static final String REDIRECT_CHANGE_PASSWORD = "redirect:/editProfile/%s/changePassword";

    private static final String REDIRECT_USER_PROFILE = "redirect:/user";

    /**
     * Save changes to the user edits if all criteria are met
     *
     * @param user A validated user fields
     * @param result binding result for validating the user fields
     * @param selectedSportOptions selected favourite sports
     * @param file image file for the user's profile picture
     * @param userId id of the user
     * @param redirectAttributes redirect attributes to add error messages
     * @return redirect to user page
     */
    @PostMapping("/editProfile/{userId}")
    public String submitForm(
        @Valid UserEntity user,
        BindingResult result,
        @RequestParam(required = false, defaultValue = "") Set<String> selectedSportOptions,
        @RequestParam(value = "imageUpload", required = false) MultipartFile file,
        @PathVariable("userId") Long userId,
        RedirectAttributes redirectAttributes
    ) {

        LOGGER.info("POST /editProfile/{}", userId);

        UserEntity existingUser = userService.getUserById(userId);

        if (userService.getUserByEmail(user.getEmail()) != null && !existingUser.getEmail().equals(user.getEmail())) {
            result.rejectValue(
                    "email",
                    "user.email.exists",
                    "Email already exists!"
            );
        }

        if (result.hasErrors()) {

            Map<String, String> errorMap = result.getFieldErrors()
                    .stream()
                    .filter(fieldError -> fieldError.getDefaultMessage() != null)
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage, ((s, s2) -> s + " " + s2)));

            redirectAttributes.addFlashAttribute("editErrors", errorMap);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("hasErrors", true);

            return REDIRECT_USER_PROFILE;
        }

        if (file != null && !file.isEmpty()) {
            try {
                mediaService.setUserImageIfValid(file, existingUser);
            } catch (MediaService.UploadFailure uploadFailure) {
                if (uploadFailure.getReason() == MediaService.FailReason.INVALID_IMAGE) {
                    redirectAttributes.addFlashAttribute("imageError", "Invalid file type. Please upload an image file.");
                    return REDIRECT_USER_PROFILE;
                } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                    redirectAttributes.addFlashAttribute("imageError", "Error uploading file: "
                            + uploadFailure.getMessage());
                    userService.delete(existingUser);
                    return REDIRECT_USER_PROFILE;
                }

            }
            userService.updateUser(existingUser);
        }

        //this should perhaps be a JPA 'merge'
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setDateOfBirth(user.getDateOfBirth());


        //log user with new email in
        existingUser.confirmEmail();
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(existingUser.getEmail(), null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);


        // Save user to database
        sportService.updateUserSports(selectedSportOptions, existingUser);
        userService.updateUser(existingUser);

        redirectAttributes.addFlashAttribute("changesSaved", "Changes have been saved");
        return REDIRECT_USER_PROFILE;
    }

    /**
     * Edits the user location and saves the location to the user
     *
     * @param location location entity to add to the user
     * @param bindingResult binding result for validating the location entity
     * @param userId id of the user
     * @param redirectAttributes redirect attributes to pass through the error messages
     * @return redirects to the user page
     */
    @PostMapping("/editProfile/{userId}/edit-location")
    public String editUserLocation(@Valid Location location,
                                   BindingResult bindingResult,
                                   @PathVariable String userId,
                                   RedirectAttributes redirectAttributes) {
        UserEntity existingUser = userService.getUserById(Long.parseLong(userId));

        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors()
                    .stream()
                    .filter(fieldError -> fieldError.getDefaultMessage() != null)
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("modalId", "myModal");
            redirectAttributes.addFlashAttribute("fieldErrors", errorMap);
            return REDIRECT_USER_PROFILE;
        }

        existingUser.setLocation(location);
        userService.updateUser(existingUser);

        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("/editProfile/{userId}/changePassword")
    public String getChangePasswordPage(Model model, @PathVariable("userId") Long id) {
        LOGGER.info("GET /editProfile/{}/changePassword", id);
        UserEntity user = userService.getLoggedInUser();
        if (user == null) {
            return "redirect:/";
        }
        if (!id.equals(user.getId())) {
            return String.format("redirect:/editProfile/%s", user.getId());
        }

        model.addAttribute("userId", id);
        return "updatePasswordForm";
    }

    @PostMapping("/editProfile/{userId}/changePassword")
    public String handlePasswordChange(@PathVariable("userId") String userId,
                                       @RequestParam("oldPassword") String oldPassword,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("retypePassword") String retypePassword,
                                       RedirectAttributes redirectAttributes) {
        UserEntity user = userService.getUserById(Long.parseLong(userId));

        if (!userService.checkPasswordMatches(Long.parseLong(userId), oldPassword)) {
            // The old password is incorrect; display an error message
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "The old password is incorrect");
            return String.format(REDIRECT_CHANGE_PASSWORD, userId);
        }

         if (!newPassword.equals(retypePassword)) {
             // The new password and the retype password do not match; display an error message
             redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Passwords do not match!");
             return String.format(REDIRECT_CHANGE_PASSWORD, userId);
         }

        // The old password is correct and passwords match; proceed with the password update process
        Optional<String> inputErrors = userService.updatePassword(user, newPassword, retypePassword);

        if (inputErrors.isPresent()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, inputErrors.get());
            return String.format(REDIRECT_CHANGE_PASSWORD, userId);
        }

        // Redirect the user to edit profile page and display a success message
        redirectAttributes.addFlashAttribute("passwordUpdatedMessage", "Password updated successfully");
        return REDIRECT_EDIT_PROFILE + userId;
    }
}