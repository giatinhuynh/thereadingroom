package com.thereadingroom.service;

import com.thereadingroom.model.dao.book.BookDAO;
import com.thereadingroom.model.dao.book.IBookDAO;
import com.thereadingroom.service.CSVExport.CSVExportService;
import com.thereadingroom.service.CSVExport.ICSVExportService;
import com.thereadingroom.service.book.BookService;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.cart.ICartService;
import com.thereadingroom.service.inventory.IInventoryService;
import com.thereadingroom.service.inventory.InventoryService;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.order.OrderService;
import com.thereadingroom.service.payment.IPaymentService;
import com.thereadingroom.service.payment.PaymentService;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.service.user.UserService;
import com.thereadingroom.utils.auth.SessionManager;

/**
 * ServiceManager is a singleton class responsible for managing and providing access
 * to various services used in the application. It serves as a central point for
 * creating and retrieving service instances, ensuring that only one instance of each
 * service is created and used throughout the application.
 */
public class ServiceManager {

    // Singleton instance of ServiceManager
    private static ServiceManager instance;

    // Services managed by ServiceManager
    private final IUserService userService;               // Service for user-related operations
    private final IOrderService orderService;             // Service for order-related operations
    private final IInventoryService inventoryService;     // Service for inventory management
    private final IPaymentService paymentService;         // Service for payment handling
    private final ICartService cartService;               // Service for cart management
    private final IBookService bookService;               // Service for book-related operations
    private final ICSVExportService csvExportService;     // Service for exporting data to CSV
    private final SessionManager sessionManager;          // Manages user sessions across the app

    /**
     * Private constructor to initialize all the services.
     * This constructor sets up all dependencies needed for the services.
     */
    public ServiceManager() {
        // Data Access Object (DAO) for books
        IBookDAO bookDAO = new BookDAO();

        // Initialize services
        this.userService = new UserService();
        this.orderService = OrderService.getInstance();
        this.inventoryService = new InventoryService(bookDAO);  // Injecting bookDAO into inventory service
        this.paymentService = PaymentService.getInstance();
        this.cartService = CartService.getInstance();
        this.bookService = BookService.getInstance();
        this.csvExportService = CSVExportService.getInstance();
        this.sessionManager = new SessionManager();  // Create an instance of SessionManager for handling sessions
    }

    /**
     * Retrieves the singleton instance of the ServiceManager.
     * Ensures that only one instance of ServiceManager exists throughout the application.
     *
     * @return The singleton instance of ServiceManager.
     */
    public static synchronized ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    // Getter methods to access individual services

    /**
     * Retrieves the UserService instance.
     *
     * @return An instance of IUserService for managing users.
     */
    public IUserService getUserService() {
        return userService;
    }

    /**
     * Retrieves the OrderService instance.
     *
     * @return An instance of IOrderService for managing orders.
     */
    public IOrderService getOrderService() {
        return orderService;
    }

    /**
     * Retrieves the InventoryService instance.
     *
     * @return An instance of IInventoryService for managing book inventory.
     */
    public IInventoryService getInventoryService() {
        return inventoryService;
    }

    /**
     * Retrieves the PaymentService instance.
     *
     * @return An instance of IPaymentService for managing payments.
     */
    public IPaymentService getPaymentService() {
        return paymentService;
    }

    /**
     * Retrieves the CartService instance.
     *
     * @return An instance of ICartService for managing shopping carts.
     */
    public ICartService getCartService() {
        return cartService;
    }

    /**
     * Retrieves the BookService instance.
     *
     * @return An instance of IBookService for managing books.
     */
    public IBookService getBookService() {
        return bookService;
    }

    /**
     * Retrieves the CSVExportService instance.
     *
     * @return An instance of ICSVExportService for exporting data to CSV format.
     */
    public ICSVExportService getCSVExportService() {
        return csvExportService;
    }

    /**
     * Retrieves the SessionManager instance.
     *
     * @return The SessionManager instance for managing user sessions.
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
