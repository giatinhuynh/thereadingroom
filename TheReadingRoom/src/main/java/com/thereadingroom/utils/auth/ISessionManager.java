package com.thereadingroom.utils.auth;

import com.thereadingroom.model.entity.ShoppingCart;

/**
 * ISessionManager defines the contract for managing user sessions within the application.
 * This includes handling user details, shopping cart state, and session clearing.
 */
public interface ISessionManager {

    /**
     * Stores the user's details in the session.
     *
     * @param userId    The user's unique ID.
     * @param username  The username of the user.
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param isAdmin   Boolean indicating whether the user has admin privileges.
     */
    void setUserDetails(int userId, String username, String firstName, String lastName, boolean isAdmin);

    /**
     * Retrieves the user ID stored in the session.
     *
     * @return The user ID associated with the current session.
     */
    int getUserId();

    /**
     * Retrieves the username stored in the session.
     *
     * @return The username associated with the current session.
     */
    String getUsername();

    /**
     * Retrieves the first name stored in the session.
     *
     * @return The first name of the user associated with the current session.
     */
    String getFirstName();

    /**
     * Retrieves the last name stored in the session.
     *
     * @return The last name of the user associated with the current session.
     */
    String getLastName();

    /**
     * Sets the shopping cart associated with the user session.
     *
     * @param shoppingCart The user's shopping cart.
     */
    void setShoppingCart(ShoppingCart shoppingCart);

    /**
     * Retrieves the shopping cart associated with the user session.
     *
     * @return The user's shopping cart.
     */
    ShoppingCart getShoppingCart();

    /**
     * Clears all session data, including user details and shopping cart, effectively logging the user out.
     */
    void clearSession();
}
