package com.thereadingroom.model.dao.cart;

import com.thereadingroom.model.dao.BaseDAO;
import com.thereadingroom.model.dao.database.Database;
import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for managing cart-related operations.
 * Handles operations like creating a cart, adding/removing items, checking stock, etc.
 */
public class CartDAO extends BaseDAO implements ICartDAO {

    /**
     * Retrieves the active cart for a user, or creates a new one if no active cart exists.
     *
     * @param userId the user ID.
     * @return the cart ID, or creates a new cart if one doesn't exist.
     */
    @Override
    public int getOrCreateCart(int userId) {
        int cartId = getActiveCart(userId);
        if (cartId == -1) {
            cartId = createNewCart(userId);
        }
        return cartId;
    }

    /**
     * Fetches the active cart for a user.
     *
     * @param userId the user ID.
     * @return the cart ID if found, or -1 if no active cart is found.
     */
    private int getActiveCart(int userId) {
        String sql = "SELECT cart_id FROM cart WHERE user_id = ? AND status = 'active'";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cart_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active cart: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Creates a new active cart for the given user.
     *
     * @param userId the user ID.
     * @return the newly created cart ID.
     */
    private int createNewCart(int userId) {
        String sql = "INSERT INTO cart (user_id, status) VALUES (?, 'active')";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // Return the newly generated cart ID
            }
        } catch (SQLException e) {
            System.out.println("Error creating new cart: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves all items in a cart by cart ID.
     *
     * @param cartId the cart ID.
     * @return a list of CartItem objects representing the items in the cart.
     */
    @Override
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT ci.cart_item_id, b.id AS book_id, b.title, b.author, ci.quantity, b.price " +
                "FROM cart_items ci " +
                "JOIN books b ON ci.book_id = b.id " +
                "WHERE ci.cart_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem(
                        rs.getInt("cart_item_id"),
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                );
                cartItems.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching cart items: " + e.getMessage());
        }
        return cartItems;
    }

    /**
     * Removes a specific book from the cart.
     *
     * @param cartId the cart ID.
     * @param bookId the book ID to be removed.
     */
    @Override
    public void removeBookFromCart(int cartId, int bookId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND book_id = ?";
        executeUpdate(sql, cartId, bookId);  // Execute the update to remove the book from the cart
    }

    /**
     * Updates the quantity of a specific book in the cart.
     *
     * @param cartId   the cart ID.
     * @param bookId   the book ID to update.
     * @param quantity the new quantity to set for the book.
     */
    @Override
    public void updateBookQuantity(int cartId, int bookId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND book_id = ?";
        executeUpdate(sql, quantity, cartId, bookId);  // Update the book quantity in the cart
    }

    /**
     * Adds or updates a book in the cart. If the book already exists, the quantity is updated.
     *
     * @param cartId   the cart ID.
     * @param bookId   the book ID.
     * @param quantity the quantity to add or update.
     */
    @Override
    public void addOrUpdateBookInCart(int cartId, int bookId, int quantity) {
        String sql = "INSERT OR REPLACE INTO cart_items (cart_id, book_id, quantity) VALUES (?, ?, ?)";
        executeUpdate(sql, cartId, bookId, quantity);  // Add or update the book in the cart
    }

    /**
     * Removes a list of books from the cart.
     *
     * @param cartId       the cart ID.
     * @param booksToRemove the list of books to be removed from the cart.
     */
    public void removeBooksFromCart(int cartId, List<Book> booksToRemove) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND book_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Book book : booksToRemove) {
                pstmt.setInt(1, cartId);
                pstmt.setInt(2, book.getBookId());
                pstmt.addBatch();  // Add each removal operation to the batch
            }
            pstmt.executeBatch();  // Execute the batch removal
        } catch (SQLException e) {
            System.out.println("Error removing books from cart: " + e.getMessage());
        }
    }
}
