package com.thereadingroom.model.entity;

/**
 * Represents a user entity in the application, storing user details such as
 * username, first name, last name, password, and admin status.
 */
public class User {

    private int id;  // Unique identifier for the user
    private String username;  // Username of the user
    private String firstName;  // User's first name
    private String lastName;  // User's last name
    private String password;  // Password of the user
    private boolean isAdmin;  // Whether the user is an admin

    /**
     * Constructor to initialize the User object with all attributes.
     *
     * @param id        Unique ID for the user.
     * @param username  Username of the user.
     * @param firstName First name of the user.
     * @param lastName  Last name of the user.
     * @param password  User's password.
     * @param isAdmin   Boolean indicating if the user has admin privileges.
     */
    public User(int id, String username, String firstName, String lastName, String password, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters for accessing user details

    /**
     * Gets the unique ID of the user.
     *
     * @return User ID.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the first name of the user.
     *
     * @return First name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return Last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the password of the user.
     *
     * @return Password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Checks if the user has admin privileges.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    // Setters for modifying user details

    /**
     * Sets the first name of the user.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the admin status of the user.
     *
     * @param admin True to make the user an admin, false otherwise.
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
