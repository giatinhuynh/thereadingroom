package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.User;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing user accounts in the admin panel.
 * Provides functionality to display, search, edit, and remove users.
 */
@Controller
public class AdminManageUsersController {

    @FXML
    private TableView<User> userTableView;  // Table view for displaying the list of users

    @FXML
    private TableColumn<User, Integer> userIdColumn;  // Column for displaying the user ID

    @FXML
    private TableColumn<User, String> usernameColumn;  // Column for displaying the username

    @FXML
    private TableColumn<User, String> firstNameColumn;  // Column for displaying the user's first name

    @FXML
    private TableColumn<User, String> lastNameColumn;  // Column for displaying the user's last name

    @FXML
    private TableColumn<User, Void> actionColumn;  // Column for action buttons (Edit, Remove)

    @FXML
    private TextField searchField;  // Input field for searching users by ID or username

    private final IUserService userService;  // Service for handling user-related operations
    private final UIUtils uiUtils;  // Utility instance for UI-related tasks

    private List<User> allUsers;  // List of all users, excluding admin users

    /**
     * Constructor-based dependency injection for UserService and UIUtils.
     *
     * @param userService The service for managing user operations.
     * @param uiUtils Utility for handling UI-related tasks.
     */
    @Autowired
    public AdminManageUsersController(IUserService userService, UIUtils uiUtils) {
        this.userService = userService;
        this.uiUtils = uiUtils;
    }

    /**
     * Initializes the controller after the components are fully loaded.
     * Sets up the table columns, loads users, and applies styles.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        loadUsers();  // Load users initially and populate the table
        applyStyles();
    }

    /**
     * Applies custom CSS styles to the table view.
     */
    private void applyStyles() {
        uiUtils.loadCSS(userTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Configures the columns of the user table with the appropriate data fields.
     */
    private void setupTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }

    /**
     * Adds Edit and Remove buttons to the action column for each user row.
     */
    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = createEditButton();
                    private final Button removeButton = createRemoveButton();

                    // Creates an "Edit" button for each row
                    private Button createEditButton() {
                        Button button = new Button("Edit");
                        button.setStyle("-fx-background-color: #228b22; -fx-text-fill: white;");
                        button.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);  // Handle the editing of the user
                            reloadUserTableWithActions();  // Reload the table after editing
                        });
                        return button;
                    }

                    // Creates a "Remove" button for each row
                    private Button createRemoveButton() {
                        Button button = new Button("Remove");
                        button.setStyle("-fx-background-color: #d2691e; -fx-text-fill: white;");
                        button.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleRemoveUser(user);  // Handle the removal of the user
                            reloadUserTableWithActions();  // Reload the table after removing
                        });
                        return button;
                    }

                    // Update the cell with action buttons if the row is not empty
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);  // No buttons for empty rows
                        } else {
                            HBox actionButtons = new HBox(editButton, removeButton);
                            actionButtons.setSpacing(10);
                            actionButtons.setAlignment(Pos.CENTER);
                            setGraphic(actionButtons);  // Display the action buttons for non-empty rows
                        }
                    }
                };
            }
        });
    }

    /**
     * Loads all non-admin users from the database and populates the table view.
     */
    private void loadUsers() {
        allUsers = userService.getAllUsers()
                .stream()
                .filter(user -> !user.isAdmin())  // Exclude admin users
                .collect(Collectors.toList());

        reloadUserTableWithActions();  // Populate the table with users and add action buttons
    }

    /**
     * Updates the table view with the provided list of users.
     *
     * @param users The list of users to display in the table.
     */
    private void updateTableView(List<User> users) {
        userTableView.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Handles the search functionality by filtering users based on the search query.
     * Filters users by ID or username and updates the table view with the results.
     */
    @FXML
    public void handleSearchUser() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            reloadUserTableWithActions();  // Reload all users when search is cleared
            return;
        }

        List<User> filteredUsers = filterUsers(query);  // Filter users based on the query
        if (filteredUsers.isEmpty()) {
            uiUtils.showError("Search Error", "No matching users found.");
        } else {
            updateTableView(filteredUsers);  // Display filtered users
        }
    }

    /**
     * Filters the list of all users based on the provided search query.
     * Supports filtering by user ID or username.
     *
     * @param query The search query.
     * @return A list of users that match the query.
     */
    private List<User> filterUsers(String query) {
        return allUsers.stream()
                .filter(user -> String.valueOf(user.getId()).equals(query) ||
                        user.getUsername().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    /**
     * Opens the user edit modal and populates it with the selected user's data.
     *
     * @param user The user to be edited.
     */
    private void handleEditUser(User user) {
        uiUtils.loadModal("/com/thereadingroom/fxml/admin/admin_edit_user.fxml", "Edit User", controller -> {
            AdminEditUserController editController = (AdminEditUserController) controller;
            editController.setUser(user);  // Pass the selected user to the edit controller
        }, (Stage) userTableView.getScene().getWindow());

        reloadUserTableWithActions();  // Reload the table after editing a user
        loadUsers();
    }

    /**
     * Handles the removal of a user after confirming the action.
     *
     * @param user The user to be removed.
     */
    private void handleRemoveUser(User user) {
        if (confirmUserDeletion()) {
            deleteUser(user);  // Delete the user if confirmed
        }

        reloadUserTableWithActions();  // Reload the table after removing a user
        loadUsers();
    }

    /**
     * Displays a confirmation dialog to the admin before deleting a user.
     *
     * @return true if the deletion is confirmed, false otherwise.
     */
    private boolean confirmUserDeletion() {
        return uiUtils.showConfirmation("Confirm Deletion", "Are you sure you want to delete the user?");
    }

    /**
     * Deletes the user and displays a success or error message based on the operation result.
     *
     * @param user The user to be deleted.
     */
    private void deleteUser(User user) {
        boolean success = userService.deleteUser(user.getId());
        if (success) {
            uiUtils.showAlert("Success", "User removed successfully.");
        } else {
            uiUtils.showError("Error", "Failed to remove user.");
        }
    }

    /**
     * Reloads the user table and refreshes the UI components, ensuring that
     * the action buttons (Edit, Remove) are re-added to each row.
     */
    public void reloadUserTableWithActions() {
        userTableView.setItems(FXCollections.observableArrayList(allUsers));  // Set all users to the table
        userTableView.refresh();  // Refresh the table UI
        addActionButtonsToTable();  // Add the action buttons back to the table
    }
}
