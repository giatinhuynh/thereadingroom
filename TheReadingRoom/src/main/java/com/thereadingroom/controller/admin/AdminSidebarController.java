package com.thereadingroom.controller.admin;

import com.thereadingroom.controller.common.EditProfileController;
import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.auth.SessionManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling the Admin sidebar functionality.
 * This class provides navigation to various admin features such as managing stocks, orders, users, editing profile, and logging out.
 */
@Controller
public class AdminSidebarController {

    @FXML
    private Label usernameLabel;  // Label to display the currently logged-in admin's username

    @FXML
    private VBox contentArea;  // Content area in the sidebar for rendering different admin views

    private final ServiceManager serviceManager;  // Service manager for accessing session and other services
    private final UIUtils uiUtils;  // Utility class for UI-related tasks such as loading scenes and displaying alerts

    /**
     * Constructor for dependency injection of required services.
     *
     * @param serviceManager The service manager for accessing session management and other services.
     * @param uiUtils Utility for handling UI-related operations.
     */
    @Autowired
    public AdminSidebarController(ServiceManager serviceManager, UIUtils uiUtils) {
        this.serviceManager = serviceManager;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the sidebar by displaying the logged-in admin's username.
     * If the username is not found, an error is shown.
     */
    @FXML
    public void initialize() {
        // Retrieve the username from the session manager and display it in the sidebar
        SessionManager sessionManager = serviceManager.getSessionManager();
        String username = sessionManager.getUsername();
        if (username != null) {
            usernameLabel.setText("Welcome, Admin " + username);
        } else {
            uiUtils.showError("Session Error", "Unable to fetch admin username.");
        }
    }

    /**
     * Retrieves the current stage (window) for scene transitions.
     *
     * @return The current stage (window).
     */
    private Stage getCurrentStage() {
        return (Stage) contentArea.getScene().getWindow();
    }

    /**
     * Loads a new scene without passing any additional data.
     *
     * @param fxmlPath The path to the FXML file for the scene to load.
     * @param title The title to be displayed in the new window.
     */
    private void loadSceneWithoutData(String fxmlPath, String title) {
        uiUtils.loadScene(fxmlPath, getCurrentStage(), title);
    }

    /**
     * Handles navigation to the "Manage Stocks" section.
     * Loads the stock management view for the admin.
     */
    @FXML
    public void handleViewStocks() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_view_stocks.fxml", "Manage Stocks");
    }

    /**
     * Handles navigation to the "Manage All Orders" section.
     * Loads the order management view for the admin.
     */
    @FXML
    public void handleViewManageOrders() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_manage_orders.fxml", "Manage All Orders");
    }

    /**
     * Handles navigation to the "Manage Users" section.
     * Loads the user management view for the admin.
     */
    @FXML
    public void handleViewManageUsers() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_manage_users.fxml", "Manage Users");
    }

    /**
     * Handles the process for editing the currently logged-in admin's profile.
     * Loads the edit profile scene and pre-populates the form with the user's existing details.
     */
    @FXML
    public void handleEditProfile() {
        User currentUser = serviceManager.getSessionManager().getCurrentUser();  // Fetch the current user from the session
        if (currentUser != null && currentUser.getId() > 0) {
            // Load the Edit Profile scene and pass the current user data to the form
            uiUtils.loadSceneWithData("/com/thereadingroom/fxml/admin/admin_edit_profile.fxml", getCurrentStage(), "Edit Profile", controller -> {
                if (controller instanceof EditProfileController) {
                    ((EditProfileController) controller).populateFieldsWithUser(currentUser);
                }
            });
        } else {
            // Show an error if the user data is unavailable
            System.out.println("Error: Current user is null, cannot proceed to edit profile.");
            uiUtils.showError("Error", "No user data available. Please log in again.");
        }
    }

    /**
     * Handles the admin logout process.
     * Clears the session and redirects the user to the login screen.
     */
    @FXML
    public void handleLogout() {
        // Clear the current session and redirect to the login screen
        serviceManager.getSessionManager().clearSession();
        uiUtils.loadScene("/com/thereadingroom/fxml/common/login.fxml", getCurrentStage(), "Login");
    }
}
