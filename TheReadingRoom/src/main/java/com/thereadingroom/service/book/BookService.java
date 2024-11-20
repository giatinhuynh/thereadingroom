package com.thereadingroom.service.book;

import com.thereadingroom.model.dao.book.BookDAO;
import com.thereadingroom.model.entity.Book;

import java.util.List;

/**
 * BookService is responsible for managing book-related operations.
 * It serves as an intermediary between controllers and data access objects (DAOs).
 * Implements the Singleton pattern to ensure a single instance of the service throughout the application.
 */
public class BookService implements IBookService {

    // Singleton instance of BookService
    private static BookService instance;
    private final BookDAO bookDAO;  // DAO for accessing book-related data

    /**
     * Private constructor for Singleton pattern.
     * Initializes the BookDAO to handle database operations for books.
     */
    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Singleton method to get the single instance of BookService.
     *
     * @return The singleton instance of BookService.
     */
    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }

    /**
     * Retrieve all books in the bookstore.
     *
     * @return List of all available books.
     */
    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    /**
     * Retrieve the top 5 best-selling books based on the number of copies sold.
     *
     * @return List of top 5 books sorted by sales.
     */
    @Override
    public List<Book> getTop5Books() {
        return bookDAO.getTop5Books();
    }

    /**
     * Search for books by a keyword in their title.
     *
     * @param keyword The keyword to search for in the book titles.
     * @return List of books that match the search keyword.
     */
    @Override
    public List<Book> searchBooksByTitle(String keyword) {
        return bookDAO.searchBooksByTitle(keyword);
    }

    /**
     * Update the physical stock of a specific book by its ID.
     *
     * @param bookId   The ID of the book to update.
     * @param newStock The new stock quantity.
     * @return True if the update was successful, false otherwise.
     */
    @Override
    public boolean updatePhysicalCopies(int bookId, int newStock) {
        return bookDAO.updatePhysicalCopies(bookId, newStock);
    }

    /**
     * Find a book by its unique ID.
     *
     * @param bookId The ID of the book to find.
     * @return The Book object if found, or null if not found.
     */
    @Override
    public Book findBookById(int bookId) {
        return bookDAO.findBookById(bookId);
    }

    /**
     * Delete a book by its unique ID.
     *
     * @param bookId The ID of the book to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    @Override
    public boolean deleteBookById(int bookId) {
        return bookDAO.deleteBookById(bookId);
    }

    /**
     * Update the details of an existing book.
     *
     * @param book The Book object containing updated details.
     * @return True if the update was successful, false otherwise.
     */
    @Override
    public boolean updateBook(Book book) {
        return bookDAO.updateBook(book);
    }

    /**
     * Add a new book to the bookstore.
     *
     * @param book The Book object to add.
     * @return True if the addition was successful, false otherwise.
     */
    @Override
    public boolean addBook(Book book) {
        return bookDAO.addBook(book);
    }
}
