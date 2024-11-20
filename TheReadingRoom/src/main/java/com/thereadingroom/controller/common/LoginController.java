package com.thereadingroom.controller.common;

import com.thereadingroom.controller.user.UserDashboardController;
import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.book.BookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

/**
 * Controller for handling user login functionality.
 * This class provides the logic to authenticate users, manage user sessions,
 * and redirect them to the appropriate dashboard based on their roles.
 */
@Controller
public class LoginController {

    @FXML
    private TextField usernameField;  // Input field for the username

    @FXML
    private PasswordField passwordField;  // Input field for the password

    @FXML
    private ImageView imageView;  // ImageView to display the library's image

    private final IUserService userService;  // Service for managing user-related operations
    private final ServiceManager serviceManager;  // Manages services across the application, including session handling
    private final UIUtils uiUtils;  // Utility class for UI-related operations

    /**
     * Constructor for injecting services.
     *
     * @param userService Service for user-related operations.
     * @param serviceManager Manages services and session data.
     * @param uiUtils Utility class for UI-related tasks.
     */
    @Autowired
    public LoginController(IUserService userService, ServiceManager serviceManager, UIUtils uiUtils) {
        this.userService = userService;
        this.serviceManager = serviceManager;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller after the FXML is loaded.
     * It loads the library image onto the login screen.
     */
    @FXML
    public void initialize() {
        uiUtils.loadImage(imageView, "/com/thereadingroom/assets/images/Library.jpg");
    }

    /**
     * Handles the login process.
     * It validates the input fields, checks the user's credentials, and redirects to the correct dashboard based on the user's role.
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputFields(username, password)) return;

        if (userService.validateUserLogin(username, password)) {
            processSuccessfulLogin(username);  // Redirect based on user role
        } else {
            uiUtils.showError("Login Failed", "Invalid username or password.");
        }
    }

    /**
     * Validates that both username and password fields are filled.
     *
     * @param username The input username.
     * @param password The input password.
     * @return True if both fields are filled, otherwise false.
     */
    private boolean validateInputFields(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            uiUtils.showError("Login Failed", "Please enter both username and password.");
            return false;
        }
        return true;
    }

    /**
     * Processes a successful login by retrieving the user's details and setting the session data.
     * Redirects to the admin dashboard if the user is an admin, otherwise redirects to the user dashboard.
     *
     * @param username The username of the logged-in user.
     */
    private void processSuccessfulLogin(String username) {
        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            setSessionData(user);  // Set the user session details

            if (userService.isAdminUser(username)) {
                redirectToAdminDashboard();
            } else {
                initializeUserDashboard(user);
            }
        } else {
            uiUtils.showError("Login Error", "User data not found.");
        }
    }

    /**
     * Sets the session data for the logged-in user.
     *
     * @param user The logged-in user.
     */
    private void setSessionData(User user) {
        System.out.println("Setting session for user ID: " + user.getId());  // Log the session data setting for debugging
        serviceManager.getSessionManager().setUserDetails(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isAdmin()
        );
    }

    /**
     * Redirects the user to the admin dashboard.
     */
    private void redirectToAdminDashboard() {
        uiUtils.loadSceneWithData("/com/thereadingroom/fxml/admin/admin_view_stocks.fxml", getStage(), "Manage Stocks", controller -> {
            // No additional controller actions needed for this scene.
        });
    }

    /**
     * Initializes the user dashboard by setting up the user's shopping cart and loading the user dashboard scene.
     *
     * @param user The logged-in user.
     */
    private void initializeUserDashboard(User user) {
        ShoppingCart shoppingCart = createShoppingCart(user);
        serviceManager.getSessionManager().setShoppingCart(shoppingCart);  // Store the shopping cart in session

        uiUtils.loadSceneWithData("/com/thereadingroom/fxml/user/user_dashboard.fxml", getStage(), "User Dashboard", controller -> {
            UserDashboardController userController = (UserDashboardController) controller;
            userController.setShoppingCart(shoppingCart);  // Pass the shopping cart to the user dashboard controller
        });
    }

    /**
     * Creates and returns a shopping cart for the logged-in user.
     *
     * @param user The logged-in user.
     * @return The shopping cart for the user.
     */
    private ShoppingCart createShoppingCart(User user) {
        int cartId = CartService.getInstance().getOrCreateCart(user.getId());  // Retrieve or create a cart for the user
        ShoppingCart shoppingCart = new ShoppingCart(user.getId(), cartId);
        CartService.getInstance().getCartItems(cartId).forEach(item -> {
            Book book = BookService.getInstance().findBookById(item.getBookId());
            shoppingCart.addBook(book, item.getQuantity());
        });
        return shoppingCart;
    }

    /**
     * Handles the user registration process by loading the registration scene.
     */
    @FXML
    public void handleRegister() {
        uiUtils.loadSceneWithData("/com/thereadingroom/fxml/common/register.fxml", getStage(), "User Registration", controller -> {
            // No additional controller actions needed for this scene.
        });
    }

    /**
     * Retrieves the current stage (window) of the application.
     *
     * @return The current stage.
     */
    private Stage getStage() {
        return (Stage) usernameField.getScene().getWindow();
    }
}
