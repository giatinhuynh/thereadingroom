package com.thereadingroom.service.CSVExport;

import com.thereadingroom.model.dao.order.OrderDAO;
import com.thereadingroom.model.entity.Order;
import com.thereadingroom.utils.export.CSVExportUtility;

import java.util.List;
import java.util.Optional;

/**
 * CSVExportService handles the exporting of data to CSV files.
 * It is responsible for coordinating data retrieval and delegating CSV generation to CSVExportUtility.
 */
public class CSVExportService implements ICSVExportService {

    // Singleton instance
    private static CSVExportService instance;
    private final OrderDAO orderDAO;

    // Private constructor for Singleton pattern
    private CSVExportService() {
        this.orderDAO = new OrderDAO();
    }

    // Singleton - Get the single instance of CSVExportService
    public static synchronized CSVExportService getInstance() {
        if (instance == null) {
            instance = new CSVExportService();
        }
        return instance;
    }

    /**
     * Export all orders of a specific user to a CSV file.
     *
     * @param userId   ID of the user whose orders are to be exported.
     * @param filePath File path where the CSV file will be saved.
     * @return true if the export was successful; false otherwise.
     */
    public boolean exportAllOrdersByUserToCSV(int userId, String filePath) {
        // Retrieve all orders for the user
        List<Order> orders = orderDAO.getAllOrdersByUser(userId);

        // Check if there are orders to export
        if (orders.isEmpty()) {
            System.out.println("No orders found for user with ID: " + userId);
            return false;
        }

        // Use CSVExportUtility to export the orders to a CSV file
        return CSVExportUtility.exportOrdersToCSV(orders, filePath);
    }

    /**
     * Export selected orders to a CSV file.
     *
     * @param orderIds List of order IDs to be exported.
     * @param filePath File path where the CSV file will be saved.
     * @return true if the export was successful; false otherwise.
     */
    public boolean exportSelectedOrdersToCSV(List<Integer> orderIds, String filePath) {
        // Retrieve selected orders
        List<Order> selectedOrders = orderDAO.getSelectedOrdersByIds(orderIds);

        // Check if there are orders to export
        if (selectedOrders.isEmpty()) {
            System.out.println("No orders found for the given IDs.");
            return false;
        }

        // Use CSVExportUtility to export the selected orders to a CSV file
        return CSVExportUtility.exportOrdersToCSV(selectedOrders, filePath);
    }

    /**
     * Export a single order to a CSV file.
     *
     * @param orderId  ID of the order to be exported.
     * @param filePath File path where the CSV file will be saved.
     * @return true if the export was successful; false otherwise.
     */
    public boolean exportOrderByIdToCSV(int orderId, String filePath) {
        // Retrieve the order by its ID
        Optional<Order> orderOptional = orderDAO.getOrderById(orderId);

        // Check if order is present
        if (orderOptional.isEmpty()) {
            System.out.println("No order found with ID: " + orderId);
            return false;
        }

        // Use CSVExportUtility to export the order to a CSV file
        return CSVExportUtility.exportOrdersToCSV(List.of(orderOptional.get()), filePath);
    }

    /**
     * Export selected orders for admin to a CSV file.
     *
     * @param orderIds List of order IDs to be exported.
     * @param filePath File path where the CSV file will be saved.
     * @return true if the export was successful; false otherwise.
     */
    public boolean adminExportSelectedOrdersToCSV(List<Integer> orderIds, String filePath) {
        // Use OrderDAO to fetch orders for admin with detailed information
        List<Order> selectedOrders = orderDAO.fetchOrdersForAdmin(orderIds);

        if (selectedOrders.isEmpty()) {
            System.out.println("No orders found for the given IDs.");
            return false;
        }

        // Use the CSVExportUtility for admin-specific export logic
        return CSVExportUtility.exportAdminOrdersToCSV(selectedOrders, filePath);
    }

}
