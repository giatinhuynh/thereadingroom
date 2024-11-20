package com.thereadingroom.service.cart;

import com.thereadingroom.model.dao.cart.CartDAO;
import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;

import java.util.List;

/**
 * CartService manages operations related to the shopping cart, including adding, updating, and removing books,
 * as well as managing stock reservations and finalizing purchases.
 */
public class CartService implements ICartService {

    // Singleton instance
    private static CartService instance;
    private final CartDAO cartDAO;

    // Private constructor for Singleton
    public CartService() {
        this.cartDAO = new CartDAO();
    }

    // Get the singleton instance of CartService
    public static synchronized CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    /**
     * Get or create a cart for the user. If the user already has an active cart, return its ID.
     * Otherwise, create a new cart.
     *
     * @param userId The ID of the user.
     * @return The ID of the user's active cart.
     */
    public int getOrCreateCart(int userId) {
        return cartDAO.getOrCreateCart(userId);  // Use the correct method from CartDAO
    }

    /**
     * Update the quantity of a book in the cart.
     *
     * @param cartId  The ID of the cart.
     * @param bookId  The ID of the book to be updated.
     * @param quantity The new quantity of the book.
     */
    public void updateBookQuantity(int cartId, int bookId, int quantity) {
        cartDAO.updateBookQuantity(cartId, bookId, quantity);
    }

    /**
     * Get all the items currently in the user's cart.
     *
     * @param cartId The ID of the user's cart.
     * @return A list of CartTableItem objects representing the items in the cart.
     */
    public List<CartItem> getCartItems(int cartId) {
        System.out.println("Fetching cart items for cart ID: " + cartId);
        List<CartItem> cartItems = cartDAO.getCartItems(cartId);
        System.out.println("Cart items retrieved: " + cartItems.size() + " items.");
        for (CartItem item : cartItems) {
            System.out.println("Item: Book ID = " + item.getBookId() + ", Quantity = " + item.getQuantity());
        }
        return cartItems;
    }

    /**
     * Remove a specific book from the user's cart.
     *
     * @param cartId The ID of the cart.
     * @param bookId The ID of the book to be removed.
     */
    public void removeBookFromCart(int cartId, int bookId) {
        cartDAO.removeBookFromCart(cartId, bookId);
    }

    public void removeBooksFromCart(int cartId, List<Book> booksToRemove) {
        // Delegate to DAO to handle actual removal of books from the cart in the database
        cartDAO.removeBooksFromCart(cartId, booksToRemove);
    }

    /**
     * Add or update a book in the cart. This method decides whether to add a new book to the cart
     * or update the quantity.
     *
     * @param cartId  The ID of the cart.
     * @param bookId  The ID of the book.
     * @param quantity The quantity to add/update.
     */
    public void addOrUpdateBookInCart(int cartId, int bookId, int quantity) {
        cartDAO.addOrUpdateBookInCart(cartId, bookId, quantity);
    }
}
