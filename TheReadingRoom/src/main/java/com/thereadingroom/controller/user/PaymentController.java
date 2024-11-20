package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.Order;
import com.thereadingroom.model.entity.OrderItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.service.cart.ICartService;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.payment.IPaymentService;
import com.thereadingroom.utils.auth.PaymentValidator;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling payment processing.
 * It includes functionality for payment validation, order saving, stock updates, and cart clearing after payment.
 */
@Controller
public class PaymentController {

    @FXML
    public TextField cardNumberField;  // Input field for card number

    @FXML
    protected TextField cardHolderNameField;  // Input field for cardholder's name

    @FXML
    public TextField expiryDateField;  // Input field for card expiry date

    @FXML
    public TextField cvvField;  // Input field for card CVV

    @FXML
    public Label totalAmountLabel;  // Label displaying the total amount to be paid

    public double totalAmount;  // The total amount for the payment
    public int userId;  // The ID of the user making the payment
    private ShoppingCart shoppingCart;  // Shopping cart containing the user's selected items

    private final IOrderService orderService;  // Service for handling order-related operations
    private final ICartService cartService;  // Service for handling cart-related operations
    private final IPaymentService paymentService;  // Service for handling payment processing
    private final UIUtils uiUtils;  // UI utility for handling common UI-related tasks
    private ShoppingCartController shoppingCartController;  // Controller for managing shopping cart operations
    private Stage paymentStage;  // The stage for the payment window

