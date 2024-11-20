package com.thereadingroom.utils.export;

import com.thereadingroom.model.entity.Order;
import com.thereadingroom.model.entity.OrderItem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting data to CSV files.
 * This class provides a method to export a list of orders and their items into a CSV file.
 */
public class CSVExportUtility {

    /**
     * Exports a list of orders to a CSV file.
     * Each order's date & time, total price, and purchased books with their quantities are written as rows in the CSV.
     *
     * @param orders   The list of orders to be exported.
     * @param filePath The file path where the CSV file will be saved.
     * @return true if the export was successful, false otherwise.
     */
    public static boolean exportOrdersToCSV(List<Order> orders, String filePath) {
        // Try-with-resources to ensure the FileWriter is closed automatically
        try (FileWriter writer = new FileWriter(filePath)) {

            // Write CSV header line
            writer.append("Order Date,Total Price,Book Title,Quantity\n");

            // Iterate over each order
            for (Order order : orders) {
                // Get the date and total price for the current order
                String orderDate = order.getOrderDate().toString();  // Use formatted date-time
                double totalPrice = order.getTotalPrice();

                // Iterate over the items in the order
                for (OrderItem item : order.getOrderItems()) {
                    // Append order date, total price, book title, and quantity as CSV rows
                    writer.append(orderDate)                        // Write order date
                            .append(',')
                            .append(String.valueOf(totalPrice))      // Write total price
                            .append(',')
                            .append(item.getTitle())                 // Write book title
                            .append(',')
                            .append(String.valueOf(item.getQuantity())) // Write quantity
                            .append('\n');                            // End the row
                }
            }

            // Ensure all data is written to the file
            writer.flush();
            System.out.println("Orders exported successfully to " + filePath);
            return true;

        } catch (IOException e) {
            // Handle potential I/O errors
            System.err.println("Error exporting orders to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exports a list of orders to a CSV file for admin users.
     * Each order's number, user ID, total price, and purchased books with their quantities are written as rows in the CSV.
     *
     * @param orders   The list of orders to be exported.
     * @param filePath The file path where the CSV file will be saved.
     * @return true if the export was successful, false otherwise.
     */
    public static boolean exportAdminOrdersToCSV(List<Order> orders, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {

            // Write the admin-specific CSV header
            writer.append("Order Number,User ID,Order Date,Total Price,Book Title,Quantity\n");

            // Iterate over each order
            for (Order order : orders) {
                String orderNumber = order.getOrderNumber();
                String userId = String.valueOf(order.getUserId());
                String orderDate = order.getOrderDate().toString(); // Formatted date
                double totalPrice = order.getTotalPrice();

                // Iterate over each item in the order
                for (OrderItem item : order.getOrderItems()) {
                    writer.append(orderNumber)
                            .append(',')
                            .append(userId)
                            .append(',')
                            .append(orderDate)
                            .append(',')
                            .append(String.valueOf(totalPrice))
                            .append(',')
                            .append(item.getTitle())
                            .append(',')
                            .append(String.valueOf(item.getQuantity()))
                            .append('\n');
                }
            }

            writer.flush();
            System.out.println("Admin orders exported successfully to " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting orders to CSV: " + e.getMessage());
            return false;
        }
    }
}
