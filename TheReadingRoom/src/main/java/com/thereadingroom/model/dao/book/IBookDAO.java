package com.thereadingroom.model.dao.book;

import com.thereadingroom.model.entity.Book;

import java.util.List;

/**
 * Interface for Book Data Access Object (DAO).
 * This interface defines the operations for interacting with the books table in the database.
 */
public interface IBookDAO {

    /**
     * Retrieve all books from the database.
     *
     * @return a list of all books.
     */
    List<Book> getAllBooks();

    /**
     * Retrieve the top 5 best-selling books from the database.
     * Books are ranked by the number of sold copies.
     *
     * @return a list of the top 5 books based on the number of sold copies.
     */
    List<Book> getTop5Books();

    /**
     * Get the number of available physical copies for a given book.
     *
     * @param book the book entity to check stock for.
     * @return the number of available physical copies.
     */
    int getAvailableCopies(Book book);

    /**
     * Update the physical stock (number of copies) of a given book.
     *
     * @param bookId   the ID of the book to update.
     * @param newStock the new stock value.
     * @return true if the update was successful, false otherwise.
     */
    boolean updatePhysicalCopies(int bookId, int newStock);

    /**
     * Reduce the physical stock (number of copies) of a book by a given quantity.
     * This is typically done when a book is sold.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies to reduce.
     * @return true if the operation was successful, false otherwise.
     */
    boolean reducePhysicalCopies(int bookId, int quantity);

    /**
     * Update the number of sold copies for a given book.
     * This is typically done after a successful sale or order completion.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies sold to add to the current sold count.
     * @return true if the update was successful, false otherwise.
     */
    boolean updateSoldCopies(int bookId, int quantity);

    /**
     * Search for books by their title using a case-insensitive search.
     *
     * @param keyword the search keyword to match against book titles.
     * @return a list of books that match the search keyword.
     */
    List<Book> searchBooksByTitle(String keyword);

    /**
     * Retrieve a book from the database by its ID.
     *
     * @param bookId the ID of the book to retrieve.
     * @return the Book entity, or null if the book is not found.
     */
    Book findBookById(int bookId);

    /**
     * Add a new book to the database.
     *
     * @param book the book entity to add.
     * @return true if the book was added successfully, false otherwise.
     */
    boolean addBook(Book book);

    /**
     * Update the details of an existing book in the database.
     *
     * @param book the book entity with updated details.
     * @return true if the update was successful, false otherwise.
     */
    boolean updateBook(Book book);

    /**
     * Delete a book from the database by its ID.
     *
     * @param bookId the ID of the book to delete.
     * @return true if the book was deleted successfully, false otherwise.
     */
    boolean deleteBookById(int bookId);

    /**
     * Update the number of sold copies for a book after a payment has been processed.
     * This method ensures that the stock is adjusted after a transaction.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies sold.
     * @return true if the update was successful, false otherwise.
     */
    boolean updateSoldCopiesAfterPayment(int bookId, int quantity);

    /**
     * Restore the physical stock of a book by a given quantity.
     * This is typically used to undo a stock reduction in cases like order cancellation.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies to restore.
     * @return true if the operation was successful, false otherwise.
     */
    boolean restorePhysicalCopies(int bookId, int quantity);
}
