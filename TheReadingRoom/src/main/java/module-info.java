/**
 * Module definition for the 'The Reading Room' application.
 * This module integrates JavaFX for the UI and Spring for dependency injection, along with other dependencies.
 */
module com.thereadingroom {

    // JavaFX modules required for the application's UI components
    requires javafx.controls;         // JavaFX controls (e.g., buttons, text fields)
    requires javafx.fxml;             // FXML for building JavaFX UI declaratively
    requires javafx.web;              // JavaFX WebView for embedding web content

    // External libraries used by the application
    requires org.controlsfx.controls; // Additional JavaFX controls from ControlsFX
    requires com.dlsc.formsfx;        // JavaFX form elements from FormsFX
    requires net.synedra.validatorfx; // ValidatorFX for form validation
    requires org.kordamp.ikonli.javafx; // Ikonli for icon support in JavaFX
    requires org.kordamp.bootstrapfx.core; // BootstrapFX for JavaFX styling
    requires eu.hansolo.tilesfx;      // TilesFX for creating custom JavaFX tiles
    requires com.almasb.fxgl.all;     // FXGL for game-related features (optional)

    // Java Database Connectivity (JDBC) and connection pooling
    requires java.sql;                // JDBC API for database interaction
    requires com.zaxxer.hikari;       // HikariCP for connection pooling

    // Spring Framework modules for dependency injection and context management
    requires spring.context;          // ApplicationContext from Spring
    requires spring.beans;            // Spring beans and dependency injection
    requires spring.core;             // Core Spring functionality (dependency injection, AOP, etc.)
    requires spring.aop;              // Spring AOP (Aspect-Oriented Programming) module

    // Open specific packages to JavaFX and Spring for reflection (controllers and utility classes)
    opens com.thereadingroom.controller.admin to javafx.fxml, spring.beans, spring.core;  // Admin controllers
    opens com.thereadingroom.controller.user to javafx.fxml, spring.beans, spring.core;   // User controllers
    opens com.thereadingroom.controller.common to javafx.fxml, spring.beans, spring.core; // Common controllers
    opens com.thereadingroom.utils.ui to javafx.fxml, spring.beans, spring.core;          // UI utilities
    opens com.thereadingroom.view to javafx.fxml;                                         // Main application views
    opens com.thereadingroom.model.dao to javafx.fxml;                                    // Data Access Objects (DAO)
    opens com.thereadingroom.utils.auth to spring.core, spring.beans, javafx.fxml;        // Authentication utilities

    // Open the configuration package to Spring for reflection (e.g., AppConfig)
    opens com.thereadingroom.config to spring.core, spring.beans, spring.context;         // Spring configuration

    // Export services, utilities, and controllers to make them accessible to other modules
    exports com.thereadingroom.service;                  // General service layer
    exports com.thereadingroom.utils.auth to spring.core, spring.beans, spring.context, javafx.fxml;  // Authentication utilities
    exports com.thereadingroom.utils.ui;                 // UI utilities (SpringFXMLLoader, etc.)
    exports com.thereadingroom.controller.admin to javafx.fxml;  // Admin controllers
    exports com.thereadingroom.controller.user to javafx.fxml;   // User controllers
    exports com.thereadingroom.controller.common to javafx.fxml; // Common controllers
    exports com.thereadingroom.view;                     // Main application views
    exports com.thereadingroom.model.entity;             // Entity models (Book, User, etc.)
    exports com.thereadingroom.model.dao;                // Data Access Objects (DAO)

    // Export individual services for modular access
    exports com.thereadingroom.service.book;             // Book-related services
    exports com.thereadingroom.service.order;            // Order-related services
    exports com.thereadingroom.service.payment;          // Payment-related services
    exports com.thereadingroom.service.user;             // User-related services
    exports com.thereadingroom.service.inventory;        // Inventory-related services
    exports com.thereadingroom.service.CSVExport;        // CSV export services
    exports com.thereadingroom.service.cart;             // Shopping cart services

    // Open DAO packages to JavaFX for reflection (if needed for UI interaction)
    opens com.thereadingroom.model.dao.book to javafx.fxml;  // Book DAO
    opens com.thereadingroom.model.dao.cart to javafx.fxml;  // Cart DAO
    opens com.thereadingroom.model.dao.user to javafx.fxml;  // User DAO
    opens com.thereadingroom.model.dao.order to javafx.fxml; // Order DAO
    opens com.thereadingroom.model.dao.database to javafx.fxml;  // Database initializer

    // Open entity models for reflection (e.g., for use in JavaFX bindings)
    opens com.thereadingroom.model.entity to javafx.base, javafx.fxml; // Entity models (Book, Order, etc.)
}
