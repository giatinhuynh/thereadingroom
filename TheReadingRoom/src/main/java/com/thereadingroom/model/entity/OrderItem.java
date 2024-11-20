package com.thereadingroom.model.entity;

/**
 * Represents an item in an order, including the book's ID, title,
 * the quantity purchased, and the price per unit.
 */
public class OrderItem {

    private int bookId;        // The ID of the book in the order
    private String title;      // The title of the book
    private int quantity;      // The quantity of the book ordered
    private double price;      // The price per unit of the book

    /**
     * Constructor to create an OrderItem instance.
     *
     * @param bookId   The unique ID of the book
     * @param title    The title of the book
     * @param quantity The quantity of the book being ordered
     * @param price    The price per unit of the book
     */
    public OrderItem(int bookId, String title, int quantity, double price) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters for OrderItem fields

    // Returns the unique ID of the book in this order item
    public int getBookId() {
        return bookId;
    }

    // Returns the title of the book in this order item
    public String getTitle() {
        return title;
    }

    // Sets the title of the book in this order item
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the quantity of the book ordered
    public int getQuantity() {
        return quantity;
    }

    // Sets the quantity of the book ordered
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Returns the price per unit of the book
    public double getPrice() {
        return price;
    }

    // Sets the price per unit of the book
    public void setPrice(double price) {
        this.price = price;
    }
}
