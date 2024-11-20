package com.thereadingroom.view;

import com.thereadingroom.config.AppConfig;
import com.thereadingroom.model.dao.database.DatabaseInitializer;
import com.thereadingroom.utils.ui.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;

/**
 * Main entry point for the JavaFX application.
 * This class is responsible for bootstrapping the Spring application context and launching the JavaFX UI.
 */
public class Main extends Application {

    private ApplicationContext springContext;  // Spring application context for managing beans

    /**
     * Initializes the application.
     * This method sets up the Spring context and initializes the database.
     */
    @Override
    public void init() {
        // Initialize the Spring application context using the AppConfig class
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // Initialize the database by creating tables, adding an admin user, and populating initial data
        DatabaseInitializer.initializeDatabase();
    }

    /**
     * Starts the JavaFX application by loading the login screen.
     * This method sets up the primary stage and displays the login UI.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML for the login screen using Spring's context-aware loader
            SpringFXMLLoader loader = new SpringFXMLLoader(springContext);
            URL fxmlLocation = getClass().getResource("/com/thereadingroom/fxml/common/login.fxml");

            // Load the root element from the FXML file
            Parent root = (Parent) loader.load(fxmlLocation);

            // Create a new scene with a size of 1280x720
            Scene scene = new Scene(root, 1280, 720);

            // Configure the primary stage with the scene
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");  // Set the window title
            primaryStage.setResizable(false);  // Disable window resizing
            primaryStage.show();  // Display the window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method, the entry point of the Java application.
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
