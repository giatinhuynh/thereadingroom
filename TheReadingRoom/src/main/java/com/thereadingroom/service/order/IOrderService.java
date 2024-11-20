package com.thereadingroom.service.order;

import com.thereadingroom.model.entity.Order;

import java.util.List;

/**
 * IOrderService defines the contract for order-related operations in the system.
 * Implementing classes are responsible for handling order placement, retrieval,
 * exporting orders to CSV, and deleting orders.
 */
public interface IOrderService {

    /**
     * Place a new order in the system.
     *
     * @param order The order to be placed.
     * @return true if the order was successfully placed, false otherwise.
     */
    boolean placeOrder(Order order);

    /**
     * Retrieve all orders from the system (Admin only).
     *
     * @return A list of all orders.
     */
    List<Order> getAllOrders();  // New method for admin to get all orders

    /**
     * Retrieve all orders placed by a specific user.
     *
     * @param userId The ID of the user whose orders are being retrieved.
     * @return A list of orders placed by the user.
     */
    List<Order> getAllOrdersByUser(int userId);

    /**
     * Export selected orders of a specific user to a CSV file.
     *
     * @param userId   The ID of the user whose orders are to be exported.
     * @param orderIds A list of order IDs to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful, false otherwise.
     */
    boolean exportOrdersToCSV(int userId, List<Integer> orderIds, String filePath);  // Existing export method for users

    /**
     * Admin method to export selected orders to a CSV file (no userId required).
     *
     * @param orderIds A list of order IDs to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful, false otherwise.
     */
    boolean adminExportOrdersToCSV(List<Integer> orderIds, String filePath);  // New method for admin to export orders

    /**
     * Delete an order by its ID.
     *
     * @param orderId The ID of the order to be deleted.
     * @return true if the order was successfully deleted, false otherwise.
     */
    boolean deleteOrderById(int orderId);
}
