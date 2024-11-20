# The Reading Room - Assignment 2 (COSC2391/Further Programming)

---

## Student Details
- **Student Name:** Gia Tin Huynh Duc
- **Student ID:** s3962053
- **Course:** COSC2391 - Further Programming
- **Institution:** RMIT University

---

## Project Overview
Welcome to *The Reading Room*, a bookstore management system developed as part of Assignment 2 for the COSC2391 Further Programming course. This project leverages JavaFX for the user interface and the Spring Framework for backend service management. It enables both administrators and regular users to manage books and orders seamlessly, with additional features for order export, user management, and reporting. The project demonstrates solid software engineering practices, adhering to principles of Object-Oriented Programming (OOP) and design patterns.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Technology Stack](#technology-stack)
4. [System Requirements](#system-requirements)
5. [Installation Guide](#installation-guide)
6. [Usage](#usage)
7. [Project Structure](#project-structure)
8. [OOP Principles & Design Patterns](#oop-principles--design-patterns)
9. [Contributions](#contributions)
10. [Contact Information](#contact-information)

---

## Key Features

### User Functionality
- **Browse Books:** Users can browse through the available books, view detailed information, and add them to their shopping cart.
- **Shopping Cart:** Users can adjust quantities, view total costs, and proceed to checkout.
- **Checkout Process:** Users validate stock availability, reserve books, and make secure payments.
- **View and Export Orders:** Users can view their past orders and export them for record-keeping.
- **Profile Management:** Users can update personal information in their profile dashboard.

### Admin Functionality
- **Book Management:** Administrators can add new books, edit existing books, and reset both stock and sold copies.
- **User Management:** Administrators can manage user profiles, including editing and deleting accounts.
- **Order Management:** Administrators can manage and export user orders.
- **Report Generation:** Export CSV reports for sales and user activity.

### Backend Features
- **Inventory Management:** Adjusts book stock and sold copies based on user purchases.
- **Payment Processing:** Secure payment validation with real-time stock adjustment.
- **Database Initialization:** Auto-initializes the database with required tables and seed data (books, users, etc.).

---

## Technology Stack
### Frontend
- **JavaFX:** Provides a modern, responsive user interface.
- **FXML:** Used for defining the UI structure, separating logic from design.

### Backend
- **Spring Framework:** Handles dependency injection and backend service management.
- **HikariCP:** Manages database connection pooling.
- **SQLite:** Lightweight relational database for data storage.

### Testing
- **JUnit 5:** Unit testing framework for testing services and controllers.
- **Mockito:** Used for mocking dependencies in unit tests.

---

## System Requirements
- **Java Development Kit (JDK):** Version 21 or higher.
- **Maven:** For dependency management and building the project.
- **SQLite:** No manual setup required, the database initializes automatically on application startup.

---

## Installation Guide

1. **Clone the Repository:**
   ```
   git clone https://github.com/FurtherJavaProgramming/assignment2-giatinhuynh.git
   cd TheReadingRoom
   ```
2. **Build the Project:** Use Maven to resolve dependencies and compile the project.
   ```
   mvn clean install
   ```
3. **Run the Application:** Start the application directly using Maven.
4. **Database Setup:** The database automatically initializes with pre-seeded data, including admin users and sample books.

---

## Usage

### Admin Users
- **Login:** Use admin credentials to access the system.
- **Manage Books:** Add, edit, or reset book stock and sold copies.
- **Manage Users:** Update user profiles and delete accounts.
- **Order Management:** View and export reports for book orders and sales.

### Regular Users
- **Register or Login:** Create an account or log in with an existing account.
- **Browse and Shop:** Add books to your cart and proceed to checkout.
- **View Orders:** View and export your past orders.
- **Profile Management:** Update personal details via the user dashboard.

## Project Structure
```
src/
│
├── main/
│   ├── java/com/thereadingroom/
│   │   ├── config/               # Spring configuration files for dependency injection
│   │   │   └── AppConfig.java     # Main Spring configuration class
│   │   ├── controller/           # Controllers for handling UI logic for Admin and User
│   │   │   ├── admin/            # Controllers specific to Admin operations
│   │   │   │   ├── AdminAddBookController.java        # Controller for adding new books
│   │   │   │   ├── AdminEditBookController.java       # Controller for editing existing books
│   │   │   │   ├── AdminEditUserController.java       # Controller for editing user profiles
│   │   │   │   ├── AdminManageUsersController.java    # Controller for managing users
│   │   │   │   ├── AdminOrderController.java          # Controller for managing and viewing orders
│   │   │   │   ├── AdminSidebarController.java        # Sidebar navigation controller for admin
│   │   │   │   └── AdminStockController.java          # Controller for managing book stock
│   │   │   ├── common/           # Controllers for common functionality shared across users and admins
│   │   │   │   ├── EditProfileController.java         # Controller for editing user profiles
│   │   │   │   ├── LoginController.java               # Controller for handling user login
│   │   │   │   └── RegisterController.java            # Controller for user registration
│   │   │   ├── user/             # Controllers specific to User operations
│   │   │   │   ├── BookTableController.java           # Controller for viewing books in a table
│   │   │   │   ├── CheckoutConfirmationController.java # Controller for confirming checkout details
│   │   │   │   ├── OrderController.java               # Controller for viewing user orders
│   │   │   │   ├── PaymentController.java             # Controller for processing user payments
│   │   │   │   ├── ShoppingCartController.java        # Controller for managing shopping cart items
│   │   │   │   ├── SidebarController.java             # Sidebar navigation controller for user
│   │   │   │   ├── UserDashboardController.java       # Controller for user dashboard
│   │   │   │   └── ViewAllBooksController.java        # Controller for viewing all books available
│   │   ├── model/                # Data Access Objects (DAOs) and Entity classes representing the database tables
│   │   │   ├── dao/              # DAO classes for interacting with the database
│   │   │   │   ├── book/         # DAO classes for book-related operations
│   │   │   │   │   ├── BookDAO.java                   # DAO for handling book operations
│   │   │   │   │   └── IBookDAO.java                  # Interface for BookDAO
│   │   │   │   ├── cart/         # DAO classes for shopping cart-related operations
│   │   │   │   │   ├── CartDAO.java                   # DAO for handling cart operations
│   │   │   │   │   └── ICartDAO.java                  # Interface for CartDAO
│   │   │   │   ├── database/     # Database-related classes
│   │   │   │   │   ├── Database.java                  # Singleton class for managing database connections
│   │   │   │   │   └── DatabaseInitializer.java       # Class for initializing the database schema
│   │   │   │   ├── order/        # DAO classes for order-related operations
│   │   │   │   │   ├── IOrderDAO.java                 # Interface for OrderDAO
│   │   │   │   │   └── OrderDAO.java                  # DAO for handling order operations
│   │   │   │   ├── user/         # DAO classes for user-related operations
│   │   │   │   │   ├── IUserDAO.java                  # Interface for UserDAO
│   │   │   │   │   └── UserDAO.java                   # DAO for handling user operations
│   │   │   │   └── BaseDAO.java                       # Base class for common DAO operations
│   │   │   ├── entity/           # Entity classes representing database tables (Book, Cart, Order, etc.)
│   │   │   │   ├── Book.java                        # Entity class for book objects
│   │   │   │   ├── CartItem.java                    # Entity class for individual cart items
│   │   │   │   ├── CartTableItem.java               # Entity class for items displayed in the shopping cart table
│   │   │   │   ├── Order.java                       # Entity class for orders
│   │   │   │   ├── OrderItem.java                   # Entity class for individual order items
│   │   │   │   ├── Payment.java                     # Entity class for payment details
│   │   │   │   ├── ShoppingCart.java                # Entity class representing the user's shopping cart
│   │   │   │   └── User.java                        # Entity class for users
│   │   ├── service/              # Service classes handling business logic
│   │   │   ├── book/             # Service classes for book-related operations
│   │   │   │   ├── BookService.java                 # Service for book-related business logic
│   │   │   │   └── IBookService.java                # Interface for BookService
│   │   │   ├── cart/             # Service classes for cart-related operations
│   │   │   │   ├── CartService.java                 # Service for shopping cart-related business logic
│   │   │   │   └── ICartService.java                # Interface for CartService
│   │   │   ├── CSVExport/        # Service classes for CSV exporting functionality
│   │   │   │   ├── CSVExportService.java            # Service for exporting data to CSV
│   │   │   │   └── ICSVExportService.java           # Interface for CSVExportService
│   │   │   ├── inventory/        # Service classes for inventory management
│   │   │   │   ├── IInventoryService.java           # Interface for InventoryService
│   │   │   │   └── InventoryService.java            # Service for managing book inventory
│   │   │   ├── order/            # Service classes for order-related operations
│   │   │   │   ├── IOrderService.java               # Interface for OrderService
│   │   │   │   └── OrderService.java                # Service for managing orders
│   │   │   ├── payment/          # Service classes for payment processing
│   │   │   │   ├── IPaymentService.java             # Interface for PaymentService
│   │   │   │   └── PaymentService.java              # Service for processing payments
│   │   │   ├── user/             # Service classes for user-related operations
│   │   │   │   ├── IUserService.java                # Interface for UserService
│   │   │   │   └── UserService.java                 # Service for managing users
│   │   │   └── ServiceManager.java                  # Singleton managing access to all services
│   │   ├── utils/                # Utility classes for various helper functionalities
│   │   │   ├── auth/             # Utility classes for authentication
│   │   │   │   ├── ISessionManager.java             # Interface for Session management
│   │   │   │   ├── PaymentValidator.java            # Class for validating payment details
│   │   │   │   └── SessionManager.java              # Class for managing user sessions
│   │   │   ├── export/           # Utility classes for exporting functionalities
│   │   │   │   └── CSVExportUtility.java            # Utility for handling CSV export operations
│   │   │   └── ui/               # Utility classes for user interface handling
│   │   │       ├── SpringFXMLLoader.java            # Custom loader for Spring-integrated FXML views
│   │   │       └── UIUtils.java                     # Utility class for common UI operations
│   │   └── view/                 # JavaFX main application
│   │       └── Main.java                             # Main entry point for the JavaFX application
│   └── resources/
│       ├── com/thereadingroom/                      # Resource folder for images, CSS, and FXML files
│       │   ├── assets/images/                       # Assets such as images used in the application
│       │   │   └── Library.jpg                      # Sample image for the UI
│       │   ├── css/                                 # CSS stylesheets for the UI
│       │   │   └── table-style.css                  # Stylesheet for customizing table views
│       │   └── fxml/                                # FXML files for defining UI layouts
│       │       ├── admin/                           # FXML views for Admin-specific screens
│       │       │   ├── admin_add_book.fxml          # View for adding new books
│       │       │   ├── admin_edit_book.fxml         # View for editing book details
│       │       │   ├── admin_manage_orders.fxml     # View for managing orders
│       │       │   ├── admin_sidebar.fxml           # Sidebar navigation for admins
│       │       └── user/                            # FXML views for User-specific screens
│       │           ├── checkout_confirmation.fxml   # View for checkout confirmation
│       │           ├── payment.fxml                 # View for payment processing
│       │           ├── shopping_cart.fxml           # View for managing shopping cart items
│       │           ├── sidebar.fxml                 # Sidebar navigation for users
│       │           └── user_dashboard.fxml          # View for the user dashboard
│
├── test/
│   └── java/com/thereadingroom/                     # Unit tests for controllers, services, and utilities
│       ├── controller/user/                         # Unit tests for user controllers
│       │   ├── CheckoutConfirmationControllerTest.java  # Unit tests for checkout confirmation controller
│       │   ├── PaymentControllerTest.java               # Unit tests for payment controller
│       │   └── ShoppingCartControllerTest.java         # Unit tests for shopping cart controller
│       └── utils/                                    # Unit tests for utilities
│           └── JavaFXInitializer.java                # Tests for JavaFX initializer
├── pom.xml                                           # Maven configuration file
```

---

## OOP Principles & Design Patterns

This project, **The Reading Room**, adheres to core Object-Oriented Programming (OOP) principles and incorporates several well-known design patterns. Below is an overview of these principles and patterns applied throughout the codebase, based on a review of the provided files.

### OOP Principles:

1. **Encapsulation:**
    - **Definition:** Encapsulation restricts direct access to some of an object's components, which is essential for protecting the integrity of the object. This principle ensures that the internal representation of objects is hidden and only exposed through a controlled interface.
    - **Applied In:**
      - **`Book`**, **`User`**, **`Order`**, and **`ShoppingCart`** classes encapsulate their properties (like `title`, `price`, `totalPrice`) and provide getter and setter methods for controlled access.
      - **`SessionManager`** encapsulates the details of the current user session (such as `username`, `userId`, `shoppingCart`) and provides methods like `setUserDetails()`, `getUserId()`, etc., to access and modify session-related data safely.

2. **Abstraction:**
    - **Definition:** Abstraction involves hiding the complex implementation details and showing only the essential features of the object.
    - **Applied In:**
      - Interfaces like **`IOrderService`**, **`ICartService`**, **`IInventoryService`**, and **`IUserDAO`** provide a contract for the services and DAO layers. Implementations (e.g., `OrderService`, `CartService`) define the actual behavior, but the higher-level components interact only with the interfaces, abstracting the underlying implementation.
      - **`SpringFXMLLoader`** abstracts the logic for loading FXML files and injecting dependencies through the Spring Framework, simplifying UI management.

3. **Inheritance:**
    - **Definition:** Inheritance allows one class to acquire the properties and methods of another class, promoting code reuse.
    - **Applied In:**
      - **`BaseDAO`** serves as a common parent class for all DAO implementations (e.g., `UserDAO`, `BookDAO`). It provides generic database operations (`executeUpdate()`, `executeQuery()`, etc.) that are reused across different DAOs, following the **DRY (Don't Repeat Yourself)** principle.
      - **`AbstractController`** classes (if any) could be used for sharing common functionality across multiple controllers, although not explicitly listed here.

4. **Polymorphism:**
    - **Definition:** Polymorphism allows objects of different classes to be treated as objects of a common super class, enabling one interface to be used for a general class of actions.
    - **Applied In:**
      - **Service Interfaces:** The services, such as **`IOrderService`**, **`ICartService`**, and **`IInventoryService`**, allow different service implementations (if needed in the future) to be plugged in without changing the higher-level logic. This makes the system easily extendable.
      - **DAO Interfaces:** DAOs like **`IUserDAO`**, **`IBookDAO`**, and **`IOrderDAO`** enable interaction with different types of databases or data sources, should the implementation change, without altering the business logic.

5. **SOLID Principles:**
    - **Single Responsibility Principle (SRP):** Each class and module is focused on a single responsibility.
      - Example: **`UserDAO`** is responsible for handling all database interactions related to users, while **`InventoryService`** handles stock-related operations.
    - **Open/Closed Principle (OCP):** Classes are open for extension but closed for modification.
      - Example: The use of interfaces like **`IOrderService`** and **`ICartService`** ensures that new functionality can be added by implementing new classes rather than modifying existing ones.
    - **Liskov Substitution Principle (LSP):** Subtypes should be substitutable for their base types.
      - Example: Any implementation of **`IOrderService`** can replace the default service in the application, as long as it adheres to the interface contract.
    - **Interface Segregation Principle (ISP):** Clients should not be forced to depend on interfaces they do not use.
      - Example: Specific, small interfaces like **`ICartService`**, **`IInventoryService`**, and **`IUserService`** are used instead of a monolithic service interface, ensuring that each service has a clear responsibility.
    - **Dependency Inversion Principle (DIP):** High-level modules should not depend on low-level modules. Both should depend on abstractions.
      - Example: **`ServiceManager`** depends on abstractions (interfaces) rather than concrete implementations, promoting flexibility and modularity.

### Design Patterns:

1. **Singleton Pattern:**
    - **Usage:**
      - The **`ServiceManager`** class uses the Singleton pattern to ensure that only one instance of services like **`OrderService`**, **`InventoryService`**, and **`UserService`** is created and shared across the entire application. This prevents multiple instantiations and provides a centralized way to access services.

2. **Factory Pattern:**
    - **Usage:**
      - **`SpringFXMLLoader`** uses the Factory pattern to instantiate JavaFX controllers and inject Spring-managed beans into them. It simplifies the creation of controllers and promotes a loose coupling between the UI and business logic.
      - The controllers themselves are dynamically injected using Spring’s context, enabling flexibility in how UI elements are loaded and managed.

3. **MVC (Model-View-Controller) Architecture:**
    - **Usage:**
      - The entire system is designed following the **MVC architecture**:
        - **Model:** The entities such as **`Book`**, **`User`**, **`Order`**, and **`ShoppingCart`** represent the data model of the system.
        - **View:** JavaFX FXML files define the user interface.
        - **Controller:** Controllers like **`AdminAddBookController`**, **`AdminEditUserController`**, and **`CheckoutConfirmationController`** handle user interactions, update the model, and update the view.

4. **Observer Pattern:**
    - **Usage:**
      - The **`TableView`** and other JavaFX UI components implicitly use the Observer pattern. When data (e.g., book inventory or shopping cart items) is updated, the UI elements that are bound to these data sources automatically reflect the changes, promoting a reactive UI experience.

5. **DAO (Data Access Object) Pattern:**
    - **Usage:**
      - The **DAO pattern** is used to separate the persistence logic from the business logic. DAOs such as **`UserDAO`**, **`BookDAO`**, and **`OrderDAO`** abstract the database interactions, allowing for easy switching of the underlying database engine without modifying the business logic.

6. **Dependency Injection (DI):**
    - **Usage:**
      - The project leverages Spring Framework for **Dependency Injection**. Classes like **`InventoryService`**, **`OrderService`**, and **`UserService`** are injected wherever needed, ensuring loose coupling between components and making the system more testable and scalable.

---

## Contributions

This project was developed as part of the COSC2391 - Further Programming course at RMIT University. The following best practices were applied:

- **Modular Design:** Clear separation of concerns between the model, view, and controller layers.
- **Test-Driven Development:** Unit tests were written to ensure functionality is reliable and robust.
- **Version Control:** Managed with Git, featuring meaningful commit messages and a structured workflow.
- **Code Documentation:** All classes and methods are documented to ensure maintainability.

---

## Contact Information

For questions or further information, please contact:

**Project Lead:** Gia Tin Huynh Duc
**School Email:** s3962053@student.rmit.edu.au 
**Personal Email:** giatinhuynh2612@gmail.com
**github**: [giatinhuynh](https://github.com/giatinhuynh)  
**Course:** COSC2391 - Further Programming  
**Institution:** RMIT University

---
## Thank You

I would like to extend my sincere gratitude to the markers and instructors for their time, effort, and invaluable feedback throughout this project. This assignment has been a valuable learning experience, providing deep insights into Java development, software engineering practices, and the application of Object-Oriented Programming principles.

Your dedication in guiding us through complex programming concepts has been greatly appreciated, and I look forward to applying these lessons in future projects.

Thank you once again for your support, patience, and the opportunity to enhance my skills.
