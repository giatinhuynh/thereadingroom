package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class for handling the addition of new books to the system from the admin panel.
 * This class interacts with the view (FXML) to capture user inputs and perform
 * operations such as adding books to the system's inventory.
 */
@Controller
public class AdminAddBookController {

    @FXML
    private TextField titleField;  // Input field for the book title

    @FXML
    private TextField authorField;  // Input field for the book author

    @FXML
    private TextField stockField;  // Input field for the stock quantity of the book

    @FXML
    private TextField priceField;  // Input field for the price of the book

    private final IBookService bookService;  // Service to handle book-related business logic
    private final UIUtils uiUtils;  // Utility for UI-related functions such as showing alerts or errors

    // Runnable to trigger a refresh of the book list in the parent controller after a book is added
    private Runnable onBookAdded;

    /**
     * Constructor-based dependency injection.
     *
     * @param bookService Service for book-related operations.
     * @param uiUtils Utility for handling UI-related operations.
     */
    @Autowired
    public AdminAddBookController(IBookService bookService, UIUtils uiUtils) {
        this.bookService = bookService;
        this.uiUtils = uiUtils;
    }

    /**
     * Handles the event of adding a new book to the system.
     * Validates the user input and attempts to add the book through the service.
     * Displays appropriate success or error messages.
     */
    @FXML
    public void handleAddBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            int stock = Integer.parseInt(stockField.getText());
            double price = Double.parseDouble(priceField.getText());

            // Validate input fields
            if (title.isEmpty() || author.isEmpty()) {
                uiUtils.showError("Input Error", "Please fill in all fields.");
                return;
            }

            // Create a new book entity
            Book newBook = new Book(0, title, author, stock, price, 0);

            // Attempt to add the book using the service
            boolean success = bookService.addBook(newBook);

            // Show appropriate feedback and close the window if successful
            if (success) {
                uiUtils.showAlert("Success", "Book added successfully!");
                if (onBookAdded != null) {
                    onBookAdded.run();  // Notify the parent controller to refresh book list
                }
                uiUtils.closeCurrentWindow(titleField);  // Close the window after adding the book
            } else {
                uiUtils.showError("Error", "Failed to add the book.");
            }
        } catch (NumberFormatException e) {
            // Handle invalid number input for stock or price
            uiUtils.showError("Input Error", "Invalid input for stock or price.");
        }
    }

    /**
     * Allows the parent controller to provide a callback that runs when a book is successfully added.
     * This is useful for refreshing the book list after a new entry is made.
     *
     * @param onBookAdded A callback to run after a book is added.
     */
    public void setOnBookAdded(Runnable onBookAdded) {
        this.onBookAdded = onBookAdded;
    }

    /**
     * Handles the event of canceling the book addition process.
     * Closes the current window without saving.
     */
    @FXML
    public void handleCancel() {
        uiUtils.closeCurrentWindow(titleField);
    }
}
