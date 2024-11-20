package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.inventory.InventoryService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing the shopping cart in the user interface.
 * Provides functionality for displaying cart items, handling quantity adjustments, removing items,
 * and checking out selected items.
 */
@Controller
public class ShoppingCartController {

    @FXML
    protected TableView<CartTableItem> cartTableView;  // TableView displaying the cart items
    @FXML
    protected TableColumn<CartTableItem, Boolean> selectColumn;  // Column for selecting items
    @FXML
    protected TableColumn<CartTableItem, String> itemNameColumn;  // Column for the item names
    @FXML
    protected TableColumn<CartTableItem, Integer> quantityColumn;  // Column for the quantity of items
    @FXML
    protected TableColumn<CartTableItem, Double> totalAmountColumn;  // Column for the total amount of each item
    @FXML
    protected TableColumn<CartTableItem, String> removeColumn;  // Column for removing items
    @FXML
    protected Label totalPriceLabel;  // Label displaying the total price of selected items

    protected ShoppingCart shoppingCart;  // The shopping cart containing the user's items
    protected final CartService cartService;  // Service for handling cart operations
    protected InventoryService inventoryService;  // Service for handling inventory-related operations
    protected final UIUtils uiUtils;  // Utility for handling UI-related tasks
    protected final ServiceManager serviceManager;  // Service manager for session and service-related operations

    protected SimpleDoubleProperty totalPrice = new SimpleDoubleProperty(0.0);  // Property for total price binding

    /**
     * Constructor for ShoppingCartController, with dependencies injected.
     *
     * @param cartService      Service for handling cart operations.
     * @param inventoryService Service for handling inventory-related operations.
     * @param uiUtils          Utility class for handling UI operations.
     * @param serviceManager   Service manager for handling services like session management.
     */
    @Autowired
    public ShoppingCartController(CartService cartService, InventoryService inventoryService, UIUtils uiUtils, ServiceManager serviceManager) {
        this.cartService = cartService;
        this.inventoryService = inventoryService;
        this.uiUtils = uiUtils;
        this.serviceManager = serviceManager;
    }

    /**
     * Initializes the controller, sets up the table columns, and binds the total price label.
     */
    @FXML
    public void initialize() {
        cartTableView.sceneProperty().addListener((observableValue, scene, newScene) -> {
            if (newScene != null) {
                loadStylesheet();
                cartTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            }
        });

        setupTableColumns();
        totalPriceLabel.textProperty().bind(totalPrice.asString("Total Price: $%.2f"));
    }

