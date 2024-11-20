package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartTableItem;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.inventory.InventoryService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CheckoutConfirmationController.
 * This class tests the functionality of the controller, particularly
 * handling checkout confirmation and stock reservation logic.
 */
@SuppressWarnings("unchecked")
public class CheckoutConfirmationControllerTest {

    // The controller under test, with mocks injected
    @InjectMocks
    private CheckoutConfirmationController controller;

    // Mock dependencies
    @Mock
    private UIUtils mockUiUtils;

    @Mock
    private ShoppingCartController mockShoppingCartController;

    @Mock
    private ShoppingCart mockShoppingCart;

    @Mock
    private InventoryService mockInventoryService;

    @Mock
    private Stage mockStage;

    // Mock list of CartTableItems for test setup
    private List<CartTableItem> mockCartItems;

    /**
     * Initializes JavaFX toolkit before any tests run.
     * JavaFX needs to be initialized once for the tests that involve JavaFX components.
     */
    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        AtomicBoolean initialized = new AtomicBoolean(false);
        Platform.startup(() -> initialized.set(true));

        CountDownLatch latch = new CountDownLatch(1);
        if (!initialized.get()) {
            Platform.runLater(latch::countDown);
            latch.await();
        }
    }

    /**
     * Sets up the test environment before each test case.
     * This includes initializing mocks and configuring mock behaviors.
     */
    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Inject mockInventoryService into the mockShoppingCartController
        mockShoppingCartController.inventoryService = mockInventoryService;

        // Setup mock cart items with actual Book objects
        Book book1 = new Book(1, "Book A", "Author A", 10, 15.0, 100);
        Book book2 = new Book(2, "Book B", "Author B", 5, 20.0, 50);
        mockCartItems = List.of(new CartTableItem(book1, 2), new CartTableItem(book2, 1));

        // Initialize real JavaFX components for testing
        controller.checkoutTableView = new TableView<>();
        controller.totalPriceLabel = new Label();

        // Set up table columns for the TableView
        TableColumn<CartTableItem, String> itemNameColumn = new TableColumn<>("Item");
        TableColumn<CartTableItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<CartTableItem, Double> totalAmountColumn = new TableColumn<>("Total Amount");

        // Add the columns to the TableView
        controller.checkoutTableView.getColumns().addAll(itemNameColumn, quantityColumn, totalAmountColumn);
    }

    /**
     * Helper method to run tasks on the JavaFX thread and wait for completion.
     * This ensures the JavaFX thread is available for testing UI components.
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
     * Tests if the checkout details are set correctly in the TableView and total price label.
     */
    @Test
    public void testSetCheckoutDetails_shouldSetTableAndTotalPrice() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            double totalPrice = 25.0;

            // Call the method being tested
            controller.setCheckoutDetails(mockCartItems, totalPrice, mockShoppingCartController, mockShoppingCart);

            // Assert that the table and total price label are updated correctly
            assertEquals(2, controller.checkoutTableView.getItems().size());
            assertEquals("Total Price: $25.00", controller.totalPriceLabel.getText());
        });
    }

    /**
     * Tests the confirm checkout functionality when stock reservation is successful.
     * Verifies that the payment screen is loaded and the current window is closed.
     */
    @Test
    public void testHandleConfirmCheckout_shouldLoadPaymentScreenWhenStockReserved() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            // Mock successful stock reservation
            when(mockInventoryService.reserveStockForCheckout(mockCartItems)).thenReturn(true);

            // Set the mock stage for the controller
            controller.setStage(mockStage);

            // Call the method being tested
            controller.handleConfirmCheckout();

            // Verify that the payment screen is loaded and the current window is closed
            verify(mockUiUtils, times(1)).loadSceneWithDataDefault(anyString(), any(Stage.class), eq("Payment"), any());
            verify(mockUiUtils, times(1)).closeCurrentWindow(controller.totalPriceLabel);
        });
    }

    /**
     * Tests the confirm checkout functionality when stock reservation fails.
     * Verifies that an error message is shown and no payment screen is loaded.
     */
    @Test
    public void testHandleConfirmCheckout_shouldShowErrorWhenStockReservationFails() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            // Mock failed stock reservation
            when(mockInventoryService.reserveStockForCheckout(mockCartItems)).thenReturn(false);

            // Set the mock stage for the controller
            controller.setStage(mockStage);

            // Call the method being tested
            controller.handleConfirmCheckout();

            // Verify that an error message is shown and the payment screen is not loaded
            verify(mockUiUtils, times(1)).showError("Stock Reservation Error", "Unable to reserve stock for one or more items. Please check your cart.");
            verify(mockUiUtils, never()).loadSceneWithDataDefault(anyString(), any(Stage.class), anyString(), any());
        });
    }

    /**
     * Tests the cancel checkout functionality.
     * Verifies that the current window is closed when the user cancels the checkout.
     */
    @Test
    public void testHandleCancelCheckout_shouldCloseStage() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            // Mock the label's scene and window
            Scene mockScene = mock(Scene.class);
            when(controller.totalPriceLabel.getScene()).thenReturn(mockScene);
            when(mockScene.getWindow()).thenReturn(mockStage);

            // Call the method being tested
            controller.handleCancelCheckout();

            // Verify that the current window is closed
            verify(mockUiUtils, times(1)).closeCurrentWindow(controller.totalPriceLabel);
        });
    }

    /**
     * Tests the loading of the payment screen.
     * Verifies that the payment screen is loaded with the correct data.
     */
    @Test
    public void testLoadPaymentScreen_shouldLoadPaymentSceneWithCorrectData() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            double totalAmount = 25.0;

            // Mock the label's scene and window
            Scene mockScene = mock(Scene.class);
            when(controller.totalPriceLabel.getScene()).thenReturn(mockScene);
            when(mockScene.getWindow()).thenReturn(mockStage);

            // Call the method being tested
            controller.loadPaymentScreen(totalAmount, mockShoppingCart);

            // Verify that the payment scene is loaded with the correct data
            verify(mockUiUtils, times(1)).loadSceneWithDataDefault(
                    eq("/com/thereadingroom/fxml/user/payment.fxml"),
                    eq(mockStage),
                    eq("Payment"),
                    any()
            );
        });
    }

    /**
     * Tests the stage closing method directly.
     * Verifies that the current stage is closed successfully.
     */
    @Test
    public void testCloseStage_shouldCloseStageSuccessfully() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
            // Mock the label's scene and window
            Scene mockScene = mock(Scene.class);
            when(controller.totalPriceLabel.getScene()).thenReturn(mockScene);
            when(mockScene.getWindow()).thenReturn(mockStage);

            // Call the method being tested
            controller.closeStage();

            // Verify that the current stage is closed
            verify(mockUiUtils, times(1)).closeCurrentWindow(controller.totalPriceLabel);
        });
    }
}
