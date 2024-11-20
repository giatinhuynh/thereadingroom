package com.thereadingroom.service.inventory;

import com.thereadingroom.model.dao.book.IBookDAO;
import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartTableItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing book inventory operations,
 * including reserving stock, reverting reservations, checking stock availability,
 * and finalizing stock adjustments after successful purchases.
 */
@Service
public class InventoryService implements IInventoryService {

    private final IBookDAO bookDAO;

    /**
     * Constructor for InventoryService, injecting the necessary DAO dependencies.
     *
     * @param bookDAO Data access object for interacting with the book data.
     */
    public InventoryService(IBookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Calculates the total price of selected items in the cart.
     *
     * @param selectedItems List of CartTableItem objects representing the items in the cart.
     * @return The total price for all selected items.
     */
    public double calculateTotalPrice(List<CartTableItem> selectedItems) {
        return selectedItems.stream()
                .mapToDouble(CartTableItem::getTotalAmount)
                .sum();
    }

    /**
     * Validates stock availability for all selected items in the cart.
     * Ensures that there is enough stock for each item to proceed with checkout.
     *
     * @param selectedItems List of CartTableItem objects representing the items in the cart.
     * @return true if all items have sufficient stock, false otherwise.
     */
    public boolean validateStockAvailability(List<CartTableItem> selectedItems) {
        for (CartTableItem item : selectedItems) {
            if (!isStockAvailable(item.getBook(), item.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reserves stock for all selected items during checkout.
     * This method reduces the available stock temporarily, holding it for the user.
     *
     * @param selectedItems List of CartTableItem objects representing the items in the cart.
     * @return true if all items could be reserved, false otherwise.
     */
    public boolean reserveStockForCheckout(List<CartTableItem> selectedItems) {
        Map<Book, Integer> booksToReserve = new HashMap<>();
        for (CartTableItem item : selectedItems) {
            booksToReserve.put(item.getBook(), item.getQuantity());
        }
        return reserveBooks(booksToReserve);
    }

    /**
     * Reverts the reserved stock when the checkout or payment process is canceled.
     * This method restores the previously reserved stock to its original state.
     *
     * @param selectedItems List of CartTableItem objects representing the items in the cart.
     */
    public void revertReservedStock(List<CartTableItem> selectedItems) {
        Map<Book, Integer> booksToRevert = new HashMap<>();
        for (CartTableItem item : selectedItems) {
            booksToRevert.put(item.getBook(), item.getQuantity());
        }
        revertReservations(booksToRevert);
    }

    /**
     * Finalizes stock adjustments after a successful payment.
     * This method reduces the physical stock of each item permanently
     * and updates the number of sold copies.
     *
     * @param selectedItems List of CartTableItem objects representing the items in the cart.
     */
    public void finalizeStockAfterPayment(List<CartTableItem> selectedItems) {
        Map<Book, Integer> booksToFinalize = new HashMap<>();
        for (CartTableItem item : selectedItems) {
            booksToFinalize.put(item.getBook(), item.getQuantity());
        }

        for (Map.Entry<Book, Integer> entry : booksToFinalize.entrySet()) {
            Book book = entry.getKey();
            int quantitySold = entry.getValue();

            // Log the finalization process
            System.out.println("Finalizing stock for book: " + book.getTitle());
            int currentPhysicalStock = bookDAO.getAvailableCopies(book);
            System.out.println("Current physical stock: " + currentPhysicalStock + ", Quantity sold: " + quantitySold);

            try {
                // Reduce physical stock and update sold copies in the database
                bookDAO.updateSoldCopiesAfterPayment(book.getBookId(), quantitySold);

                int updatedPhysicalStock = bookDAO.getAvailableCopies(book);
                System.out.println("Updated physical stock: " + updatedPhysicalStock);
            } catch (Exception e) {
                System.err.println("Failed to finalize stock for book: " + book.getTitle() + " due to: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if the requested quantity of a book is available in stock.
     *
     * @param book     The book object to check stock for.
     * @param quantity The quantity of the book requested.
     * @return true if the stock is sufficient, false otherwise.
     */
    public boolean isStockAvailable(Book book, int quantity) {
        int availableCopies = bookDAO.getAvailableCopies(book);
        return availableCopies >= quantity;
    }

    /**
     * Reserves stock for a set of books by reducing their physical copies.
     *
     * @param books Map of Book objects and their corresponding quantities to reserve.
     * @return true if all books could be reserved, false otherwise.
     */
    public boolean reserveBooks(Map<Book, Integer> books) {
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            int availableCopies = bookDAO.getAvailableCopies(entry.getKey());
            if (availableCopies < entry.getValue()) {
                return false;
            }
            // Reserve stock by reducing physical copies
            bookDAO.reducePhysicalCopies(entry.getKey().getBookId(), entry.getValue());
        }
        return true;
    }

    /**
     * Reverts the reserved stock for a set of books, restoring the physical copies.
     *
     * @param books Map of Book objects and their corresponding quantities to restore.
     */
    public void revertReservations(Map<Book, Integer> books) {
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            Book book = entry.getKey();
            int quantityToRevert = entry.getValue();
            int currentStock = bookDAO.getAvailableCopies(book);

            // Log the reversion process
            System.out.println("Reverting stock for book: " + book.getTitle());
            System.out.println("Current stock: " + currentStock + ", Quantity to revert: " + quantityToRevert);

            try {
                bookDAO.restorePhysicalCopies(book.getBookId(), quantityToRevert);
                int newStock = bookDAO.getAvailableCopies(book);
                System.out.println("Stock after reversion: " + newStock);
            } catch (Exception e) {
                System.err.println("Failed to revert stock for book: " + book.getTitle() + " due to: " + e.getMessage());
            }
        }
    }

    /**
     * Finalizes stock adjustments after a successful payment by updating the physical stock and sold copies.
     *
     * @param books Map of Book objects and their corresponding quantities sold.
     */
    public void finalizeStockAdjustments(Map<Book, Integer> books) {
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            Book book = entry.getKey();
            int quantitySold = entry.getValue();

            // Log the finalization process
            System.out.println("Finalizing stock for book: " + book.getTitle());
            int currentPhysicalStock = bookDAO.getAvailableCopies(book);
            System.out.println("Current physical stock: " + currentPhysicalStock + ", Quantity sold: " + quantitySold);

            try {
                // Reduce physical stock and update sold copies in the database
                bookDAO.reducePhysicalCopies(book.getBookId(), quantitySold);
                bookDAO.updateSoldCopiesAfterPayment(book.getBookId(), quantitySold);

                int updatedPhysicalStock = bookDAO.getAvailableCopies(book);
                System.out.println("Updated physical stock: " + updatedPhysicalStock);
            } catch (Exception e) {
                System.err.println("Failed to finalize stock for book: " + book.getTitle() + " due to: " + e.getMessage());
            }
        }
    }
}