    /**
     * Configures the table columns with cell factories and sets up action buttons.
     */
    private void setupTableColumns() {
        selectColumn.setCellFactory(tableColumn -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean isSelected, boolean empty) {
                super.updateItem(isSelected, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartTableItem cartItem = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(cartItem.isSelected());
                    checkBox.selectedProperty().addListener((observableValue, e, newValue) -> {
                        cartItem.setSelected(newValue);
                        updateTotalPrice();
                    });
                    setGraphic(checkBox);
                }
            }
        });

        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().itemNameProperty());
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());

        quantityColumn.setCellFactory(tableColumn -> new TableCell<>() {
            private final Button incrementButton = new Button("+");
            private final Button decrementButton = new Button("-");
            private final Label quantityLabel = new Label();
            private final HBox container = new HBox(10, decrementButton, quantityLabel, incrementButton);

            {
                container.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartTableItem cartItem = getTableView().getItems().get(getIndex());
                    quantityLabel.setText(String.valueOf(cartItem.getQuantity()));
                    setGraphic(container);

                    decrementButton.setOnAction(event -> adjustQuantity(cartItem, -1));
                    incrementButton.setOnAction(event -> adjustQuantity(cartItem, 1));
                }
            }
        });

        removeColumn.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty("Remove"));
        removeColumn.setCellFactory(tableColumn -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                    removeButton.setOnAction(event -> {
                        CartTableItem cartItem = getTableView().getItems().get(getIndex());
                        removeItem(cartItem);
                    });
                }
            }
        });
    }

    /**
     * Loads the custom CSS for the table view.
     */
    private void loadStylesheet() {
        uiUtils.loadCSS(cartTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Sets the shopping cart and loads its items into the table view.
     *
     * @param shoppingCart The shopping cart object containing the user's selected items.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        loadCartItems();
    }

    /**
     * Loads the items from the shopping cart into the table view.
     */
    private void loadCartItems() {
        List<CartTableItem> cartItems = shoppingCart.getBooks().entrySet().stream()
                .map(entry -> new CartTableItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        cartTableView.getItems().setAll(cartItems);
        updateTotalPrice();
    }

    /**
     * Updates the total price of selected items in the cart.
     */
    private void updateTotalPrice() {
        totalPrice.set(inventoryService.calculateTotalPrice(cartTableView.getItems().stream()
                .filter(CartTableItem::isSelected)
                .collect(Collectors.toList())));
    }

    /**
     * Handles the checkout process by validating stock availability and loading the checkout confirmation.
     */
    @FXML
    public void handleCheckout() {
        List<CartTableItem> selectedItems = cartTableView.getItems().stream()
                .filter(CartTableItem::isSelected)
                .toList();

        if (selectedItems.isEmpty()) {
            uiUtils.showAlert("Checkout Error", "No items selected for checkout.");
            return;
        }

        // Revalidate stock before proceeding to checkout
        if (!inventoryService.validateStockAvailability(selectedItems)) {
            CartTableItem soldOutItem = selectedItems.stream()
                    .filter(item -> !inventoryService.isStockAvailable(item.getBook(), item.getQuantity()))
                    .findFirst()
                    .orElse(null);

            if (soldOutItem != null) {
                String bookTitle = soldOutItem.getBook().getTitle();
                uiUtils.showError("Stock Reservation Error", "The book \"" + bookTitle + "\" is no longer available.");
            } else {
                uiUtils.showError("Stock Reservation Error", "Unable to proceed due to stock availability issues.");
            }
            return;
        }

        // Load checkout confirmation modal
        double totalAmount = inventoryService.calculateTotalPrice(selectedItems);
        Stage confirmationStage = new Stage();
        uiUtils.loadModal("/com/thereadingroom/fxml/user/checkout_confirmation.fxml", "Confirm Checkout", controller -> {
            CheckoutConfirmationController confirmationController = (CheckoutConfirmationController) controller;
            confirmationController.setCheckoutDetails(selectedItems, totalAmount, this, shoppingCart);
            confirmationController.setStage(confirmationStage);
        }, confirmationStage);
    }

    /**
     * Removes the checked-out items from the cart after a successful checkout.
     */
    protected void removeCheckedOutItemsFromCart() {
        List<CartTableItem> selectedItems = cartTableView.getItems().stream()
                .filter(CartTableItem::isSelected)
                .toList();

        List<Book> booksToRemove = selectedItems.stream()
                .map(CartTableItem::getBook)
                .toList();

        cartService.removeBooksFromCart(shoppingCart.getCartId(), booksToRemove);
        shoppingCart.removeBooks(booksToRemove);
        cartTableView.getItems().removeAll(selectedItems);
    }

    /**
     * Checks if a specific book is selected in the cart.
     *
     * @param book The book to check.
     * @return true if the book is selected, otherwise false.
     */
    public boolean isBookSelected(Book book) {
        return cartTableView.getItems().stream()
                .filter(cartItem -> cartItem.getBook().equals(book))
                .anyMatch(CartTableItem::isSelected);
    }

    /**
     * Removes a specific item from the cart.
     *
     * @param cartItem The cart item to remove.
     */
    public void removeItem(CartTableItem cartItem) {
        shoppingCart.removeBook(cartItem.getBook());
        cartService.removeBookFromCart(shoppingCart.getCartId(), cartItem.getBook().getBookId());
        cartTableView.getItems().remove(cartItem);
        updateTotalPrice();
    }

    /**
     * Adjusts the quantity of a specific item in the cart.
     *
     * @param cartItem   The cart item to adjust.
     * @param adjustment The adjustment value (+1 to increment, -1 to decrement).
     */
    protected void adjustQuantity(CartTableItem cartItem, int adjustment) {
        int newQuantity = cartItem.getQuantity() + adjustment;
        if (newQuantity > 0) {
            cartItem.setQuantity(newQuantity);
            shoppingCart.updateBookQuantity(cartItem.getBook(), newQuantity);
            cartService.updateBookQuantity(shoppingCart.getCartId(), cartItem.getBook().getBookId(), newQuantity);
            cartTableView.refresh();
            updateTotalPrice();
        }
    }

    /**
     * Reverts reserved stock if checkout is canceled or fails.
     *
     * @param selectedItems The items for which stock was reserved.
     */
    public void revertReservedStock(List<CartTableItem> selectedItems) {
        inventoryService.revertReservedStock(selectedItems);
    }
}
