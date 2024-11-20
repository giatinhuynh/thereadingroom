package com.thereadingroom.model.dao.book;

import com.thereadingroom.model.dao.BaseDAO;
import com.thereadingroom.model.dao.database.Database;
import com.thereadingroom.model.entity.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Book entity.
 * Provides methods for interacting with the books table in the database.
 */
public class BookDAO extends BaseDAO implements IBookDAO {

    /**
     * Retrieve all books from the database.
     *
     * @return a list of all books.
     */
    @Override
    public List<Book> getAllBooks() {
        String query = "SELECT id, title, author, physical_copies, price, sold_copies FROM books";
        return executeBookListQuery(query);
    }

    /**
     * Retrieve the top 5 best-selling books based on the number of sold copies.
     *
     * @return a list of the top 5 books sorted by sold copies.
     */
    @Override
    public List<Book> getTop5Books() {
        String query = "SELECT id, title, author, physical_copies, price, sold_copies FROM books ORDER BY sold_copies DESC LIMIT 5";
        return executeBookListQuery(query);
    }

    /**
     * Retrieve the available stock (physical copies) for a specific book.
     *
     * @param book the book entity to check.
     * @return the number of physical copies available.
     */
    @Override
    public int getAvailableCopies(Book book) {
        return getStockForBook(book.getBookId());
    }

    /**
     * Update the physical stock of a book in the database.
     *
     * @param bookId   the ID of the book to update.
     * @param newStock the new stock value.
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updatePhysicalCopies(int bookId, int newStock) {
        String query = "UPDATE books SET physical_copies = ? WHERE id = ?";
        return executeUpdate(query, newStock, bookId);
    }

    /**
     * Reduce the stock of a book by a given quantity.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies to reduce.
     * @return true if the operation was successful, false otherwise.
     */
    @Override
    public boolean reducePhysicalCopies(int bookId, int quantity) {
        return updateBookField(bookId, -quantity, "physical_copies");
    }

    /**
     * Restore the stock of a book by a given quantity (e.g., for reverting reservations).
     *
     * @param bookId   the ID of the book.
     * @param quantity the quantity to restore.
     * @return true if the operation was successful, false otherwise.
     */
    @Override
    public boolean restorePhysicalCopies(int bookId, int quantity) {
        return updateBookField(bookId, quantity, "physical_copies");
    }

    /**
     * Update the sold copies for a specific book after a payment.
     *
     * @param bookId   the ID of the book.
     * @param quantity the number of copies sold.
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updateSoldCopiesAfterPayment(int bookId, int quantity) {
        return updateBookField(bookId, quantity, "sold_copies");
    }

    /**
     * Generic method to update a specific field of a book.
     *
     * @param bookId    the ID of the book to update.
     * @param quantity  the amount to update by.
     * @param fieldName the field to update (e.g., "physical_copies", "sold_copies").
     * @return true if the update was successful, false otherwise.
     */
    private boolean updateBookField(int bookId, int quantity, String fieldName) {
        String query = "UPDATE books SET " + fieldName + " = " + fieldName + " + ? WHERE id = ?";
        return executeUpdate(query, quantity, bookId);
    }

    /**
     * Update the sold copies field of a specific book.
     *
     * @param bookId   the ID of the book.
     * @param quantity the quantity to update.
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updateSoldCopies(int bookId, int quantity) {
        String query = "UPDATE books SET sold_copies = ? WHERE id = ?";
        return executeUpdate(query, quantity, bookId);
    }

    /**
     * Search for books by title (case-insensitive).
     *
     * @param keyword the keyword to search for in book titles.
     * @return a list of books that match the search keyword.
     */
    @Override
    public List<Book> searchBooksByTitle(String keyword) {
        String query = "SELECT * FROM books WHERE LOWER(title) LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(constructBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error searching books by title: " + e.getMessage());
        }
        return books;
    }

    /**
     * Find a book by its ID.
     *
     * @param bookId the ID of the book to retrieve.
     * @return the corresponding Book entity, or null if not found.
     */
    @Override
    public Book findBookById(int bookId) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return constructBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Add a new book to the database.
     *
     * @param book the book entity to add.
     * @return true if the operation was successful, false otherwise.
     */
    @Override
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, physical_copies, price, sold_copies) VALUES (?, ?, ?, ?, 0)";
        return executeUpdate(query, book.getTitle(), book.getAuthor(), book.getPhysicalCopies(), book.getPrice());
    }

    /**
     * Update the details of an existing book.
     *
     * @param book the book entity to update.
     * @return true if the operation was successful, false otherwise.
     */
    @Override
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, physical_copies = ?, price = ?, sold_copies = ? WHERE id = ?";
        return executeUpdate(query, book.getTitle(), book.getAuthor(), book.getPhysicalCopies(), book.getPrice(), book.getSoldCopies(), book.getBookId());
    }

    /**
     * Delete a book by its ID.
     *
     * @param bookId the ID of the book to delete.
     * @return true if the operation was successful, false otherwise.
     */
    @Override
    public boolean deleteBookById(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";
        return executeUpdate(query, bookId);
    }

    /**
     * Helper method to execute a query and return a list of books.
     *
     * @param query the SQL query to execute.
     * @return a list of books that match the query.
     */
    private List<Book> executeBookListQuery(String query) {
        List<Book> books = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(constructBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Helper method to construct a Book entity from a ResultSet.
     *
     * @param rs the ResultSet containing book data.
     * @return the constructed Book entity.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Book constructBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("physical_copies"),
                rs.getDouble("price"),
                rs.getInt("sold_copies")
        );
    }

    /**
     * Helper method to retrieve the stock of a book by its ID.
     *
     * @param bookId the ID of the book.
     * @return the number of physical copies available.
     */
    public int getStockForBook(int bookId) {
        String query = "SELECT physical_copies FROM books WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("physical_copies");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving stock for book ID: " + bookId + " - " + e.getMessage());
        }
        return 0;
    }
}
