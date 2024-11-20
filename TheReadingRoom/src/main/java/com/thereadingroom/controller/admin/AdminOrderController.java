package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Order;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing orders in the admin panel.
 * Provides functionalities to view, filter, sort, remove, and export orders.
 */
@Controller
public class AdminOrderController {

    @FXML
    private TableView<Order> orderTableView;  // Table view for displaying the list of orders

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;  // Column for displaying order ID

    @FXML
    private TableColumn<Order, Integer> userIdColumn;  // Column for displaying user ID

    @FXML
    private TableColumn<Order, String> orderNumberColumn;  // Column for displaying order number

    @FXML
    private TableColumn<Order, Double> orderTotalColumn;  // Column for displaying total order price

    @FXML
    private TableColumn<Order, String> orderDateColumn;  // Column for displaying order date

    @FXML
    private TableColumn<Order, Void> actionColumn;  // Column for action buttons (Remove)

    @FXML
    private TextField filterUserIdField;  // Input field for filtering orders by user ID

    @FXML
    private ComboBox<String> sortComboBox;  // Combo box for selecting sorting options

    @FXML
    private Button exportOrdersButton;  // Button for exporting selected orders

    private final IOrderService orderService;  // Service for handling order-related operations
    private final UIUtils uiUtils;  // Utility instance for UI-related tasks

    private List<Order> allOrders;  // List to store all fetched orders

    /**
     * Constructor-based dependency injection for OrderService and UIUtils.
     *
     * @param orderService The service for managing order operations.
     * @param uiUtils Utility for handling UI-related tasks.
     */
    @Autowired
    public AdminOrderController(IOrderService orderService, UIUtils uiUtils) {
        this.orderService = orderService;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller after the components are fully loaded.
     * Sets up the table columns, action buttons, sorting options, and loads orders.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionButtons();
        setupSortComboBox();
        loadOrders();
        uiUtils.loadCSS(orderTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Configures the columns of the order table with appropriate data fields.
     */
    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedOrderDate"));

        orderTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // Allow multiple selections
    }

    /**
     * Adds action buttons (Remove) to the action column for each order row.
     */
    private void setupActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                return new TableCell<>() {
                    private final Button removeButton = new Button("Remove");

                    {
                        removeButton.setOnAction(e -> {
                            Order order = getTableView().getItems().get(getIndex());
                            handleRemoveOrder(order);  // Handle order removal
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);  // No button for empty rows
                        } else {
                            setGraphic(removeButton);  // Show remove button for each row
                        }
                    }
                };
            }
        });
    }

    /**
     * Sets up sorting options in the ComboBox (by Order Date or Total Price).
     */
    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("Order Date", "Total Price"));
    }

    /**
     * Loads all orders from the database and populates the table view.
     */
    private void loadOrders() {
        allOrders = orderService.getAllOrders();  // Fetch all orders from the service
        updateTableView(allOrders);  // Update the table view with the fetched orders
    }

    /**
     * Applies filtering and sorting to the orders based on user input and selections.
     * Updates the table view accordingly.
     */
    @FXML
    public void applyFilterAndSort() {
        List<Order> filteredOrders = applyFilter(allOrders, filterUserIdField.getText().trim());
        List<Order> sortedOrders = applySort(filteredOrders, sortComboBox.getValue());
        updateTableView(sortedOrders);
    }

    /**
     * Filters the orders based on user ID input.
     *
     * @param orders List of orders to be filtered.
     * @param userIdFilter The user ID to filter orders by.
     * @return A list of filtered orders matching the user ID.
     */
    private List<Order> applyFilter(List<Order> orders, String userIdFilter) {
        if (userIdFilter.isEmpty()) {
            return orders;  // Return all orders if no filter is applied
        }
        return orders.stream()
                .filter(order -> String.valueOf(order.getUserId()).equals(userIdFilter))
                .collect(Collectors.toList());
    }

    /**
     * Sorts the orders based on the selected sort option (Order Date or Total Price).
     *
     * @param orders List of orders to be sorted.
     * @param sortOption The sorting option selected by the user.
     * @return A sorted list of orders.
     */
    private List<Order> applySort(List<Order> orders, String sortOption) {
        if ("Order Date".equals(sortOption)) {
            orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));  // Sort by date (latest first)
        } else if ("Total Price".equals(sortOption)) {
            orders.sort((o1, o2) -> Double.compare(o2.getTotalPrice(), o1.getTotalPrice()));  // Sort by price (highest first)
        }
        return orders;
    }

    /**
     * Updates the table view with the provided list of orders.
     *
     * @param orders The list of orders to display in the table view.
     */
    private void updateTableView(List<Order> orders) {
        orderTableView.setItems(FXCollections.observableArrayList(orders));  // Set the orders to the table view
    }

    /**
     * Handles exporting selected orders to a CSV file.
     * Opens a file chooser for saving the exported file.
     */
    @FXML
    public void handleExportOrders() {
        List<Order> selectedOrders = orderTableView.getSelectionModel().getSelectedItems();

        if (selectedOrders.isEmpty()) {
            uiUtils.showError("No Selection", "Please select at least one order to export.");
            return;  // Exit if no orders are selected
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(exportOrdersButton.getScene().getWindow());  // Open save dialog

        if (file != null) {
            exportSelectedOrdersToAdminCSV(selectedOrders, file);  // Export selected orders if a file is chosen
        }
    }

    /**
     * Exports the selected orders to a CSV file.
     *
     * @param selectedOrders The list of orders to export.
     * @param file The file where the CSV will be saved.
     */
    private void exportSelectedOrdersToAdminCSV(List<Order> selectedOrders, File file) {
        List<Integer> selectedOrderIds = selectedOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
        boolean success = orderService.adminExportOrdersToCSV(selectedOrderIds, file.getAbsolutePath());

        if (success) {
            uiUtils.showAlert("Export Successful", "Orders exported successfully to: " + file.getAbsolutePath());
        } else {
            uiUtils.showError("Export Failed", "Failed to export selected orders.");
        }
    }

    /**
     * Handles the removal of an order after confirming the action.
     *
     * @param order The order to be removed.
     */
    private void handleRemoveOrder(Order order) {
        boolean confirm = uiUtils.showConfirmation("Confirm Deletion", "Are you sure you want to remove this order?");
        if (confirm) {
            boolean success = orderService.deleteOrderById(order.getOrderId());
            if (success) {
                uiUtils.showAlert("Success", "Order removed successfully!");
                loadOrders();  // Reload orders after successful removal
            } else {
                uiUtils.showError("Error", "Failed to remove order.");
            }
        }
    }
}
