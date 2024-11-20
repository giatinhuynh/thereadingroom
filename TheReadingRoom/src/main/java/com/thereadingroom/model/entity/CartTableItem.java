package com.thereadingroom.model.entity;

import javafx.beans.property.*;

/**
 * Represents an item in the shopping cart as displayed in a table.
 * Stores information such as the book details, quantity, total amount, and selection status.
 */
public class CartTableItem {

    private final Book book;  // Reference to the book this cart item represents
    private final SimpleStringProperty itemName;  // Name of the book to be displayed in the UI
    private final SimpleIntegerProperty quantity;  // Quantity of the book selected by the user
    private final SimpleDoubleProperty totalAmount;  // Total cost of the book (price * quantity)
    private final SimpleBooleanProperty selected;  // Whether this item is selected for checkout

    /**
     * Constructor to initialize a CartTableItem with the given book and quantity.
     * It automatically calculates the total amount based on the book's price and quantity.
     *
     * @param book     The book being added to the cart.
     * @param quantity The quantity of the book.
     */
    public CartTableItem(Book book, int quantity) {
        this.book = book;
        this.itemName = new SimpleStringProperty(book.getTitle());  // Initialize the item's name from the book title
        this.quantity = new SimpleIntegerProperty(quantity);  // Set the initial quantity
        this.totalAmount = new SimpleDoubleProperty(book.getPrice() * quantity);  // Calculate the total amount
        this.selected = new SimpleBooleanProperty(false);  // Initially, the item is not selected for checkout
    }

    /**
     * Returns the book associated with this cart item.
     *
     * @return The book object.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Property method for the item name, used for binding in UI components.
     *
     * @return A SimpleStringProperty representing the item's name.
     */
    public SimpleStringProperty itemNameProperty() {
        return itemName;
    }

    /**
     * Gets the current quantity of the book.
     *
     * @return The quantity of the book in the cart.
     */
    public int getQuantity() {
        return quantity.get();
    }

    /**
     * Updates the quantity of the book in the cart and recalculates the total amount.
     *
     * @param quantity The new quantity of the book.
     */
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);  // Update the quantity
        this.totalAmount.set(this.book.getPrice() * quantity);  // Recalculate the total amount
    }

    /**
     * Gets the total amount for this cart item (price * quantity).
     *
     * @return The total cost of the item.
     */
    public double getTotalAmount() {
        return totalAmount.get();
    }

    /**
     * Property method for total amount, used for binding in UI components.
     *
     * @return A SimpleDoubleProperty representing the total amount.
     */
    public SimpleDoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    /**
     * Gets whether the item is selected for checkout.
     *
     * @return true if the item is selected, false otherwise.
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Sets whether the item is selected for checkout.
     *
     * @param selected The new selected state (true if selected, false if not).
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);  // Update the selection state
    }
}
