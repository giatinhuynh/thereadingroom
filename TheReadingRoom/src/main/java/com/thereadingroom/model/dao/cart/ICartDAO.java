package com.thereadingroom.model.dao.cart;

import com.thereadingroom.model.entity.CartItem;

import java.util.List;

/**
 * Interface for managing cart-related data access operations.
 * Defines the methods for interacting with the cart in the database, including adding/removing books,
 * updating quantities, managing stock reservations, and clearing carts.
 */
public interface ICartDAO {

    /**
     * Retrieves or creates a cart for the specified user.
     * If the user has no active cart, a new one will be created.
     *
     * @param userId the user ID
     * @return the cart ID (existing or newly created)
     */
    int getOrCreateCart(int userId);

    /**
     * Updates the quantity of a specific book in the cart.
     * If the quantity is zero or less, the item may be removed from the cart depending on implementation.
     *
     * @param cartId the cart ID
     * @param bookId the book ID
     * @param quantity the new quantity to set
     */
    void updateBookQuantity(int cartId, int bookId, int quantity);

    /**
     * Adds or updates a book in the cart. If the book already exists in the cart,
     * the quantity will be updated. If the book does not exist, it will be added.
     *
     * @param cartId the cart ID
     * @param bookId the book ID
     * @param quantity the quantity to add or update
     */
    void addOrUpdateBookInCart(int cartId, int bookId, int quantity);

    /**
     * Retrieves all items from the specified cart. Each item in the cart is returned as a
     * CartItem object which includes the book's details and its quantity.
     *
     * @param cartId the cart ID
     * @return a list of CartItem objects representing the items in the cart
     */
    List<CartItem> getCartItems(int cartId);

    /**
     * Removes a specific book from the cart by its ID.
     * This operation deletes the item from the cart completely.
     *
     * @param cartId the cart ID
     * @param bookId the book ID to remove
     */
    void removeBookFromCart(int cartId, int bookId);
}
