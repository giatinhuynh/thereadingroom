package com.thereadingroom.service.inventory;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;

import java.util.List;
import java.util.Map;

/**
 * IInventoryService defines the operations related to managing book inventory,
 * including reserving books, reverting reservations, checking stock availability,
 * and finalizing stock adjustments after purchases.
 */
public interface IInventoryService {

    /**
     * Reserve books from the inventory based on the provided map of books and their quantities.
     * This method temporarily decreases the available stock to hold the items for a user
     * during the checkout process, preventing other users from purchasing the same items.
     *
     * @param books Map containing Book objects and their corresponding quantities to reserve.
     * @return true if all books have sufficient stock for reservation, false otherwise.
     *         Returns false if any book has insufficient stock, preventing the reservation.
     */
    boolean reserveBooks(Map<Book, Integer> books);

    /**
     * Revert the stock reservation for the provided books.
     * This is used when a user cancels their checkout or when payment fails,
     * returning the previously reserved stock back to inventory.
     *
     * @param books Map containing Book objects and the quantities that should be returned to stock.
     */
    void revertReservations(Map<Book, Integer> books);

    /**
     * Finalize stock adjustments for the purchased books.
     * This method is called after a successful purchase to permanently reduce the stock
     * for the sold books and to update the number of sold copies.
     *
     * @param books Map of Book objects and their corresponding quantities sold.
     */
    void finalizeStockAdjustments(Map<Book, Integer> books);

    /**
     * Checks if sufficient stock is available for the requested quantity of a book.
     * This method is useful for validating if an item can be reserved or purchased.
     *
     * @param book     The Book object for which to check stock availability.
     * @param quantity The quantity to check against the available stock.
     * @return true if the available stock is greater than or equal to the requested quantity, false otherwise.
     */
    boolean isStockAvailable(Book book, int quantity);
}
