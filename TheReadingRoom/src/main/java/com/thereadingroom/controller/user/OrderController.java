package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Order;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for displaying and managing user orders in the user interface.
 * Provides functionalities such as listing user orders, selecting orders, and exporting selected orders to a CSV file.
 */
@Controller
public class OrderController {

    private List<Order> userOrders;  // List to hold the user's orders
    private final IOrderService orderService;  // Service for order-related operations
    private final UIUtils uiUtils;  // Utility class for UI-related functions
    private final ServiceManager serviceManager;  // ServiceManager for managing user sessions and services
    private final List<CheckBox> orderCheckBoxes = new ArrayList<>();  // List of checkboxes to track selected orders

    @FXML
    private Button exportOrdersButton;  // Button to trigger the export of selected orders

    @FXML
    private ListView<HBox> orderListView;  // ListView to display orders in the user interface

    /**
     * Constructor-based injection of dependencies.
     *
     * @param orderService   The order service for fetching orders.
     * @param uiUtils        The utility class for UI-related tasks.
     * @param serviceManager The service manager for handling session data.
     */
    @Autowired
    public OrderController(IOrderService orderService, UIUtils uiUtils, ServiceManager serviceManager) {
        this.orderService = orderService;
        this.uiUtils = uiUtils;
        this.serviceManager = serviceManager;
    }

    /**
     * Initializes the controller after the FXML file is loaded.
     * Disables the export button and applies custom styles to the order list.
     */
    @FXML
    public void initialize() {
        exportOrdersButton.setDisable(true);
        uiUtils.loadCSS(orderListView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Sets the user ID and loads the user's orders.
     *
     * @param userId The ID of the current user.
     */
    public void setUserId(int userId) {
        if (orderService != null) {
            loadUserOrders(userId);
        } else {
            uiUtils.showError("Error", "OrderService is not initialized.");
        }
    }

    /**
     * Loads the user's orders and populates the ListView.
     *
     * @param userId The ID of the current user.
     */
    private void loadUserOrders(int userId) {
        userOrders = orderService.getAllOrdersByUser(userId);
        orderListView.getItems().clear();
        orderCheckBoxes.clear();

        if (userOrders.isEmpty()) {
            // Display a message if no orders are found
            orderListView.getItems().add(new HBox(new Label("No orders found.")));
            exportOrdersButton.setDisable(true);
        } else {
            // Populate the ListView with orders and enable the export button
            userOrders.forEach(order -> {
                HBox orderItem = createOrderItem(order);
                orderListView.getItems().add(orderItem);
            });
            exportOrdersButton.setDisable(false);
        }
    }

    /**
     * Creates an HBox item for displaying an order with a checkbox for selection.
     *
     * @param order The order to display.
     * @return HBox representing the order.
     */
    private HBox createOrderItem(Order order) {
        CheckBox checkBox = new CheckBox();  // Checkbox to select the order
        Label orderLabel = new Label(formatOrderForDisplay(order));  // Label to display the order details
        orderCheckBoxes.add(checkBox);  // Add the checkbox to the tracking list
        HBox orderBox = new HBox(10, checkBox, orderLabel);  // Layout container for the order
        orderBox.setStyle("-fx-padding: 5px;");
        return orderBox;
    }

    /**
     * Formats the order details for display in the ListView.
     *
     * @param order The order to format.
     * @return A string representing the formatted order details.
     */
    private String formatOrderForDisplay(Order order) {
        StringBuilder orderDetails = new StringBuilder(String.format("Order #%s | Date: %s | Total: $%.2f\n",
                order.getOrderNumber(),
                order.getFormattedOrderDate(),
                order.getTotalPrice()));

        order.getOrderItems().forEach(item -> orderDetails.append(String.format("   - %s (x%d): $%.2f\n",
                item.getTitle(),
                item.getQuantity(),
                item.getPrice())));

        return orderDetails.toString();
    }

    /**
     * Handles the export of selected orders to a CSV file.
     * Prompts the user to select a file location and exports the orders to that file.
     */
    @FXML
    public void handleExportOrders() {
        List<Order> selectedOrders = getSelectedOrders();  // Retrieve selected orders
        if (selectedOrders.isEmpty()) {
            uiUtils.showAlert("No Selection", "Please select at least one order to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();  // File chooser for saving the CSV file
        fileChooser.setTitle("Save Orders as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(exportOrdersButton.getScene().getWindow());  // Show save dialog

        if (file != null) {
            exportOrdersToFile(file, selectedOrders);  // Export orders to the selected file
        }
    }

    /**
     * Retrieves the list of selected orders based on the state of the checkboxes.
     *
     * @return A list of selected orders.
     */
    private List<Order> getSelectedOrders() {
        return userOrders.stream()
                .filter(order -> {
                    int index = userOrders.indexOf(order);
                    return orderCheckBoxes.get(index).isSelected();  // Check if the corresponding checkbox is selected
                })
                .collect(Collectors.toList());
    }

    /**
     * Exports the selected orders to a CSV file.
     *
     * @param file            The file to export the orders to.
     * @param selectedOrders  The list of selected orders to export.
     */
    private void exportOrdersToFile(File file, List<Order> selectedOrders) {
        List<Integer> selectedOrderIds = selectedOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
        boolean success = orderService.exportOrdersToCSV(serviceManager.getSessionManager().getUserId(), selectedOrderIds, file.getAbsolutePath());

        if (success) {
            uiUtils.showAlert("Export Successful", "Orders exported to: " + file.getAbsolutePath());
        } else {
            uiUtils.showError("Export Failed", "Failed to export orders.");
        }
    }
}
