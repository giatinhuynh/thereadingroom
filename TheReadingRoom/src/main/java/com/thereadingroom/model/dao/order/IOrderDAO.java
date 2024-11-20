package com.thereadingroom.model.dao.order;

import com.thereadingroom.model.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Order Data Access Object (DAO).
 * Defines methods for performing CRUD operations related to orders.
 */
public interface IOrderDAO {

    /**
     * Saves an order in the database.
     *
     * @param order The Order object to save.
     * @return true if the order was saved successfully, false otherwise.
     */
    boolean saveOrder(Order order);

    /**
     * Retrieves all orders made by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Order objects belonging to the user.
     */
    List<Order> getAllOrdersByUser(int userId);

    /**
     * Retrieves an order by its unique ID.
     *
     * @param orderId The ID of the order to retrieve.
     * @return An Optional containing the Order if found, or empty if not found.
     */
    Optional<Order> getOrderById(int orderId);

    /**
     * Retrieves a subset of orders by their IDs, for a specific user.
     *
     * @param userId   The ID of the user.
     * @param orderIds The list of order IDs to retrieve.
     * @return A list of Order objects that match the provided IDs.
     */
    List<Order> getSelectedOrdersByUser(int userId, List<Integer> orderIds);

    /**
     * Retrieves a subset of orders by their IDs, independent of any user.
     *
     * @param orderIds The list of order IDs to retrieve.
     * @return A list of Order objects that match the provided IDs.
     */
    List<Order> getSelectedOrdersByIds(List<Integer> orderIds);

    /**
     * Retrieves all orders from the database.
     *
     * @return A list of all Order objects.
     */
    List<Order> getAllOrders();
}
