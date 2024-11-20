package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for managing book stocks in the admin panel.
 * This class provides functionality to view, search, edit, add, and update book stock.
 */
@Controller
public class AdminStockController {

    @FXML
    private TableView<Book> bookTableView;  // Table view to display books

    @FXML
    private TableColumn<Book, Integer> bookIdColumn;  // Column for displaying book ID

    @FXML
    private TableColumn<Book, String> bookTitleColumn;  // Column for displaying book title

    @FXML
    private TableColumn<Book, String> bookAuthorColumn;  // Column for displaying book author

    @FXML
    private TableColumn<Book, Integer> bookStockColumn;  // Column for displaying stock quantity

    @FXML
    private TableColumn<Book, Double> bookPriceColumn;  // Column for displaying book price

    @FXML
    private TableColumn<Book, Integer> bookSoldCopiesColumn;  // Column for displaying number of sold copies

    @FXML
    private TableColumn<Book, Void> actionColumn;  // Column for displaying action buttons (edit/remove)

    @FXML
    private TextField searchField;  // Input field for searching books

    @FXML
    private TextField bookIdField;  // Input field for book ID for stock updates

    @FXML
    private TextField newStockField;  // Input field for new stock quantity

    private final IBookService bookService;  // Service for handling book-related operations
    private final UIUtils uiUtils;  // Utility for UI-related tasks

    /**
     * Constructor to inject the required services.
     *
     * @param bookService Service to manage book-related operations.
     * @param uiUtils Utility for handling UI-related operations.
     */
    @Autowired
    public AdminStockController(IBookService bookService, UIUtils uiUtils) {
        this.bookService = bookService;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller by setting up the table columns, adding action buttons, loading books, and applying styles.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        addActionButtonsToTable();
        loadBooks();
        uiUtils.loadCSS(bookTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Configures the table columns to display book properties.
     */
    private void setupTableColumns() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookStockColumn.setCellValueFactory(new PropertyValueFactory<>("physicalCopies"));
        bookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bookSoldCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("soldCopies"));
    }

    /**
     * Loads the books from the database and populates the table view.
     */
    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        bookTableView.getItems().setAll(books);
    }

    /**
     * Adds Edit and Remove buttons to each row in the action column.
     */
    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = createEditButton();
                    private final Button removeButton = createRemoveButton();

                    private Button createEditButton() {
                        Button button = new Button("Edit");
                        button.setStyle("-fx-background-color: #d2691e; -fx-text-fill: white;");
                        button.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            handleEditBook(book);
                        });
                        return button;
                    }

                    private Button createRemoveButton() {
                        Button button = new Button("Remove");
                        button.setStyle("-fx-background-color: #ff4500; -fx-text-fill: white;");
                        button.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            handleRemoveBook(book);
                        });
                        return button;
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox actionButtons = new HBox(editButton, removeButton);
                            actionButtons.setSpacing(10);
                            actionButtons.setAlignment(Pos.CENTER);
                            setGraphic(actionButtons);
                        }
                    }
                };
            }
        });
    }

    /**
     * Handles the action of editing a book by loading the edit book modal.
     *
     * @param book The book to be edited.
     */
    private void handleEditBook(Book book) {
        uiUtils.loadModal("/com/thereadingroom/fxml/admin/admin_edit_book.fxml", "Edit Book", controller -> {
            AdminEditBookController editBookController = (AdminEditBookController) controller;
            editBookController.setBook(book);
        }, (Stage) bookTableView.getScene().getWindow());
        loadBooks();  // Reload books after editing
    }

    /**
     * Handles the action of removing a book.
     * Confirms the deletion and reloads the book list after a successful deletion.
     *
     * @param book The book to be removed.
     */
    private void handleRemoveBook(Book book) {
        boolean confirm = uiUtils.showConfirmation("Confirm Deletion", "Are you sure you want to remove the book?");
        if (confirm) {
            boolean success = bookService.deleteBookById(book.getBookId());
            if (success) {
                uiUtils.showAlert("Success", "Book removed successfully!");
                loadBooks();
            } else {
                uiUtils.showError("Error", "Failed to remove book.");
            }
        }
    }

    /**
     * Handles searching books based on the title entered in the search field.
     * If the search field is empty, reloads all books.
     */
    @FXML
    public void handleSearchBooks() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            List<Book> searchResults = bookService.searchBooksByTitle(keyword);
            bookTableView.getItems().setAll(searchResults);
        } else {
            loadBooks();  // Reload all books if the search field is empty
        }
    }

    /**
     * Handles updating the stock of a book based on the entered Book ID and new stock quantity.
     */
    @FXML
    public void handleUpdateStock() {
        try {
            int bookId = Integer.parseInt(bookIdField.getText().trim());
            int newStock = Integer.parseInt(newStockField.getText().trim());

            if (newStock < 0) {
                uiUtils.showError("Invalid Input", "Stock value cannot be negative.");
                return;
            }

            boolean success = bookService.updatePhysicalCopies(bookId, newStock);
            if (success) {
                uiUtils.showAlert("Success", "Stock updated successfully!");
                loadBooks();
            } else {
                uiUtils.showError("Update Failed", "Could not update stock. Please check the Book ID.");
            }
        } catch (NumberFormatException e) {
            uiUtils.showError("Invalid Input", "Please enter valid numbers for Book ID and Stock.");
        }
    }

    /**
     * Handles opening the modal for adding a new book.
     * After the book is added, reloads the book list.
     */
    @FXML
    public void handleAddBook() {
        uiUtils.loadModal("/com/thereadingroom/fxml/admin/admin_add_book.fxml", "Add New Book", controller -> {
            AdminAddBookController addBookController = (AdminAddBookController) controller;
            addBookController.setOnBookAdded(this::loadBooks);
        }, (Stage) bookTableView.getScene().getWindow());
    }
}
