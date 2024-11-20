package com.thereadingroom.service.CSVExport;

/**
 * ICSVExportService defines the contract for exporting data to CSV files.
 * It declares methods for exporting orders by user, by order IDs, or by a specific order ID.
 */
public interface ICSVExportService {

    /**
     * Export all orders of a specific user to a CSV file.
     *
     * @param userId   The ID of the user whose orders are to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful; false otherwise.
     */
    boolean exportAllOrdersByUserToCSV(int userId, String filePath);

    /**
     * Export selected orders to a CSV file.
     *
     * @param orderIds List of order IDs to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful; false otherwise.
     */
    boolean exportSelectedOrdersToCSV(java.util.List<Integer> orderIds, String filePath);

    /**
     * Export a single order to a CSV file by its ID.
     *
     * @param orderId  The ID of the order to be exported.
     * @param filePath The file path where the CSV will be saved.
     * @return true if the export was successful; false otherwise.
     */
    boolean exportOrderByIdToCSV(int orderId, String filePath);
}
