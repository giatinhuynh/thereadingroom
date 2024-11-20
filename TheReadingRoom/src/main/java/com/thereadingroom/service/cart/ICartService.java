package com.thereadingroom.service.cart;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;

import java.util.List;

/**
 * ICartService defines the contract for cart-related operations.
 * This service handles the addition, removal, and updating of books in the cart,
 * stock adjustments, and managing user checkout reservations.
 */
public interface ICartService {

    /**
     * Retrieves the active cart for a user, or creates a new one if no active cart exists.
     *
     * @param userId The ID of the user.
     * @return The ID of the user's active cart.
     */
    int getOrCreateCart(int userId);

    /**
     * Updates the quantity of a specific book in the user's cart.
     * If the book's quantity is reduced to zero, it can be removed from the cart.
     *
     * @param cartId   The ID of the cart.
     * @param bookId   The ID of the book to update.
     * @param quantity The new quantity to be set for the book.
     */
    void updateBookQuantity(int cartId, int bookId, int quantity);

    /**
     * Retrieves all the items currently in the cart for the given cart ID.
     *
     * @param cartId The ID of the cart.
     * @return A list of CartItem objects representing the books and their quantities in the cart.
     */
    List<CartItem> getCartItems(int cartId);

    /**
     * Removes a specific book from the cart based on the cart ID and book ID.
     *
     * @param cartId The ID of the cart.
     * @param bookId The ID of the book to be removed.
     */
    void removeBookFromCart(int cartId, int bookId);

    /**
     * Adds a new book to the cart or updates the quantity if the book already exists.
     * If the book is already present in the cart, its quantity is updated to the new value.
     *
     * @param cartId   The ID of the cart.
     * @param bookId   The ID of the book.
     * @param quantity The quantity to be added or updated.
     */
    void addOrUpdateBookInCart(int cartId, int bookId, int quantity);

    /**
     * Removes multiple books from the cart based on a list of books.
     *
     * @param cartId        The ID of the cart.
     * @param booksToRemove A list of Book objects to be removed from the cart.
     */
    void removeBooksFromCart(int cartId, List<Book> booksToRemove);
}
