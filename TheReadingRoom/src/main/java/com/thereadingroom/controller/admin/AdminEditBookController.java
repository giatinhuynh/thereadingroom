package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class for handling the editing of book details in the admin panel.
 * This class interacts with the view (FXML) to allow users to modify and save book details.
 */
@Controller
public class AdminEditBookController {

    @FXML
    private TextField titleField;  // Input field for the book title

    @FXML
    private TextField authorField;  // Input field for the book author

    @FXML
    private TextField stockField;  // Input field for the stock quantity (physical copies)

    @FXML
    private TextField priceField;  // Input field for the price of the book

    @FXML
    private TextField soldCopiesField;  // Input field for the number of sold copies

    private final IBookService bookService;  // Service for handling book-related operations
    private final UIUtils uiUtils;  // Utility for handling UI operations like alerts and closing windows

    private Book book;  // Holds the book object that is being edited

    /**
     * Constructor-based dependency injection.
     *
     * @param bookService Service for book-related operations.
     * @param uiUtils Utility for UI-related operations.
     */
    @Autowired
    public AdminEditBookController(IBookService bookService, UIUtils uiUtils) {
        this.bookService = bookService;
        this.uiUtils = uiUtils;
    }

    /**
     * Sets the book to be edited and populates the input fields with the book's current details.
     *
     * @param book The book to be edited.
     */
    public void setBook(Book book) {
        this.book = book;
        populateBookDetails();  // Pre-fill the form with current book details
    }

    /**
     * Populates the form fields with the book's current details to be edited.
     */
    private void populateBookDetails() {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        stockField.setText(String.valueOf(book.getPhysicalCopies()));
        priceField.setText(String.valueOf(book.getPrice()));
        soldCopiesField.setText(String.valueOf(book.getSoldCopies()));
    }

    /**
     * Handles saving the updated book details to the system.
     * Validates input and calls the book service to update the book in the database.
     */
    @FXML
    public void handleSaveBook() {
        try {
            // Update the book entity with the new values from the input fields
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setPhysicalCopies(Integer.parseInt(stockField.getText()));
            book.setPrice(Double.parseDouble(priceField.getText()));
            book.setSoldCopies(Integer.parseInt(soldCopiesField.getText()));

            // Attempt to update the book using the service
            boolean success = bookService.updateBook(book);

            // Show success or error feedback and close the window if successful
            if (success) {
                uiUtils.showAlert("Success", "Book updated successfully!");
                uiUtils.closeCurrentWindow(titleField);  // Close the window after saving the book
            } else {
                uiUtils.showError("Error", "Failed to update book.");
            }
        } catch (NumberFormatException e) {
            // Handle invalid number input for stock, price, or sold copies
            uiUtils.showError("Input Error", "Invalid input for stock, price, or sold copies.");
        }
    }

    /**
     * Handles the cancellation of the edit operation, closing the current window.
     */
    @FXML
    public void handleCancel() {
        uiUtils.closeCurrentWindow(titleField);
    }
}
