package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.inventory.InventoryService;
import com.thereadingroom.utils.JavaFXInitializer;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ShoppingCartController class.
 * This class tests the functionality of the shopping cart, including adding, removing,
 * adjusting item quantities, and checking out.
 */
public class ShoppingCartControllerTest {

    // Inject the ShoppingCartController and mock its dependencies
    @InjectMocks
    private ShoppingCartController controller;

    // Mock the ShoppingCart, CartService, InventoryService, and UIUtils used by the controller
    @Mock
    private ShoppingCart mockShoppingCart;

    @Mock
    private CartService mockCartService;

    @Mock
    private InventoryService mockInventoryService;

    @Mock
    private UIUtils mockUiUtils;

    /**
     * Set up the test environment before each test.
     * This includes initializing the mocks and JavaFX components.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Initialize mocks for the controller
        MockitoAnnotations.openMocks(this);
        JavaFXInitializer.initialize();  // Initialize JavaFX for unit testing

        // Initialize JavaFX components that are part of the ShoppingCartController
        controller.cartTableView = new TableView<>(FXCollections.observableArrayList());
        controller.selectColumn = new TableColumn<>();
        controller.itemNameColumn = new TableColumn<>();
        controller.quantityColumn = new TableColumn<>();
        controller.totalAmountColumn = new TableColumn<>();
        controller.removeColumn = new TableColumn<>();
        controller.totalPriceLabel = new Label();
        controller.totalPrice = new SimpleDoubleProperty(0.0);  // Property to track the total price
    }

    /**
     * Helper method to run tasks on the JavaFX thread and wait for their completion.
     * This is necessary for tests that involve JavaFX components.
     */
    private void runOnFxThreadAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    /**
     * Test case to ensure the shopping cart is correctly loaded with items
     * and that the total price is calculated properly.
     */
    @Test
    public void testSetShoppingCart_shouldLoadItems() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange mock data: Book and Cart setup
            Book book1 = new Book(1, "Test Book", "Author", 5, 10.0, 2);
            when(mockShoppingCart.getBooks()).thenReturn(Map.of(book1, 2));
            when(mockInventoryService.calculateTotalPrice(anyList())).thenReturn(20.0);

            // Act: Set the shopping cart in the controller
            controller.setShoppingCart(mockShoppingCart);

