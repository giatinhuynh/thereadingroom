package com.thereadingroom.utils.ui;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;

/**
 * SpringFXMLLoader is responsible for loading FXML files with the help of Spring's ApplicationContext.
 * This class integrates Spring's dependency injection with JavaFX, allowing controllers and other beans
 * to be managed by Spring's container.
 */
public class SpringFXMLLoader {

    // The Spring ApplicationContext used to manage beans (controllers, services, etc.)
    private final ApplicationContext context;

    /**
     * Constructor for SpringFXMLLoader. It takes an ApplicationContext as a dependency.
     * This allows the loader to use Spring's context for managing FXML controllers and other beans.
     *
     * @param context The Spring ApplicationContext, which will be used to inject beans.
     */
    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Loads an FXML file and sets up the controller using Spring's dependency injection.
     * This method ensures that controllers specified in the FXML file are retrieved from Spring's ApplicationContext,
     * allowing full integration of Spring's bean management.
     *
     * @param url The URL to the FXML file to load.
     * @return The root node of the loaded FXML file, usually a Parent or Scene.
     * @throws IOException if there is an issue loading the FXML file.
     */
    public Object load(URL url) throws IOException {
        FXMLLoader loader = new FXMLLoader(url);  // Create a new FXMLLoader instance with the given FXML URL
        loader.setControllerFactory(context::getBean);  // Set controller factory to use Spring's bean factory
        return loader.load();  // Load the FXML and return the root node
    }
}
