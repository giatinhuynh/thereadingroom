package com.thereadingroom.service.user;

import com.thereadingroom.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    /**
     * Validate user login credentials.
     *
     * @param username The username provided for login.
     * @param password The password provided for login.
     * @return true if the credentials are valid; false otherwise.
     */
    boolean validateUserLogin(String username, String password);

    /**
     * Check if the user is an admin.
     *
     * @param username The username of the user to check.
     * @return true if the user is an admin; false otherwise.
     */
    boolean isAdminUser(String username);

    /**
     * Retrieve a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found; empty otherwise.
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Update the profile of a user based on their username.
     *
     * @param username  The username of the user to update.
     * @param firstName The new first name.
     * @param lastName  The new last name.
     * @param password  The new password.
     * @param isAdmin   Whether the user is an admin.
     * @return true if the update was successful; false otherwise.
     */
    boolean updateUserProfile(String username, String firstName, String lastName, String password, boolean isAdmin);

    /**
     * Register a new user.
     *
     * @param username  The new user's username.
     * @param firstName The new user's first name.
     * @param lastName  The new user's last name.
     * @param password  The new user's password.
     * @param isAdmin   Whether the new user is an admin.
     * @return true if the registration was successful; false otherwise.
     */
    boolean registerUser(String username, String firstName, String lastName, String password, boolean isAdmin);

    /**
     * Get the user ID based on the username.
     *
     * @param username The username to search for.
     * @return The user ID associated with the username.
     */
    int getUserIdByUsername(String username);

    /**
     * Retrieve all users in the system.
     *
     * @return A list of all users.
     */
    List<User> getAllUsers();

    /**
     * Delete a user based on their user ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the deletion was successful; false otherwise.
     */
    boolean deleteUser(int userId);

    /**
     * Update the profile of a user based on their user ID.
     *
     * @param userId    The ID of the user to update.
     * @param username  The new username.
     * @param firstName The new first name.
     * @param lastName  The new last name.
     * @param password  The new password.
     * @param isAdmin   Whether the user is an admin.
     * @return true if the update was successful; false otherwise.
     */
    boolean updateUserProfilebyID(int userId, String username, String firstName, String lastName, String password, boolean isAdmin);

    public Optional<User> getUserById(int userId);
}