    /**
     * Constructor for PaymentController with dependency injection.
     *
     * @param orderService         Service for managing orders.
     * @param cartService          Service for managing the shopping cart.
     * @param paymentService       Service for processing payments.
     * @param uiUtils              Utility class for UI operations.
     */
    @Autowired
    public PaymentController(IOrderService orderService, ICartService cartService, IPaymentService paymentService, UIUtils uiUtils) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.uiUtils = uiUtils;
    }

    /**
     * Sets the payment details such as total amount, user ID, shopping cart, and cart controller.
     *
     * @param totalAmount              Total amount to be paid.
     * @param userId                   ID of the user making the payment.
     * @param shoppingCart             The shopping cart containing selected items.
     * @param shoppingCartController   Controller for handling shopping cart operations.
     */
    public void setPaymentDetails(double totalAmount, int userId, ShoppingCart shoppingCart, ShoppingCartController shoppingCartController) {
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.shoppingCart = shoppingCart;
        this.shoppingCartController = shoppingCartController;
        updateTotalAmountLabel();
    }

    /**
     * Sets the stage for the payment window and handles stage close actions.
     *
     * @param paymentStage The stage for the payment window.
     */
    public void setStage(Stage paymentStage) {
        this.paymentStage = paymentStage;

        // Handle payment cancellation when the window is closed
        this.paymentStage.setOnHidden(event -> {
            handleCancelPayment(null);
        });
    }

    /**
     * Handles the cancellation of the payment by closing the payment screen.
     */
    @FXML
    public void handleCancel() {
        closePaymentScreen();
    }

    /**
     * Updates the label displaying the total amount to be paid.
     */
    private void updateTotalAmountLabel() {
        totalAmountLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount));
    }

    /**
     * Handles the payment process, including validation, payment processing, order saving, and stock adjustments.
     */
    @FXML
    public void handlePayment() {
        disableForm();
        if (!validatePaymentDetails()) {
            enableForm();
            return;
        }

        try {
            String orderReference = processPaymentDetails();
            saveOrder(orderReference);
            finalizeStockAfterPayment();
            clearCartAfterPayment();
            uiUtils.showAlert("Payment Successful", "Your payment was successful! Order Reference: " + orderReference);
            closePaymentScreen();
        } catch (Exception e) {
            handlePaymentError();
        } finally {
            enableForm();
        }
    }

    /**
     * Handles the cancellation or closure of the payment window and reverts reserved stock.
     *
     * @param event The window event triggered when the window is closed.
     */
    @FXML
    public void handleCancelPayment(WindowEvent event) {
        // Revert reserved stock when the payment is canceled or the window is closed
        List<CartTableItem> selectedItems = getSelectedCartItems();
        shoppingCartController.revertReservedStock(selectedItems);
        closePaymentScreen();
    }

    /**
     * Validates the payment details entered by the user (card number, expiry date, CVV).
     *
     * @return true if all details are valid, otherwise false.
     */
    private boolean validatePaymentDetails() {
        String cardNumberError = PaymentValidator.validateCardNumber(cardNumberField.getText());
        String expiryDateError = PaymentValidator.validateExpiryDate(expiryDateField.getText());
        String cvvError = PaymentValidator.validateCVV(cvvField.getText());

        if (cardNumberError != null) {
            uiUtils.showError("Payment Failed", cardNumberError);
            return false;
        }
        if (expiryDateError != null) {
            uiUtils.showError("Payment Failed", expiryDateError);
            return false;
        }
        if (cvvError != null) {
            uiUtils.showError("Payment Failed", cvvError);
            return false;
        }
        return true;
    }

    /**
     * Processes the payment with the provided details (card number, cardholder name, expiry date, CVV).
     *
     * @return The order reference if the payment is successful.
     * @throws SQLException if the payment processing fails.
     */
    private String processPaymentDetails() throws SQLException {
        String cardNumber = cardNumberField.getText();
        String cardHolderName = cardHolderNameField.getText();
        String expiryDate = expiryDateField.getText();
        String cvv = cvvField.getText();

        return paymentService.processPayment(cardNumber, cardHolderName, expiryDate, cvv)
                .orElseThrow(() -> new SQLException("Payment processing failed."));
    }

    /**
     * Saves the order after successful payment processing.
     *
     * @param orderReference The reference number of the processed order.
     * @throws SQLException if there is an error saving the order.
     */
    private void saveOrder(String orderReference) throws SQLException {
        List<OrderItem> orderItems = createOrderItems();
        Order order = new Order(orderReference, userId, totalAmount, orderItems);

        if (!orderService.placeOrder(order)) {
            throw new SQLException("Error saving order.");
        }
    }

    /**
     * Finalizes the stock after successful payment by adjusting stock and sold copies for each item.
     */
    private void finalizeStockAfterPayment() {
        List<CartTableItem> selectedItems = shoppingCart.getBooks().entrySet().stream()
                .filter(entry -> shoppingCartController.isBookSelected(entry.getKey()))
                .map(entry -> new CartTableItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            return;
        }

        // Finalize stock and update sold copies in inventory
        shoppingCartController.inventoryService.finalizeStockAfterPayment(selectedItems);
        shoppingCartController.removeCheckedOutItemsFromCart();
    }

    /**
     * Creates a list of order items from the selected books in the shopping cart.
     *
     * @return A list of OrderItem objects representing the items in the order.
     */
    private List<OrderItem> createOrderItems() {
        return shoppingCart.getBooks().entrySet().stream()
                .filter(entry -> shoppingCartController.isBookSelected(entry.getKey()))
                .map(entry -> new OrderItem(entry.getKey().getBookId(), entry.getKey().getTitle(), entry.getValue(), entry.getKey().getPrice()))
                .collect(Collectors.toList());
    }

    /**
     * Clears the selected books from the cart after successful payment.
     *
     * @throws SQLException if there is an error removing books from the cart.
     */
    private void clearCartAfterPayment() throws SQLException {
        List<Book> selectedBooks = shoppingCart.getBooks().keySet().stream()
                .filter(shoppingCartController::isBookSelected)
                .collect(Collectors.toList());

        cartService.removeBooksFromCart(shoppingCart.getCartId(), selectedBooks);
        shoppingCart.removeBooks(selectedBooks);

        shoppingCartController.removeCheckedOutItemsFromCart();
    }

    /**
     * Handles errors during the payment process, reverting reserved stock and displaying an error message.
     */
    private void handlePaymentError() {
        uiUtils.showError("Payment Failed", "An error occurred during payment. Please try again.");
        List<CartTableItem> selectedItems = getSelectedCartItems();
        shoppingCartController.revertReservedStock(selectedItems);
    }

    /**
     * Closes the payment screen after processing is complete or canceled.
     */
    private void closePaymentScreen() {
        uiUtils.closeCurrentWindow(totalAmountLabel);
    }

    /**
     * Disables the payment form fields during processing to prevent further input.
     */
    public void disableForm() {
        cardNumberField.setDisable(true);
        cardHolderNameField.setDisable(true);
        expiryDateField.setDisable(true);
        cvvField.setDisable(true);
    }

    /**
     * Enables the payment form fields after processing is completed.
     */
    public void enableForm() {
        cardNumberField.setDisable(false);
        cardHolderNameField.setDisable(false);
        expiryDateField.setDisable(false);
        cvvField.setDisable(false);
    }

    /**
     * Retrieves the list of selected cart items.
     *
     * @return The list of selected CartTableItem objects.
     */
    private List<CartTableItem> getSelectedCartItems() {
        return shoppingCart.getBooks().entrySet().stream()
                .filter(entry -> shoppingCartController.isBookSelected(entry.getKey()))
                .map(entry -> new CartTableItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
