package com.thereadingroom.model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents a customer's order, including the order details such as order number,
 * user information, total price, and the list of ordered items.
 * This class is designed to be immutable in terms of order date and flexible in managing
 * other details like items and total price.
 */
public class Order {

    private int orderId;                   // Unique identifier for the order
    private String orderNumber;            // Unique reference number for the order
    private int userId;                    // The ID of the user who placed the order
    private double totalPrice;             // Total cost of all the items in the order
    private final LocalDateTime orderDate; // Date and time when the order was placed, immutable
    private List<OrderItem> orderItems;    // List of items included in the order

    /**
     * Constructor to create a new Order instance with the current timestamp as the order date.
     * Used when creating new orders at runtime.
     *
     * @param orderNumber The unique order number/reference for this order.
     * @param userId      The ID of the user placing the order.
     * @param totalPrice  The total price of all items in the order.
     * @param orderItems  The list of items that are part of the order.
     */
    public Order(String orderNumber, int userId, double totalPrice, List<OrderItem> orderItems) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
        this.orderDate = LocalDateTime.now();  // Set to current date/time when the order is created
    }

    /**
     * Constructor for creating Order instances when the order date is already known,
     * typically used when retrieving order records from the database.
     *
     * @param orderNumber The unique order number for this order.
     * @param userId      The ID of the user who placed the order.
     * @param totalPrice  The total price of the order.
     * @param orderItems  The list of items included in the order.
     * @param orderDate   The date and time when the order was placed, passed from the database.
     */
    public Order(String orderNumber, int userId, double totalPrice, List<OrderItem> orderItems, LocalDateTime orderDate) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
        this.orderDate = orderDate;  // Date provided from external source, such as a database
    }

    /**
     * Default no-argument constructor for creating empty Order objects.
     * Typically used for testing or dummy instances.
     */
    public Order() {
        this.orderDate = LocalDateTime.now();  // Default to current date/time
    }

    // Getters and setters for the order's fields, allowing for controlled access and modification

    /**
     * Gets the unique ID of the order.
     *
     * @return The order's unique identifier.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Sets the unique ID of the order.
     *
     * @param orderId The unique identifier to be assigned to this order.
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the order's reference number.
     *
     * @return The unique order number for the transaction.
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the unique order number for the transaction.
     *
     * @param orderNumber The order number to set.
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the ID of the user who placed the order.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who placed the order.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the total price of all items in the order.
     *
     * @return The total price of the order.
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Updates the total price of the order.
     * This is useful when modifying or recalculating the order's total (e.g., applying discounts).
     *
     * @param totalPrice The new total price to set.
     */
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the list of items included in the order.
     *
     * @return The list of ordered items.
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Sets the list of items included in the order.
     *
     * @param orderItems The list of order items to set.
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Gets the date and time when the order was placed.
     *
     * @return The LocalDateTime representing when the order was placed.
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Helper method to format the order date in a readable format.
     * Useful for displaying the order date to users in a human-readable way.
     *
     * @return The formatted order date as a string (MM/dd/yyyy HH:mm).
     */
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return orderDate.format(formatter);
    }
}
