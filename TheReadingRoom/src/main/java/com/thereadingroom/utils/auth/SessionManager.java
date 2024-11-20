package com.thereadingroom.utils.auth;

import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * SessionManager is responsible for managing the user session, which includes
 * storing user details, handling shopping cart data, and clearing session when logging out.
 * It implements the ISessionManager interface to ensure consistent session management practices.
 */
@Component
public class SessionManager implements ISessionManager {

    private int userId;             // Stores the unique ID of the logged-in user
    private String username;        // Stores the username of the logged-in user
    private String firstName;       // Stores the first name of the logged-in user
    private String lastName;        // Stores the last name of the logged-in user
    private boolean isAdmin;        // Indicates if the user has admin privileges
    private ShoppingCart shoppingCart;  // Stores the user's shopping cart, lazily initialized after login

    /**
     * Constructor initializes session variables to default values.
     * Session is empty by default until a user logs in.
     */
    public SessionManager() {
        this.userId = 0;                 // Default to no user ID
        this.username = null;            // No username set initially
        this.firstName = null;           // No first name set initially
        this.lastName = null;            // No last name set initially
        this.isAdmin = false;            // Default to non-admin user
        this.shoppingCart = null;        // Shopping cart is null until user logs in
    }

    /**
     * Sets the user details into the session after successful login.
     * Also initializes the shopping cart for the user.
     *
     * @param userId    The user's unique ID
     * @param username  The username of the logged-in user
     * @param firstName The first name of the logged-in user
     * @param lastName  The last name of the logged-in user
     * @param isAdmin   Boolean indicating if the user has admin privileges
     */
    public void setUserDetails(int userId, String username, String firstName, String lastName, boolean isAdmin) {
        if (userId > 0) {
            this.userId = userId;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.isAdmin = isAdmin;

            // Initialize shopping cart if not already created
            if (this.shoppingCart == null) {
                this.shoppingCart = new ShoppingCart(userId, 0);  // Initialize with user ID and default cartId = 0
            }
        } else {
            System.out.println("Invalid User ID: " + userId);  // Log invalid user ID scenario
        }
    }

    /**
     * Clears the current session when the user logs out, resetting all session data.
     * This includes user details and the associated shopping cart.
     */
    public void clearSession() {
        this.userId = 0;                 // Reset user ID
        this.username = null;            // Clear username
        this.firstName = null;           // Clear first name
        this.lastName = null;            // Clear last name
        this.shoppingCart = null;        // Clear shopping cart
        this.isAdmin = false;            // Reset admin status
    }

    /**
     * Returns the current user's session data as a User object.
     * Useful for accessing user information in a unified way.
     *
     * @return A User object containing session data (without password).
     */
    public User getCurrentUser() {
        return new User(this.userId, this.username, this.firstName, this.lastName, null, this.isAdmin);
    }

    // Getters for user session data

    /**
     * Gets the user ID stored in the session.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the username stored in the session.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the first name stored in the session.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name stored in the session.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Checks if the user has admin privileges based on session data.
     *
     * @return true if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    // Shopping cart management

    /**
     * Gets the user's shopping cart stored in the session.
     *
     * @return The user's shopping cart.
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Sets the user's shopping cart into the session.
     *
     * @param shoppingCart The shopping cart to associate with the user session.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
