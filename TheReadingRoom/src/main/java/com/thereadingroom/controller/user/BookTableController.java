package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

/**
 * Abstract controller for managing the display and interaction with a table of books.
 * Provides the core functionality for showing books in a TableView, allowing users to add books to their cart.
 */
@Controller
public abstract class BookTableController {

    protected ShoppingCart shoppingCart;  // The user's shopping cart
    protected final IBookService bookService;  // Service to handle book-related operations
    protected final CartService cartService;  // Service to handle cart-related operations
    protected final UIUtils uiUtils;  // UI utility for showing dialogs and handling UI updates

    protected TableView<Book> booksTableView;  // Table view to display books
    protected TableColumn<Book, String> titleColumn;  // Column for book titles
    protected TableColumn<Book, String> authorColumn;  // Column for book authors
    protected TableColumn<Book, String> priceColumn;  // Column for book prices
    protected TableColumn<Book, Integer> stockColumn;  // Column for available stock
    protected TableColumn<Book, Integer> soldCopiesColumn;  // Column for sold copies
    protected TableColumn<Book, Button> actionColumn;  // Column for the action button (Add to Cart)

    @Autowired
    public BookTableController(IBookService bookService, CartService cartService, UIUtils uiUtils) {
        this.bookService = bookService;
        this.cartService = cartService;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the TableView with the provided columns and loads the books into the table.
     *
     * @param tableView The TableView instance.
     * @param titleColumn The column for displaying book titles.
     * @param authorColumn The column for displaying book authors.
     * @param priceColumn The column for displaying book prices.
     * @param stockColumn The column for displaying available stock.
     * @param soldCopiesColumn The column for displaying sold copies.
     * @param actionColumn The column for the "Add to Cart" action buttons.
     */
    protected void initializeBookTable(TableView<Book> tableView, TableColumn<Book, String> titleColumn,
                                       TableColumn<Book, String> authorColumn, TableColumn<Book, String> priceColumn,
                                       TableColumn<Book, Integer> stockColumn, TableColumn<Book, Integer> soldCopiesColumn,
                                       TableColumn<Book, Button> actionColumn) {
        this.booksTableView = tableView;
        this.titleColumn = titleColumn;
        this.authorColumn = authorColumn;
        this.priceColumn = priceColumn;
        this.stockColumn = stockColumn;
        this.soldCopiesColumn = soldCopiesColumn;
        this.actionColumn = actionColumn;

        // Set cell value factories for each column
        titleColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAuthor()));
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(String.format("$%.2f", data.getValue().getPrice())));
        stockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(bookService.findBookById(data.getValue().getBookId()).getPhysicalCopies()));
        soldCopiesColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSoldCopies()));

        // Set the action column to display "Add to Cart" buttons
        actionColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(createAddButton(data.getValue())));

        // Load books into the table
        loadBooks();
    }

    /**
     * Creates the "Add to Cart" button for each book in the table.
     *
     * @param book The book to add to the cart.
     * @return A Button with an action listener that adds the book to the cart.
     */
    private Button createAddButton(Book book) {
        Button addButton = new Button("Add to Cart");
        addButton.setStyle("-fx-background-color: #d2691e; -fx-text-fill: white;");
        addButton.setOnAction(event -> {
            handleAddButton(book);  // Handle adding the book to the cart
            event.consume();
        });
        return addButton;
    }

    /**
     * Handles the action when the "Add to Cart" button is clicked.
     * Prompts the user for the quantity and validates the stock before adding the book to the cart.
     *
     * @param book The book to add.
     */
    private void handleAddButton(Book book) {
        Optional<String> result = showQuantityDialog();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = parseQuantity(quantityStr);
                validateStockAndAddToCart(book, quantity);
                uiUtils.showAlert("Success", quantity + " copies added to your cart.");
            } catch (IllegalArgumentException e) {
                uiUtils.showError("Invalid Input", e.getMessage());
            }
        });
    }

    /**
     * Parses the quantity entered by the user.
     *
     * @param quantityStr The quantity as a string.
     * @return The parsed quantity as an integer.
     * @throws IllegalArgumentException if the quantity is invalid.
     */
    private int parseQuantity(String quantityStr) throws IllegalArgumentException {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0.");
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number.");
        }
    }

    /**
     * Validates the stock availability for the book and adds it to the cart if stock is sufficient.
     *
     * @param book The book to add.
     * @param quantity The number of copies to add.
     * @throws IllegalArgumentException if stock is insufficient or the cart is not initialized.
     */
    private void validateStockAndAddToCart(Book book, int quantity) throws IllegalArgumentException {
        if (shoppingCart == null) {
            throw new IllegalArgumentException("Shopping cart is not initialized.");
        }

        Book latestBook = bookService.findBookById(book.getBookId());
        int availableStock = latestBook.getPhysicalCopies();

        if (availableStock < quantity) {
            throw new IllegalArgumentException("Only " + availableStock + " copies available.");
        }

        // Add the book to the cart if stock is valid
        updateCart(book, quantity);
    }

    /**
     * Updates the shopping cart with the selected book and quantity.
     *
     * @param book The book to add.
     * @param quantity The number of copies to add.
     */
    private void updateCart(Book book, int quantity) {
        shoppingCart.addBook(book, quantity);
        int cartId = cartService.getOrCreateCart(shoppingCart.getUserId());
        cartService.addOrUpdateBookInCart(cartId, book.getBookId(), quantity);
        syncCartWithDatabase(cartId);  // Sync the cart with the database
    }

    /**
     * Syncs the cart with the database to ensure it is up to date.
     *
     * @param cartId The ID of the shopping cart.
     */
    private void syncCartWithDatabase(int cartId) {
        List<CartItem> updatedCartItems = cartService.getCartItems(cartId);
        shoppingCart.clearCart();
        for (CartItem item : updatedCartItems) {
            Book cartBook = bookService.findBookById(item.getBookId());
            shoppingCart.addBook(cartBook, item.getQuantity());
        }
    }

    /**
     * Displays a dialog box prompting the user for the quantity of books to add to the cart.
     *
     * @return An Optional containing the user's input.
     */
    private Optional<String> showQuantityDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Quantity");
        dialog.setHeaderText("How many copies would you like to add?");
        return dialog.showAndWait();
    }

    /**
     * Abstract method to load books into the table.
     * Implementations should fetch books from the database and populate the table view.
     */
    protected abstract void loadBooks();

    /**
     * Sets the shopping cart for the controller.
     *
     * @param shoppingCart The user's shopping cart.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
