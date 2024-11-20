package com.thereadingroom.model.entity;

/**
 * Represents an item in the shopping cart, including the book details, quantity, and price.
 * Each CartItem corresponds to a specific book in the cart.
 */
public class CartItem {
    private final int cartItemId;  // Unique identifier for the cart item, does not change once set
    private final int bookId;      // Unique identifier for the associated book, does not change
    private String title;          // The title of the book in the cart
    private String author;         // The author of the book in the cart
    private int quantity;          // Quantity of the book in the cart
    private double price;          // Price of the book in the cart

    /**
     * Constructor to create a CartItem with the necessary details.
     *
     * @param cartItemId Unique identifier for the cart item
     * @param bookId     Unique identifier for the associated book
     * @param title      The title of the book
     * @param author     The author of the book
     * @param quantity   The quantity of the book in the cart
     * @param price      The price of the book
     */
    public CartItem(int cartItemId, int bookId, String title, String author, int quantity, double price) {
        this.cartItemId = cartItemId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.price = price;
    }

    // Getter for the title of the book in the cart
    public String getTitle() {
        return title;
    }

    // Setter for the title of the book
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for the quantity of the book in the cart
    public int getQuantity() {
        return quantity;
    }

    // Setter for the quantity of the book in the cart
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter for the price of the book in the cart
    public double getPrice() {
        return price;
    }

    // Setter for the price of the book
    public void setPrice(double price) {
        this.price = price;
    }

    // Getter for the unique identifier of the associated book
    public int getBookId() {
        return bookId;
    }
}
