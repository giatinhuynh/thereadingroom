package com.thereadingroom.controller.common;

import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller responsible for handling user registration.
 * Validates user inputs and interacts with the IUserService to register new users.
 */
@Controller
public class RegisterController {

    @FXML
    private TextField usernameField;  // TextField for entering the username

    @FXML
    private TextField firstNameField;  // TextField for entering the first name

    @FXML
    private TextField lastNameField;  // TextField for entering the last name

    @FXML
    private PasswordField passwordField;  // PasswordField for entering the password

    @FXML
    private ImageView imageView;  // ImageView for displaying the library image

    private final IUserService userService;  // Service responsible for user-related operations
    private final UIUtils uiUtils;  // UI utility class for common UI tasks

    /**
     * Constructor to inject required services.
     *
     * @param userService The user service for managing user-related operations.
     * @param uiUtils The utility class for handling UI operations.
     */
    @Autowired
    public RegisterController(IUserService userService, UIUtils uiUtils) {
        this.userService = userService;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller. This method is automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Use helper method in UIUtils to load and set the image
        uiUtils.loadImage(imageView, "/com/thereadingroom/assets/images/Library.jpg");
    }

    /**
     * Handles the registration process when the user submits the form.
     * Validates the input fields and attempts to register the user.
     */
    @FXML
    public void handleRegister() {
        if (!isUserServiceInitialized()) return;  // Ensure userService is initialized

        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!areFieldsValid(username, firstName, lastName, password)) return;  // Validate input fields

        // Attempt to register the user and show appropriate feedback
        boolean success = userService.registerUser(username, firstName, lastName, password, false);
        if (success) {
            uiUtils.showAlert("Success", "Registration successful! Redirecting to login...");
            redirectToLogin();  // Redirect to login if successful
        } else {
            uiUtils.showError("Registration Failed", "Username already exists. Please choose another.");
        }
    }

    /**
     * Handles the action when the user chooses to go back to the login screen.
     */
    @FXML
    public void handleBackToLogin() {
        redirectToLogin();  // Redirect to the login screen
    }

    /**
     * Checks if the userService is initialized.
     *
     * @return true if the service is initialized, false otherwise.
     */
    private boolean isUserServiceInitialized() {
        if (userService == null) {
            throw new IllegalStateException("UserService not initialized!");  // Throw exception if uninitialized
        }
        return true;
    }

    /**
     * Validates all the fields to ensure proper user input.
     *
     * @param username  The username entered by the user.
     * @param firstName The first name entered by the user.
     * @param lastName  The last name entered by the user.
     * @param password  The password entered by the user.
     * @return true if all fields are valid, false otherwise.
     */
    private boolean areFieldsValid(String username, String firstName, String lastName, String password) {
        String errorMessage = null;

        // Check if any field is empty or if the password is too short
        if (isAnyFieldEmpty(username, firstName, lastName, password)) {
            errorMessage = "All fields must be filled out.";
        } else if (password.length() < 8) {
            errorMessage = "Password must be at least 8 characters long.";
        }

        // Show error message if validation fails
        if (errorMessage != null) {
            uiUtils.showError("Registration Failed", errorMessage);
            return false;
        }
        return true;
    }

    /**
     * Checks if any of the provided fields are empty.
     *
     * @param fields The input fields to check.
     * @return true if any field is empty, false otherwise.
     */
    private boolean isAnyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return true;  // Return true if any field is empty
        }
        return false;
    }

    /**
     * Redirects the user to the login screen after registration.
     */
    private void redirectToLogin() {
        uiUtils.loadSceneWithData("/com/thereadingroom/fxml/common/login.fxml", getStage(), "Login", controller -> {
            // LoginController will be Spring managed, no need to inject manually here
        });
    }

    /**
     * Retrieves the current stage (window) from the username field.
     *
     * @return The current stage (window).
     */
    private Stage getStage() {
        return (Stage) usernameField.getScene().getWindow();  // Get the current stage
    }
}