            // Assert: Verify the cart has the correct items and total price
            assertEquals(1, controller.cartTableView.getItems().size());
            assertEquals("Test Book", controller.cartTableView.getItems().get(0).getBook().getTitle());
            assertEquals(20.0, controller.totalPrice.get(), 0.01);
        });
    }

    /**
     * Test case to verify adjusting the quantity of an item in the cart.
     * Ensures that the CartService is called to update the quantity and total price.
     */
    @Test
    public void testAdjustQuantity_shouldUpdateQuantityAndTotalPrice() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: Set up a book and cart item
            Book book1 = new Book(1, "Test Book", "Author", 5, 20.0, 2);
            CartTableItem cartItem = new CartTableItem(book1, 1);
            controller.cartTableView.getItems().add(cartItem);
            when(mockShoppingCart.getCartId()).thenReturn(1);
            controller.setShoppingCart(mockShoppingCart);

            // Act: Adjust the quantity of the cart item
            controller.adjustQuantity(cartItem, 1);  // Increase quantity by 1

            // Assert: Verify the quantity is updated and the CartService is called
            assertEquals(2, cartItem.getQuantity());
            verify(mockCartService, times(1)).updateBookQuantity(1, book1.getBookId(), 2);
        });
    }

    /**
     * Test case to ensure that the quantity of an item cannot be reduced to zero.
     * Verifies that the CartService is not called to update when the quantity is invalid.
     */
    @Test
    public void testAdjustQuantity_shouldNotAllowZeroQuantity() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: Set up a book and cart item
            Book book1 = new Book(1, "Test Book", "Author", 5, 20.0, 2);
            CartTableItem cartItem = new CartTableItem(book1, 1);
            controller.cartTableView.getItems().add(cartItem);
            when(mockShoppingCart.getCartId()).thenReturn(1);

            // Act: Try to reduce the quantity to zero
            controller.adjustQuantity(cartItem, -1);  // Decrease quantity

            // Assert: Ensure quantity remains unchanged and service is not called
            assertEquals(1, cartItem.getQuantity());
            verify(mockCartService, never()).updateBookQuantity(anyInt(), anyInt(), eq(0));
        });
    }

    /**
     * Test case to verify that removing an item from the cart works correctly.
     * Ensures that the item is removed from the shopping cart and UI is updated.
     */
    @Test
    public void testRemoveItem_shouldRemoveItemAndUpdateCart() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: Set up a book and cart item
            Book book1 = new Book(1, "Test Book", "Author", 5, 20.0, 2);
            CartTableItem cartItem = new CartTableItem(book1, 1);

            // Set the mock shopping cart
            when(mockShoppingCart.getCartId()).thenReturn(1);
            controller.setShoppingCart(mockShoppingCart);  // Set the shopping cart before invoking removeItem

            // Add item to the cartTableView
            controller.cartTableView.getItems().add(cartItem);

            // Act: Remove the item from the cart
            controller.removeItem(cartItem);

            // Assert: Ensure the item is removed from the cart and services are updated
            verify(mockShoppingCart, times(1)).removeBook(book1);
            verify(mockCartService, times(1)).removeBookFromCart(1, book1.getBookId());
            assertTrue(controller.cartTableView.getItems().isEmpty());
        });
    }

    /**
     * Test case to verify the behavior when attempting to checkout with no items selected.
     * Ensures that an error message is shown to the user.
     */
    @Test
    public void testHandleCheckout_shouldShowErrorWhenNoItemsSelected() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: No items are selected in the cart
            controller.cartTableView.getItems().add(new CartTableItem(new Book(1, "Book 1", "Author", 5, 10.0, 2), 1));

            // Act: Attempt to checkout
            controller.handleCheckout();

            // Assert: Ensure error message is displayed
            verify(mockUiUtils, times(1)).showAlert("Checkout Error", "No items selected for checkout.");
        });
    }

    /**
     * Test case to handle checkout failure due to unavailable stock.
     * Ensures that an appropriate error message is shown when stock validation fails.
     */
    @Test
    public void testHandleCheckout_shouldShowStockErrorWhenUnavailable() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: Select an item but it's out of stock
            Book book1 = new Book(1, "Book 1", "Author", 5, 10.0, 2);
            CartTableItem cartItem = new CartTableItem(book1, 1);
            cartItem.setSelected(true);
            controller.cartTableView.getItems().add(cartItem);

            when(mockInventoryService.validateStockAvailability(anyList())).thenReturn(false);
            when(mockInventoryService.isStockAvailable(book1, 1)).thenReturn(false);

            // Act: Attempt to checkout
            controller.handleCheckout();

            // Assert: Ensure stock error message is shown
            verify(mockUiUtils, times(1)).showError("Stock Reservation Error", "The book \"Book 1\" is no longer available.");
        });
    }

    /**
     * Test case to handle a successful checkout when stock is available.
     * Ensures that no errors are shown and the checkout proceeds smoothly.
     */
    @Test
    public void testHandleCheckout_shouldProceedToCheckoutWhenStockIsAvailable() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Arrange: Select an item with available stock
            Book book1 = new Book(1, "Book 1", "Author", 5, 10.0, 2);
            CartTableItem cartItem = new CartTableItem(book1, 1);
            cartItem.setSelected(true);
            controller.cartTableView.getItems().add(cartItem);

            when(mockInventoryService.validateStockAvailability(anyList())).thenReturn(true);
            when(mockInventoryService.calculateTotalPrice(anyList())).thenReturn(10.0);

            // Act: Proceed to checkout
            controller.handleCheckout();

            // Assert: Ensure no errors or alerts are shown
            verify(mockUiUtils, never()).showError(anyString(), anyString());
        });
    }
}
