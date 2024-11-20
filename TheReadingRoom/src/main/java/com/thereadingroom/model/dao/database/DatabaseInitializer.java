package com.thereadingroom.model.dao.database;

import com.thereadingroom.model.dao.BaseDAO;

import java.util.Arrays;

/**
 * DatabaseInitializer is responsible for initializing the database, including
 * creating tables, adding the admin user, and populating initial data such as books.
 */
public class DatabaseInitializer extends BaseDAO {

    /**
     * Initializes the database by creating tables, adding the admin user, and populating books.
     * This method should be called once when the application starts to ensure the database is set up.
     */
    public static void initializeDatabase() {
        createTables();           // Create necessary tables for users, books, orders, etc.
        initializeAdminUser();     // Create the admin user if it doesn't exist
        populateBooks();           // Populate books table with initial data if empty
    }

    /**
     * Creates all the necessary tables (users, books, orders, and cart) for the application.
     */
    private static void createTables() {
        createUsersTable();        // Create the users table
        createBooksTable();        // Create the books table
        createOrderTables();       // Create tables related to orders and order items
        createCartTables();        // Create tables for cart and cart items
    }

    /**
     * Creates the users table if it doesn't already exist.
     * The table stores user details such as username, password, and admin status.
     */
    private static void createUsersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                username TEXT NOT NULL UNIQUE, 
                password TEXT NOT NULL, 
                first_name TEXT, 
                last_name TEXT, 
                is_admin BOOLEAN DEFAULT 0
            );
        """;
        new DatabaseInitializer().executeUpdate(sql);
        System.out.println("Users table created or already exists.");
    }

    /**
     * Creates the books table if it doesn't already exist.
     * The table stores details about books such as title, author, stock, price, and sold copies.
     */
    private static void createBooksTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                physical_copies INTEGER NOT NULL,
                price REAL NOT NULL,
                sold_copies INTEGER NOT NULL
            );
        """;
        new DatabaseInitializer().executeUpdate(sql);
        System.out.println("Books table created or already exists.");
    }

    /**
     * Creates the orders and order_items tables to store order and order item details.
     */
    private static void createOrderTables() {
        String createOrdersTableSQL = """
            CREATE TABLE IF NOT EXISTS orders (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                order_number TEXT NOT NULL UNIQUE, 
                user_id INTEGER NOT NULL, 
                total_price REAL NOT NULL, 
                order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        String createOrderItemsTableSQL = """
            CREATE TABLE IF NOT EXISTS order_items (
                order_item_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                order_id INTEGER NOT NULL, 
                book_id INTEGER NOT NULL, 
                title TEXT NOT NULL, 
                quantity INTEGER NOT NULL, 
                price REAL NOT NULL, 
                FOREIGN KEY (order_id) REFERENCES orders(order_id), 
                FOREIGN KEY (book_id) REFERENCES books(id)
            );
        """;

        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.executeUpdate(createOrdersTableSQL);
        System.out.println("Orders table created.");
        initializer.executeUpdate(createOrderItemsTableSQL);
        System.out.println("Order items table created.");
    }

    /**
     * Creates the cart and cart_items tables to store shopping cart details.
     */
    private static void createCartTables() {
        String createCartTableSQL = """
            CREATE TABLE IF NOT EXISTS cart (
                cart_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                user_id INTEGER NOT NULL, 
                status TEXT NOT NULL DEFAULT 'active', 
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        String createCartItemsTableSQL = """
            CREATE TABLE IF NOT EXISTS cart_items (
                cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                cart_id INTEGER NOT NULL, 
                book_id INTEGER NOT NULL, 
                quantity INTEGER NOT NULL, 
                FOREIGN KEY (cart_id) REFERENCES cart(cart_id), 
                FOREIGN KEY (book_id) REFERENCES books(id), 
                UNIQUE(cart_id, book_id) ON CONFLICT REPLACE
            );
        """;

        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.executeUpdate(createCartTableSQL);
        System.out.println("Cart table created.");
        initializer.executeUpdate(createCartItemsTableSQL);
        System.out.println("Cart items table created.");
    }

    /**
     * Initializes the admin user by checking if an admin user already exists.
     * If not, it creates an admin user with default credentials.
     */
    private static void initializeAdminUser() {
        String checkAdminSQL = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertAdminSQL = """
            INSERT INTO users (username, password, first_name, last_name, is_admin)
            VALUES ('admin', 'reading_admin', 'Admin', 'Admin', 1)
        """;

        DatabaseInitializer initializer = new DatabaseInitializer();
        Boolean adminExists = initializer.executeQuery(checkAdminSQL, rs -> {
            try {
                return rs.next() && rs.getInt(1) > 0;
            } catch (Exception e) {
                return false;
            }
        });

        if (!adminExists) {
            initializer.executeUpdate(insertAdminSQL);
            System.out.println("Admin user created successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

    /**
     * Populates the books table with initial data if it is empty.
     * The initial data includes a set of predefined books with their stock and price.
     */
    private static void populateBooks() {
        String checkBooksSQL = "SELECT COUNT(*) FROM books";
        String insertBooksSQL = """
            INSERT INTO books (title, author, physical_copies, price, sold_copies)
            VALUES (?, ?, ?, ?, ?)
        """;

        String[][] books = {
                {"Absolute Java", "Savitch", "10", "50", "142"},
                {"JAVA: How to Program", "Deitel and Deitel", "100", "70", "475"},
                {"Computing Concepts with JAVA 8 Essentials", "Horstman", "500", "89", "60"},
                {"Java Software Solutions", "Lewis and Loftus", "500", "99", "12"},
                {"Java Program Design", "Cohoon and Davidson", "2", "29", "86"},
                {"Clean Code", "Robert Martin", "100", "45", "300"},
                {"Gray Hat C#", "Brandon Perry", "300", "68", "178"},
                {"Python Basics", "David Amos", "1000", "49", "79"},
                {"Bayesian Statistics The Fun Way", "Will Kurt", "600", "42", "155"}
        };

        DatabaseInitializer initializer = new DatabaseInitializer();
        Boolean booksExist = initializer.executeQuery(checkBooksSQL, rs -> {
            try {
                return rs.next() && rs.getInt(1) > 0;
            } catch (Exception e) {
                return false;
            }
        });

        if (!booksExist) {
            initializer.executeBatchUpdate(insertBooksSQL, Arrays.asList(books));
            System.out.println("Books data populated successfully.");
        } else {
            System.out.println("Books table already populated, skipping.");
        }
    }

    /**
     * Resets the stock of all books to their initial values.
     * This is typically used during testing or when resetting the database.
     */
    public static void resetBooksStock() {
        String resetBooksSQL = """
        UPDATE books SET 
            physical_copies = CASE 
                WHEN title = 'Absolute Java' THEN 10 
                WHEN title = 'JAVA: How to Program' THEN 100 
                WHEN title = 'Computing Concepts with JAVA 8 Essentials' THEN 500 
                WHEN title = 'Java Software Solutions' THEN 500 
                WHEN title = 'Java Program Design' THEN 2 
                WHEN title = 'Clean Code' THEN 100 
                WHEN title = 'Gray Hat C#' THEN 300 
                WHEN title = 'Python Basics' THEN 1000 
                WHEN title = 'Bayesian Statistics The Fun Way' THEN 600 
                ELSE physical_copies 
            END,
            sold_copies = CASE 
                WHEN title = 'Absolute Java' THEN 142
                WHEN title = 'JAVA: How to Program' THEN 475 
                WHEN title = 'Computing Concepts with JAVA 8 Essentials' THEN 60 
                WHEN title = 'Java Software Solutions' THEN 12 
                WHEN title = 'Java Program Design' THEN 86 
                WHEN title = 'Clean Code' THEN 300 
                WHEN title = 'Gray Hat C#' THEN 178 
                WHEN title = 'Python Basics' THEN 79 
                WHEN title = 'Bayesian Statistics The Fun Way' THEN 155 
                ELSE sold_copies
            END;
    """;

        new DatabaseInitializer().executeUpdate(resetBooksSQL);
        System.out.println("Books stock and sold copies reset to initial values.");
    }

}
