package com.thereadingroom.utils.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class for managing common UI operations in a JavaFX application integrated with Spring.
 * Provides methods for loading scenes, showing alerts, handling CSS, and managing images.
 */
@Component
public class UIUtils {

    private final SpringFXMLLoader springFXMLLoader;
    private final int defaultWidth;
    private final int defaultHeight;
    private final ApplicationContext springContext;

    /**
     * Constructor with default scene dimensions.
     *
     * @param springFXMLLoader Spring loader for FXML files.
     * @param springContext    The Spring application context.
     */
    @Autowired
    public UIUtils(SpringFXMLLoader springFXMLLoader, ApplicationContext springContext) {
        this(springFXMLLoader, springContext, 1280, 720);  // Default scene dimensions
    }

    /**
     * Constructor that allows specifying scene dimensions.
     *
     * @param springFXMLLoader Spring loader for FXML files.
     * @param springContext    The Spring application context.
     * @param defaultWidth     Default width of the scene.
     * @param defaultHeight    Default height of the scene.
     */
    public UIUtils(SpringFXMLLoader springFXMLLoader, ApplicationContext springContext, int defaultWidth, int defaultHeight) {
        this.springFXMLLoader = springFXMLLoader;
        this.springContext = springContext;  // Store the Spring context
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
    }

    /**
     * Displays an informational alert.
     *
     * @param title   The title of the alert.
     * @param message The message content of the alert.
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error alert.
     *
     * @param title   The title of the alert.
     * @param message The error message content of the alert.
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog and returns whether the user confirmed.
     *
     * @param title   The title of the confirmation dialog.
     * @param message The message content of the confirmation dialog.
     * @return true if the user confirmed, false otherwise.
     */
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Loads a new scene using the given FXML path and displays it on the provided stage.
     *
     * @param fxmlPath The path to the FXML file.
     * @param stage    The stage where the scene will be set.
     * @param title    The title of the window.
     */
    public void loadScene(String fxmlPath, Stage stage, String title) {
        loadScene(fxmlPath, stage, title, defaultWidth, defaultHeight);
    }

    /**
     * Loads a new scene with custom dimensions.
     *
     * @param fxmlPath The path to the FXML file.
     * @param stage    The stage where the scene will be set.
     * @param title    The title of the window.
     * @param width    The width of the scene.
     * @param height   The height of the scene.
     */
    public void loadScene(String fxmlPath, Stage stage, String title, int width, int height) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            Parent view = (Parent) springFXMLLoader.load(resourceUrl);  // Use SpringFXMLLoader for loading
            Scene scene = new Scene(view, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Unable to load the view: " + e.getMessage());
        }
    }

    /**
     * Loads a modal window with a given FXML file and data injector.
     *
     * @param fxmlPath        The path to the FXML file.
     * @param title           The title of the modal window.
     * @param dataLoader      The data loader used to inject data into the controller.
     * @param owner           The owner stage of the modal window.
     * @param <T>             The type of the controller.
     */
    public <T> void loadModal(String fxmlPath, String title, DataLoader<T> dataLoader, Stage owner) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(resourceUrl);  // Create a new FXMLLoader instance
            loader.setControllerFactory(springContext::getBean);  // Use Spring context to inject controllers

            Parent root = loader.load();  // Load the FXML
            T controller = loader.getController();  // Get the controller
            dataLoader.loadData(controller);  // Pass the loaded controller to the dataLoader

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(owner);
            modalStage.setScene(new Scene(root));
            modalStage.setTitle(title);
            modalStage.setResizable(false);
            modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Unable to load the modal: " + e.getMessage());
        }
    }

    /**
     * Loads a scene with additional data injected into the controller.
     *
     * @param fxmlPath           The path to the FXML file.
     * @param currentStage       The stage where the scene will be set.
     * @param title              The title of the window.
     * @param controllerInjector A lambda that injects data into the controller.
     */
    public void loadSceneWithData(String fxmlPath, Stage currentStage, String title, Consumer<Object> controllerInjector) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(resourceUrl);  // Create a new FXMLLoader instance
            loader.setControllerFactory(springContext::getBean);  // Use Spring context to inject controllers

            Parent root = loader.load();  // Load the FXML
            Scene scene = new Scene(root, defaultWidth, defaultHeight);
            currentStage.setScene(scene);
            currentStage.setTitle(title);

            Object controller = loader.getController();  // Get the controller
            controllerInjector.accept(controller);  // Inject the data into the controller

            currentStage.show();
        } catch (IOException e) {
            showError("Loading Error", "Failed to load the scene.");
            e.printStackTrace();
        }
    }

    /**
     * Loads a scene with data injected into the controller, using default scene dimensions.
     *
     * @param fxmlPath           The path to the FXML file.
     * @param currentStage       The stage where the scene will be set.
     * @param title              The title of the window.
     * @param controllerInjector A lambda that injects data into the controller.
     */
    public void loadSceneWithDataDefault(String fxmlPath, Stage currentStage, String title, Consumer<Object> controllerInjector) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(resourceUrl);  // Create a new FXMLLoader instance
            loader.setControllerFactory(springContext::getBean);  // Use Spring context to inject controllers

            Parent root = loader.load();  // Load the FXML
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle(title);

            Object controller = loader.getController();  // Get the controller
            controllerInjector.accept(controller);  // Inject the data into the controller

            currentStage.show();
        } catch (IOException e) {
            showError("Loading Error", "Failed to load the scene.");
            e.printStackTrace();
        }
    }

    /**
     * Closes the current window.
     *
     * @param node A node inside the window to be closed.
     */
    public void closeCurrentWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads an image from the specified path into an ImageView.
     *
     * @param imageView The ImageView where the image will be displayed.
     * @param imagePath The path to the image file.
     */
    public void loadImage(ImageView imageView, String imagePath) {
        InputStream imageStream = UIUtils.class.getResourceAsStream(imagePath);
        if (imageStream == null) {
            showError("Image Load Error", "Unable to load image from path: " + imagePath);
            return;
        }
        try {
            Image image = new Image(imageStream);
            imageView.setImage(image);
        } catch (Exception e) {
            showError("Image Load Error", "Error loading image: " + imagePath);
            e.printStackTrace();
        }
    }

    /**
     * Loads a CSS stylesheet and applies it to the given Parent node.
     *
     * @param root    The root node to apply the stylesheet to.
     * @param cssPath The path to the CSS file.
     */
    public void loadCSS(Parent root, String cssPath) {
        URL resource = UIUtils.class.getResource(cssPath);
        if (resource != null) {
            String stylesheetPath = resource.toExternalForm();
            root.getStylesheets().add(stylesheetPath);
        } else {
            System.err.println("Error: Unable to load CSS from path: " + cssPath);
        }
    }

    /**
     * Interface for loading data into a controller.
     *
     * @param <T> The type of the controller.
     */
    public interface DataLoader<T> {
        void loadData(T controller);
    }
}
