package com.thereadingroom.controller.common;

import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

/**
 * Controller class for editing the user's profile.
 * This class provides functionality to load the current user's profile data, allow updates to their profile,
 * and handle session updates accordingly.
 */
@Controller
public class EditProfileController {

    @FXML
    private TextField usernameField;  // Input field for the username

    @FXML
    private TextField firstNameField;  // Input field for the first name

    @FXML
    private TextField lastNameField;  // Input field for the last name

    @FXML
    private PasswordField passwordField;  // Input field for the password

    private final IUserService userService;  // Service to handle user-related operations
    private final UIUtils uiUtils;  // Utility for UI-related tasks
    private final ServiceManager serviceManager;  // Manages session data and services

    private User currentUser;  // Holds the current user object being edited

    /**
     * Constructor for injecting services.
     *
     * @param userService The service to manage user operations.
     * @param uiUtils The UI utility service.
     * @param serviceManager The service manager for handling session data.
     */
    @Autowired
    public EditProfileController(IUserService userService, UIUtils uiUtils, ServiceManager serviceManager) {
        this.userService = userService;
        this.uiUtils = uiUtils;
        this.serviceManager = serviceManager;
    }

    /**
     * Initializes the controller by loading the current user's data.
     */
    @FXML
    public void initialize() {
        loadCurrentUser();  // Load user data from the session
    }

    /**
     * Loads the current user from the session and populates the fields.
     */
    private void loadCurrentUser() {
        String currentUsername = serviceManager.getSessionManager().getUsername();  // Get the logged-in user's username from the session

        // Validate if the username is valid and retrieve user data
        if (isValidString(currentUsername)) {
            Optional<User> userOptional = userService.getUserByUsername(currentUsername);
            if (userOptional.isPresent()) {
                currentUser = userOptional.get();
                updateSessionData(currentUser, currentUser.getFirstName(), currentUser.getLastName());  // Update session with latest data
                populateUserDetails();  // Populate the input fields with user data
            } else {
                uiUtils.showError("Error", "User data not found for username: " + currentUsername);
            }
        } else {
            uiUtils.showError("Error", "Invalid session data. Please log in again.");
        }
    }

    /**
     * Populates the input fields with the current user's data.
     */
    private void populateUserDetails() {
        usernameField.setText(currentUser.getUsername());
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        passwordField.setPromptText("Enter new password");
    }

    /**
     * Validates if the input string is valid (non-null and non-empty).
     *
     * @param input The input string to validate.
     * @return True if the string is valid, false otherwise.
     */
    private boolean isValidString(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Populates the fields with the provided user data.
     *
     * @param user The user object to populate the fields with.
     */
    public void populateFieldsWithUser(User user) {
        if (user != null) {
            this.currentUser = user;  // Set the user being edited
            usernameField.setText(user.getUsername());
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            passwordField.setPromptText("Enter new password (min. 8 characters)");
        } else {
            uiUtils.showError("Error", "Invalid user data. Please log in again.");
        }
    }

    /**
     * Handles saving the changes to the user's profile.
     * Validates the fields and calls the service to update the user's profile.
     */
    @FXML
    public void handleSaveChanges() {
        if (validateFields()) {
            boolean success = updateUserProfile();  // Attempt to update the user's profile
            if (success) {
                loadCurrentUser();  // Reload user data after successful update
                uiUtils.showAlert("Success", "Profile updated successfully.");
            } else {
                uiUtils.showError("Error", "Failed to update profile. Please try again.");
            }
        }
    }

    /**
     * Validates the input fields to ensure data is entered correctly.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private boolean validateFields() {
        if (!isValidString(firstNameField.getText())) {
            uiUtils.showError("Validation Error", "First name cannot be empty.");
            return false;
        }
        if (!isValidString(lastNameField.getText())) {
            uiUtils.showError("Validation Error", "Last name cannot be empty.");
            return false;
        }
        if (!isValidString(passwordField.getText()) || passwordField.getText().length() < 8) {
            uiUtils.showError("Validation Error", "Password must be at least 8 characters long.");
            return false;
        }
        return true;
    }

    /**
     * Updates the user's profile by sending the data to the service and updating the session.
     *
     * @return True if the update was successful, false otherwise.
     */
    private boolean updateUserProfile() {
        String newUsername = usernameField.getText().trim();
        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        String newPassword = passwordField.getText().trim();

        // Validate if the fields contain valid data
        if (!isValidString(newUsername) || !isValidString(newFirstName) || !isValidString(newLastName)) {
            uiUtils.showError("Validation Error", "All fields must be filled out.");
            return false;
        }

        boolean success = userService.updateUserProfilebyID(
                currentUser.getId(),
                newUsername,
                newFirstName,
                newLastName,
                newPassword,
                currentUser.isAdmin()
        );

        if (success) {
            // Update session data with the new profile details
            updateSessionData(currentUser, newFirstName, newLastName);
        }

        return success;
    }

    /**
     * Updates the session with the new user details after a successful profile update.
     *
     * @param user The user whose details are being updated.
     * @param newFirstName The updated first name.
     * @param newLastName The updated last name.
     */
    private void updateSessionData(User user, String newFirstName, String newLastName) {
        // Use the service manager to update the session with the new user details
        serviceManager.getSessionManager().setUserDetails(user.getId(), user.getUsername(), newFirstName, newLastName, user.isAdmin());
    }
}
