package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.utils.auth.SessionManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class for managing the User Dashboard view.
 * It provides a table to display books, and a welcome message for the logged-in user.
 */
@Controller
public class UserDashboardController extends BookTableController {

    @FXML
    private TableView<Book> booksTableView;  // TableView to display the list of books

    @FXML
    private TableColumn<Book, String> titleColumn;  // Column to display the book title

    @FXML
    private TableColumn<Book, String> authorColumn;  // Column to display the book author

    @FXML
    private TableColumn<Book, String> priceColumn;  // Column to display the book price

    @FXML
    private TableColumn<Book, Integer> stockColumn;  // Column to display available stock of the book

    @FXML
    private TableColumn<Book, Integer> soldCopiesColumn;  // Column to display the number of sold copies

    @FXML
    private TableColumn<Book, Button> actionColumn;  // Column for actions (e.g., Add to Cart)

    @FXML
    private Label welcomeLabel;  // Label to display a welcome message for the user

    private final ServiceManager serviceManager;  // Service manager for managing session and services

    /**
     * Constructor for the UserDashboardController, with dependencies injected via Spring.
     *
     * @param bookService    Service for managing book-related operations.
     * @param cartService    Service for managing cart-related operations.
     * @param uiUtils        Utility class for UI-related operations.
     * @param serviceManager Service manager for accessing session and services.
     */
    @Autowired
    public UserDashboardController(IBookService bookService, CartService cartService, UIUtils uiUtils, ServiceManager serviceManager) {
        super(bookService, cartService, uiUtils);
        this.serviceManager = serviceManager;
    }

    /**
     * Initializes the User Dashboard. This method is automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Initialize the book table and apply styles
        initializeBookTable(booksTableView, titleColumn, authorColumn, priceColumn, stockColumn, soldCopiesColumn, actionColumn);
        uiUtils.loadCSS(booksTableView, "/com/thereadingroom/css/table-style.css");

        // Load and set the welcome message for the user
        loadWelcomeMessage();
    }

    /**
     * Loads and displays a personalized welcome message for the current user.
     * If no user data is available, a generic message is shown.
     */
    private void loadWelcomeMessage() {
        User currentUser = serviceManager.getSessionManager().getCurrentUser();
        if (currentUser != null) {
            // Construct the full name of the user and display it in the welcome message
            String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
            welcomeLabel.setText("Welcome " + fullName + " to The Reading Room!");
        } else {
            // Fallback message if no user data is available
            welcomeLabel.setText("Welcome to The Reading Room!");
        }
    }

    /**
     * Loads the top 5 books from the book service and displays them in the table.
     * This method overrides the abstract loadBooks method from BookTableController.
     */
    @Override
    protected void loadBooks() {
        booksTableView.getItems().setAll(bookService.getTop5Books());  // Fetch and display the top 5 books
    }
}
