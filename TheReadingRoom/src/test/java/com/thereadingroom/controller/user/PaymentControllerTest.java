package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.model.entity.Order;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.cart.ICartService;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.payment.IPaymentService;
import com.thereadingroom.utils.auth.PaymentValidator;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PaymentController class.
 * This class tests the functionality for handling payments, validating payment details,
 * and interacting with various services (order, cart, and payment).
 */
public class PaymentControllerTest {

    // Inject the PaymentController and mock its dependencies
    @InjectMocks
    private PaymentController paymentController;

    // Mock various services and UI utilities used by the controller
    @Mock
    private IOrderService mockOrderService;

    @Mock
    private ICartService mockCartService;

    @Mock
    private IPaymentService mockPaymentService;

    @Mock
    private UIUtils mockUiUtils;

    @Mock
    private ShoppingCartController mockShoppingCartController;

    @Mock
    private ShoppingCart mockShoppingCart;

    @Mock
    private Stage mockStage;

    // Mocked list of CartTableItems for the test setup
    private List<CartTableItem> mockCartItems;

    /**
     * Initialize the JavaFX Platform before running the tests.
     * This method ensures that JavaFX components can be tested properly.
     */
    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);  // Initializes JavaFX Platform
        latch.await();
    }

    /**
     * Setup the test environment before each test case.
     * This includes initializing mocks and setting up real UI components.
     */
    @BeforeEach
    public void setUp() {
        // Initialize mocks for the controller
        MockitoAnnotations.openMocks(this);

        // Setup real UI components for testing the controller
        paymentController.cardNumberField = new TextField();
        paymentController.cardHolderNameField = new TextField();
        paymentController.expiryDateField = new TextField();
        paymentController.cvvField = new TextField();
        paymentController.totalAmountLabel = new Label();

        // Initialize mock shopping cart with some sample data
        when(mockShoppingCart.getBooks()).thenReturn(Map.of(
                new Book(1, "Book A", "Author A", 10, 15.0, 100), 2,
                new Book(2, "Book B", "Author B", 5, 20.0, 50), 1
        ));
        // Set payment details in the controller for testing
        paymentController.setPaymentDetails(50.0, 1, mockShoppingCart, mockShoppingCartController);
    }

    /**
     * Tests that the payment details are correctly set on the controller.
     */
    @Test
    public void testSetPaymentDetails_shouldSetCorrectDetails() {
        double totalAmount = 50.0;
        int userId = 1;

        // Set the payment details
        paymentController.setPaymentDetails(totalAmount, userId, mockShoppingCart, mockShoppingCartController);

        // Assert that the details are correctly set
        assertEquals(50.0, paymentController.totalAmount);
        assertEquals(1, paymentController.userId);
        assertEquals("Total Amount: $50.00", paymentController.totalAmountLabel.getText());
    }

    /**
     * Tests the payment process for a successful payment.
     * Verifies that the payment is processed, order is placed, and UI is updated.
     */
    @Test
    public void testHandlePayment_shouldProcessPaymentSuccessfully() throws Exception {
        // Mock valid payment details
        paymentController.cardNumberField.setText("1234567890123456");
        paymentController.cardHolderNameField.setText("John Doe");
        paymentController.expiryDateField.setText("12/25");
        paymentController.cvvField.setText("123");

        // Mock a successful payment process and order placement
        when(mockPaymentService.processPayment(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of("ORDER123"));
        when(mockOrderService.placeOrder(any(Order.class))).thenReturn(true);

        // Call the handlePayment method
        paymentController.handlePayment();

        // Verify that the order was placed and UI is updated accordingly
        verify(mockOrderService, times(1)).placeOrder(any(Order.class));
        verify(mockUiUtils, times(1)).showAlert(eq("Payment Successful"), contains("ORDER123"));
        verify(mockShoppingCartController, times(1)).removeCheckedOutItemsFromCart();
    }

    /**
     * Tests the validation failure for an invalid card number during payment.
     * Ensures that validation errors are shown and payment is not processed.
     */
    @Test
    public void testHandlePayment_shouldFailValidationForInvalidCardNumber() {
        // Mock invalid card number
        paymentController.cardNumberField.setText("123");

        // Directly validate the card number using PaymentValidator
        String validationResult = PaymentValidator.validateCardNumber("123");

        // Call the handlePayment method
        paymentController.handlePayment();

        // Verify that the validation error is shown and payment is not processed
        verify(mockUiUtils, times(1)).showError(eq("Payment Failed"), eq(validationResult));
        verify(mockPaymentService, never()).processPayment(anyString(), anyString(), anyString(), anyString());
    }

    /**
     * Tests the behavior when the user cancels the payment.
     * Verifies that reserved stock is reverted and the window is closed.
     */
    @Test
    public void testHandleCancelPayment_shouldRevertStockAndCloseWindow() {
        // Mock the cancel action
        paymentController.handleCancelPayment(null);

        // Verify that the stock is reverted and the window is closed
        verify(mockShoppingCartController, times(1)).revertReservedStock(anyList());
        verify(mockUiUtils, times(1)).closeCurrentWindow(paymentController.totalAmountLabel);
    }

    /**
     * Tests the error handling during payment (e.g., payment failure).
     * Verifies that an error is shown, stock is reverted, and no order is placed.
     */
    @Test
    public void testHandlePayment_shouldHandlePaymentError() throws Exception {
        // Mock valid payment details
        paymentController.cardNumberField.setText("1234567890123456");
        paymentController.cardHolderNameField.setText("John Doe");
        paymentController.expiryDateField.setText("12/25");
        paymentController.cvvField.setText("123");

        // Simulate payment failure by returning an empty Optional
        when(mockPaymentService.processPayment(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Call the handlePayment method
        paymentController.handlePayment();

        // Verify that an error message is shown and the stock is reverted
        verify(mockUiUtils, times(1)).showError(eq("Payment Failed"), anyString());
        verify(mockShoppingCartController, times(1)).revertReservedStock(anyList());
    }

    /**
     * Tests disabling and enabling the form fields during the payment process.
     * Ensures that form fields are properly disabled during payment and re-enabled on error.
     */
    @Test
    public void testFormShouldBeDisabledDuringPaymentAndReEnabledOnError() {
        // Disable the form fields
        paymentController.disableForm();

        // Verify that the form fields are disabled
        assertTrue(paymentController.cardNumberField.isDisable());
        assertTrue(paymentController.cardHolderNameField.isDisable());
        assertTrue(paymentController.expiryDateField.isDisable());
        assertTrue(paymentController.cvvField.isDisable());

        // Enable the form fields again
        paymentController.enableForm();

        // Verify that the form fields are enabled
        assertFalse(paymentController.cardNumberField.isDisable());
        assertFalse(paymentController.cardHolderNameField.isDisable());
        assertFalse(paymentController.expiryDateField.isDisable());
        assertFalse(paymentController.cvvField.isDisable());
    }
}
