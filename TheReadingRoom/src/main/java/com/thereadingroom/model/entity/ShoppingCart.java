package com.thereadingroom.model.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a shopping cart for a user, storing books and their respective quantities.
 * Provides methods to add, update, remove books, calculate total price, and manage the cart.
 */
public class ShoppingCart {
    private final Map<Book, Integer> cart;  // A map of books and their corresponding quantities
    private final int userId;  // The ID of the user associated with this cart
    private final int cartId;  // The ID of the cart associated with this user

    /**
     * Constructor for creating a ShoppingCart for a specific user.
     *
     * @param userId The user ID associated with the cart.
     */
    public ShoppingCart(int userId, int cartId) {
        this.userId = userId;
        this.cartId = cartId;
        this.cart = new HashMap<>();
    }

    // Getter for cartId
    public int getCartId() {
        return cartId;
    }

    /**
     * Adds a book to the cart with a specified quantity. If the book already exists,
     * the quantity is increased by the provided value.
     *
     * @param book     The book to add to the cart.
     * @param quantity The quantity to add.
     * @throws IllegalArgumentException if the book is null or quantity is non-positive.
     */
    public void addBook(Book book, int quantity) {
        if (book == null || quantity <= 0) {
            throw new IllegalArgumentException("Book cannot be null and quantity must be positive.");
        }
        cart.merge(book, quantity, Integer::sum);  // Merges the new quantity with existing quantity (if any)
    }

    /**
     * Removes a book from the cart.
     *
     * @param book The book to remove.
     * @throws IllegalArgumentException if the book is null.
     */
    public void removeBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        cart.remove(book);
    }

    /**
     * Updates the quantity of a book in the cart. If the quantity is zero or negative,
     * the book is removed from the cart.
     *
     * @param book     The book whose quantity to update.
     * @param quantity The new quantity.
     * @throws IllegalArgumentException if the book is null.
     */
    public void updateBookQuantity(Book book, int quantity) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (quantity <= 0) {
            cart.remove(book);  // Remove the book if the quantity is zero or less
        } else {
            cart.put(book, quantity);  // Update the quantity of the book
        }
    }

    /**
     * Retrieves all books and their quantities in the cart.
     *
     * @return An unmodifiable map of books and their quantities.
     */
    public Map<Book, Integer> getBooks() {
        return Collections.unmodifiableMap(cart);  // Return an unmodifiable view of the cart
    }

    /**
     * Checks if the cart is empty.
     *
     * @return True if the cart is empty, false otherwise.
     */
    public boolean isEmpty() {
        return cart.isEmpty();
    }

    /**
     * Clears all items from the cart.
     */
    public void clearCart() {
        cart.clear();
    }

    /**
     * Retrieves the user ID associated with this cart.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Removes a list of books from the cart.
     *
     * @param booksToRemove The list of books to remove.
     */
    public void removeBooks(List<Book> booksToRemove) {
        booksToRemove.forEach(cart::remove);  // Method reference instead of lambda
    }
}
