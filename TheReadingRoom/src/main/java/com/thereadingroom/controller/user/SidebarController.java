package com.thereadingroom.controller.user;

import com.thereadingroom.controller.common.EditProfileController;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class for managing the sidebar in the user interface.
 * It handles navigation to different sections such as viewing all books, user dashboard, orders, profile editing, and logout.
 */
@Controller
public class SidebarController {

    @FXML
    private Label usernameLabel;  // Label to display the username of the logged-in user

    @FXML
    private VBox contentArea;  // Container for the sidebar content

    private final ServiceManager serviceManager;  // Service manager for accessing session and services
    private final UIUtils uiUtils;  // UI utility class for handling scene loading and UI-related operations

    /**
     * Constructor for SidebarController with dependencies injected via Spring.
     *
     * @param serviceManager Manages application-wide services like session management.
     * @param uiUtils        Utility for UI operations such as loading scenes and showing alerts.
     */
    @Autowired
    public SidebarController(ServiceManager serviceManager, UIUtils uiUtils) {
        this.serviceManager = serviceManager;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller and displays the logged-in user's username.
     */
    @FXML
    public void initialize() {
        displayUsername();  // Display the username at the top of the sidebar
    }

    /**
     * Displays the logged-in user's username on the sidebar.
     */
    private void displayUsername() {
        String username = serviceManager.getSessionManager().getUsername();
        usernameLabel.setText("Welcome, " + username);  // Display welcome message with username
    }

    /**
     * Retrieves the current stage (window) from the content area.
     *
     * @return The current stage.
     */
    private Stage getCurrentStage() {
        return (Stage) contentArea.getScene().getWindow();
    }

    /**
     * Loads a new scene with a controller and passes data to it.
     *
     * @param fxmlPath       The path to the FXML file to load.
     * @param title          The title of the new window.
     * @param controllerData The data to pass to the new controller.
     */
    private void loadSceneWithController(String fxmlPath, String title, Object controllerData) {
        uiUtils.loadSceneWithData(fxmlPath, getCurrentStage(), title, controller -> {
            setupControllerData(controller, controllerData);
        });
    }

    /**
     * Configures the appropriate data in the new controller based on its type.
     *
     * @param controller     The controller instance to configure.
     * @param controllerData The data to pass to the controller.
     */
    private void setupControllerData(Object controller, Object controllerData) {
        if (controller instanceof ShoppingCartController) {
            ((ShoppingCartController) controller).setShoppingCart((ShoppingCart) controllerData);
        } else if (controller instanceof OrderController) {
            ((OrderController) controller).setUserId((int) controllerData);
        } else if (controller instanceof UserDashboardController) {
            ((UserDashboardController) controller).setShoppingCart((ShoppingCart) controllerData);
        } else if (controller instanceof ViewAllBooksController) {
            ((ViewAllBooksController) controller).setShoppingCart((ShoppingCart) controllerData);
        }
    }

    /**
     * Handles the navigation to the "All Books" section.
     */
    @FXML
    public void handleViewAllBooks() {
        loadSceneWithController("/com/thereadingroom/fxml/user/all_books.fxml", "All Books", getShoppingCartFromSession());
    }

    /**
     * Handles the navigation to the "User Dashboard" section.
     */
    @FXML
    public void handleViewUserDashboard() {
        loadSceneWithController("/com/thereadingroom/fxml/user/user_dashboard.fxml", "User Dashboard", getShoppingCartFromSession());
    }

    /**
     * Handles the navigation to the "Your Orders" section.
     */
    @FXML
    public void handleViewOrders() {
        loadSceneWithController("/com/thereadingroom/fxml/user/view_orders.fxml", "Your Orders", serviceManager.getSessionManager().getUserId());
    }

    /**
     * Handles the navigation to the "Edit Profile" section where the user can update their profile details.
     */
    @FXML
    public void handleEditProfile() {
        User currentUser = serviceManager.getSessionManager().getCurrentUser();
        if (currentUser != null && currentUser.getId() > 0) {
            uiUtils.loadSceneWithData("/com/thereadingroom/fxml/user/edit_profile.fxml", getCurrentStage(), "Edit Profile", controller -> {
                if (controller instanceof EditProfileController) {
                    ((EditProfileController) controller).populateFieldsWithUser(currentUser);
                }
            });
        } else {
            System.out.println("Error: Current user is null, cannot proceed to edit profile.");
            uiUtils.showError("Error", "No user data available. Please log in again.");
        }
    }

    /**
     * Handles the logout action, clears the session, and redirects the user to the login screen.
     */
    @FXML
    public void handleLogout() {
        serviceManager.getSessionManager().clearSession();
        uiUtils.loadSceneWithData("/com/thereadingroom/fxml/common/login.fxml", getCurrentStage(), "Login", controller -> {
        });
    }

    /**
     * Handles the navigation to the "Shopping Cart" section.
     */
    @FXML
    public void handleViewCart() {
        loadSceneWithController("/com/thereadingroom/fxml/user/shopping_cart.fxml", "Shopping Cart", getShoppingCartFromSession());
    }

    /**
     * Retrieves the shopping cart associated with the current session.
     *
     * @return The shopping cart of the logged-in user.
     */
    private ShoppingCart getShoppingCartFromSession() {
        return serviceManager.getSessionManager().getShoppingCart();
    }
}
