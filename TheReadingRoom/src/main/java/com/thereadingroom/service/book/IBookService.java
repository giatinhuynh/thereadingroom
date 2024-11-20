package com.thereadingroom.service.book;

import com.thereadingroom.model.entity.Book;
import java.util.List;

/**
 * IBookService defines the contract for book-related operations.
 * It is implemented by the BookService class to manage books in the system.
 * This interface ensures a clear separation of concerns between business logic and data access layers.
 */
public interface IBookService {

    /**
     * Retrieve all books in the bookstore.
     *
     * @return A list of all books available.
     */
    List<Book> getAllBooks();

    /**
     * Retrieve the top 5 best-selling books based on the number of copies sold.
     *
     * @return A list of the top 5 best-selling books.
     */
    List<Book> getTop5Books();

    /**
     * Search for books by a keyword in their title.
     *
     * @param keyword The keyword to search for in the titles of books.
     * @return A list of books that match the search keyword.
     */
    List<Book> searchBooksByTitle(String keyword);

    /**
     * Update the physical stock of a book.
     *
     * @param bookId   The ID of the book to update.
     * @param newStock The new stock quantity for the book.
     * @return True if the stock was updated successfully, false otherwise.
     */
    boolean updatePhysicalCopies(int bookId, int newStock);

    /**
     * Find a book by its unique ID.
     *
     * @param bookId The ID of the book to find.
     * @return The Book object if found, or null if not found.
     */
    Book findBookById(int bookId);

    /**
     * Delete a book from the bookstore by its ID.
     *
     * @param bookId The ID of the book to delete.
     * @return True if the book was deleted successfully, false otherwise.
     */
    boolean deleteBookById(int bookId);

    /**
     * Update the details of an existing book.
     *
     * @param book The Book object containing updated information.
     * @return True if the book was updated successfully, false otherwise.
     */
    boolean updateBook(Book book);

    /**
     * Add a new book to the bookstore.
     *
     * @param book The Book object to add to the bookstore.
     * @return True if the book was added successfully, false otherwise.
     */
    boolean addBook(Book book);
}
