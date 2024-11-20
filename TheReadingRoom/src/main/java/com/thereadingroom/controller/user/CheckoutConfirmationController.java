package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller responsible for handling checkout confirmation in the user interface.
 * Manages the display of the checkout table, total price, and handles the confirmation or cancellation of the checkout process.
 */
@Controller
public class CheckoutConfirmationController {

    @FXML
    public TableView<CartTableItem> checkoutTableView;  // TableView for displaying items in the checkout

    @FXML
    public Label totalPriceLabel;  // Label for displaying the total price

    private List<CartTableItem> cartItems;  // List of items in the user's cart
    private double totalPrice;  // Total price of the cart items
    private ShoppingCart shoppingCart;  // The user's shopping cart
    private ShoppingCartController shoppingCartController;  // Controller managing the shopping cart

    private Stage stage;  // The current stage (window) for this controller

    @Autowired
    private UIUtils uiUtils;  // Utility class for managing UI operations

    /**
     * Initializes the controller and applies custom styles to the TableView.
     */
    @FXML
    public void initialize() {
        applyStyles();
    }

    /**
     * Applies custom CSS styles to the checkout table.
     */
    private void applyStyles() {
        uiUtils.loadCSS(checkoutTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Sets the current stage for this controller.
     *
     * @param stage The current stage (window).
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Populates the checkout table with items and displays the total price.
     *
     * @param cartItems               The list of items in the cart.
     * @param totalPrice              The total price of the items.
     * @param shoppingCartController  The controller managing the shopping cart.
     * @param shoppingCart            The user's shopping cart.
     */
    public void setCheckoutDetails(List<CartTableItem> cartItems, double totalPrice, ShoppingCartController shoppingCartController, ShoppingCart shoppingCart) {
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.shoppingCartController = shoppingCartController;
        this.shoppingCart = shoppingCart;

        // Set up the table view columns
        checkoutTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        checkoutTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checkoutTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Populate the table with cart items
        checkoutTableView.setItems(FXCollections.observableArrayList(cartItems));

        // Display the total price
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }

    /**
     * Handles the confirmation of the checkout process. If stock is successfully reserved, the payment screen is loaded.
     * If stock reservation fails, an error is displayed.
     */
    @FXML
    public void handleConfirmCheckout() {
        boolean reservationSuccessful = shoppingCartController.inventoryService.reserveStockForCheckout(cartItems);

        if (reservationSuccessful) {
            // If stock reservation is successful, load the payment screen
            loadPaymentScreen(totalPrice, shoppingCart);
            // Close the current checkout stage
            closeStage();
        } else {
            // If stock reservation fails, show an error message
            uiUtils.showError("Stock Reservation Error", "Unable to reserve stock for one or more items. Please check your cart.");
        }
    }

    /**
     * Cancels the checkout process and closes the current window.
     */
    @FXML
    public void handleCancelCheckout() {
        closeStage();
    }

    /**
     * Loads the payment screen after successful stock reservation.
     *
     * @param totalAmount   The total price for the checkout.
     * @param shoppingCart  The user's shopping cart.
     */
    public void loadPaymentScreen(double totalAmount, ShoppingCart shoppingCart) {
        Stage currentStage = (Stage) totalPriceLabel.getScene().getWindow();  // Get current stage

        // Load the payment scene and pass the shopping cart and total price to the PaymentController
        uiUtils.loadSceneWithDataDefault("/com/thereadingroom/fxml/user/payment.fxml", currentStage, "Payment", controller -> {
            PaymentController paymentController = (PaymentController) controller;
            int userId = shoppingCartController.serviceManager.getSessionManager().getUserId();  // Get the current user ID
            paymentController.setPaymentDetails(totalAmount, userId, shoppingCart, shoppingCartController);
            paymentController.setStage(currentStage);  // Set the stage in the PaymentController
        });
    }

    /**
     * Closes the current stage (window).
     */
    public void closeStage() {
        Stage currentStage = (Stage) totalPriceLabel.getScene().getWindow();  // Get the current stage

        // Close the stage using UIUtils if it's not null
        if (currentStage != null) {
            uiUtils.closeCurrentWindow(totalPriceLabel);  // Use UIUtils to close the window
        }
    }
}
