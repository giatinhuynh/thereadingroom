package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller class for managing the "View All Books" view.
 * This class provides functionality for displaying and searching through all available books.
 */
@Controller
public class ViewAllBooksController extends BookTableController {

    @FXML
    private TableView<Book> allBooksTableView;  // TableView to display all books

    @FXML
    private TableColumn<Book, String> allTitleColumn;  // Column to display the book title

    @FXML
    private TableColumn<Book, String> allAuthorColumn;  // Column to display the book author

    @FXML
    private TableColumn<Book, String> allPriceColumn;  // Column to display the book price

    @FXML
    private TableColumn<Book, Integer> allStockColumn;  // Column to display the available stock

    @FXML
    private TableColumn<Book, Integer> allSoldCopiesColumn;  // Column to display the sold copies

    @FXML
    private TableColumn<Book, Button> allActionColumn;  // Column for actions (e.g., Add to Cart)

    @FXML
    private TextField searchField;  // Input field for searching books by title

    /**
     * Constructor for the ViewAllBooksController.
     *
     * @param bookService  Service for managing book-related operations.
     * @param cartService  Service for managing cart-related operations.
     * @param uiUtils      Utility class for UI-related operations.
     */
    @Autowired
    public ViewAllBooksController(IBookService bookService, CartService cartService, UIUtils uiUtils) {
        super(bookService, cartService, uiUtils);  // Pass the services to the parent constructor
    }

    /**
     * Initializes the "View All Books" view after the FXML file is loaded.
     * Sets up the book table and applies the necessary styles.
     */
    @FXML
    public void initialize() {
        // Initialize the book table with columns and apply styles
        initializeBookTable(allBooksTableView, allTitleColumn, allAuthorColumn, allPriceColumn, allStockColumn, allSoldCopiesColumn, allActionColumn);
        uiUtils.loadCSS(allBooksTableView, "/com/thereadingroom/css/table-style.css");

        // Ensure that columns resize to fit the table width
        allBooksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load all books initially
        loadBooks();
    }

    /**
     * Loads all available books from the book service and displays them in the table.
     * This method overrides the abstract loadBooks method from BookTableController.
     */
    @Override
    protected void loadBooks() {
        List<Book> allBooks = bookService.getAllBooks();  // Fetch all books
        booksTableView.getItems().setAll(allBooks);  // Display the books in the table
    }

    /**
     * Handles the search functionality.
     * Searches for books by title based on the user's input and displays the results in the table.
     */
    @FXML
    public void handleSearchBooks() {
        String keyword = searchField.getText().trim();  // Get the search keyword

        if (!keyword.isEmpty()) {
            // Fetch and display the search results if a keyword is provided
            List<Book> searchResults = bookService.searchBooksByTitle(keyword);
            booksTableView.getItems().setAll(searchResults);
        } else {
            // Reload all books if the search field is empty
            loadBooks();
        }
    }
}
