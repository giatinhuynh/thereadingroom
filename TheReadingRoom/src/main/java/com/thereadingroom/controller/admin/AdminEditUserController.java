package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class responsible for editing user profiles in the admin panel.
 * Provides methods for populating user details, saving updates, and validating input fields.
 */
@Controller
public class AdminEditUserController {

    @FXML
    private TextField usernameField;  // Input field for the user's username

    @FXML
    private TextField firstNameField;  // Input field for the user's first name

    @FXML
    private TextField lastNameField;  // Input field for the user's last name

    @FXML
    private PasswordField passwordField;  // Input field for the user's password

    private final IUserService userService;  // Service for managing user-related operations
    private final UIUtils uiUtils;  // Utility for UI-related tasks such as showing alerts

    private User user;  // Holds the user object being edited

    /**
     * Constructor-based dependency injection for UserService and UIUtils.
     *
     * @param userService The service for handling user operations.
     * @param uiUtils Utility for UI-related functions.
     */
    @Autowired
    public AdminEditUserController(IUserService userService, UIUtils uiUtils) {
        this.userService = userService;
        this.uiUtils = uiUtils;
    }

    /**
     * Sets the user to be edited and populates the input fields with the user's current details.
     *
     * @param user The user to be edited.
     */
    public void setUser(User user) {
        this.user = user;
        populateUserDetails();  // Pre-fill the form with the user's current details
    }

    /**
     * Populates the input fields with the user's details for editing.
     */
    private void populateUserDetails() {
        usernameField.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        passwordField.setPromptText("Enter new password (min. 8 characters)");  // Prompt for password input
    }

    /**
     * Handles the saving of the updated user profile.
     * Validates input fields before attempting to update the user profile via the service.
     */
    @FXML
    public void handleSaveUser() {
        if (validateInputFields()) {
            // Attempt to update the user profile
            boolean success = userService.updateUserProfilebyID(
                    user.getId(),
                    usernameField.getText().trim(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    passwordField.getText().trim(),
                    user.isAdmin()
            );

            // Show success or error message based on the update result
            if (success) {
                uiUtils.showAlert("Success", "User profile updated successfully.");
                uiUtils.closeCurrentWindow(usernameField);  // Close the window after a successful update
            } else {
                uiUtils.showError("Update Failed", "Failed to update user profile.");
            }
        }
    }

    /**
     * Validates the input fields for the user profile form.
     * Ensures that no fields are empty and that the password meets minimum length requirements.
     *
     * @return true if all fields are valid; false otherwise.
     */
    private boolean validateInputFields() {
        if (!isValidString(usernameField.getText())) {
            uiUtils.showError("Validation Error", "Username cannot be empty.");
            return false;
        }
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
     * Checks if the provided input string is valid.
     * A valid string is non-null, non-empty, and non-blank.
     *
     * @param input The input string to validate.
     * @return true if the string is valid; false otherwise.
     */
    private boolean isValidString(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Handles the cancellation of the edit operation, closing the current window without saving changes.
     */
    @FXML
    public void handleCancel() {
        uiUtils.closeCurrentWindow(usernameField);
    }
}
