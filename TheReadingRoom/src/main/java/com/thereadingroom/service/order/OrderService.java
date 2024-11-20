package com.thereadingroom.service.order;

import com.thereadingroom.model.dao.order.OrderDAO;
import com.thereadingroom.model.entity.Order;
import com.thereadingroom.utils.export.CSVExportUtility;

import java.util.List;

/**
 * OrderService handles operations related to orders, including placing orders,
 * retrieving orders, and exporting orders to CSV.
 * It uses the singleton pattern to ensure there is only one instance of OrderService.
 */
public class OrderService implements IOrderService {

    // Singleton instance of OrderService
    private static OrderService instance;
    private final OrderDAO orderDAO;  // DAO to interact with the database

    // Private constructor to prevent direct instantiation
    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    /**
     * Get the singleton instance of OrderService.
     *
     * @return The single instance of OrderService.
     */
    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    /**
     * Place a new order in the system by saving it to the database.
     *
     * @param order The order to be placed.
     * @return true if the order was successfully placed, false otherwise.
     */
    @Override
    public boolean placeOrder(Order order) {
        return orderDAO.saveOrder(order);
    }

    /**
     * Retrieve all orders from the database. This is typically for admin users.
     *
     * @return A list of all orders.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();  // Method for admin to get all orders
    }

    /**
     * Retrieve all orders placed by a specific user.
     *
     * @param userId The ID of the user whose orders are being retrieved.
     * @return A list of orders made by the user.
     */
    @Override
    public List<Order> getAllOrdersByUser(int userId) {
        return orderDAO.getAllOrdersByUser(userId);
    }

    /**
     * Export selected orders of a user to a CSV file.
     *
     * @param userId The ID of the user.
     * @param orderIds A list of order IDs to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful, false otherwise.
     */
    @Override
    public boolean exportOrdersToCSV(int userId, List<Integer> orderIds, String filePath) {
        List<Order> selectedOrders = orderDAO.getSelectedOrdersByUser(userId, orderIds);
        return CSVExportUtility.exportOrdersToCSV(selectedOrders, filePath);
    }

    /**
     * Admin method to export selected orders to a CSV file.
     *
     * @param orderIds A list of order IDs to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful, false otherwise.
     */
    @Override
    public boolean adminExportOrdersToCSV(List<Integer> orderIds, String filePath) {
        // Fetch orders for admin with detailed information
        List<Order> selectedOrders = orderDAO.fetchOrdersForAdmin(orderIds);

        // Ensure orders are available for export
        if (selectedOrders.isEmpty()) {
            System.out.println("No orders found for the given IDs.");
            return false;
        }

        // Use CSVExportUtility for admin-specific CSV export logic
        return CSVExportUtility.exportAdminOrdersToCSV(selectedOrders, filePath);
    }

    /**
     * Delete an order by its ID from the database.
     *
     * @param orderId The ID of the order to be deleted.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteOrderById(int orderId) {
        return orderDAO.deleteOrderById(orderId);
    }
}
