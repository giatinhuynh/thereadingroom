package com.thereadingroom.utils;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

/**
 * Utility class to initialize the JavaFX platform for unit tests or applications
 * that need to interact with JavaFX components outside the main thread.
 */
public class JavaFXInitializer {
    // Static flag to ensure JavaFX is only initialized once
    private static boolean initialized = false;

    /**
     * Initializes the JavaFX platform if it has not been initialized yet.
     * This method uses a CountDownLatch to block until the JavaFX toolkit is fully initialized.
     */
    public static void initialize() {
        // Only initialize JavaFX once
        if (!initialized) {
            // Create a latch to wait for JavaFX initialization
            CountDownLatch latch = new CountDownLatch(1);

            // Start the JavaFX application thread
            Platform.startup(() -> {
                // No-op, just to initialize JavaFX
                latch.countDown();
            });

            try {
                // Wait for JavaFX to be initialized before continuing
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();  // Handle thread interruption
            }

            // Mark JavaFX as initialized to avoid reinitialization
            initialized = true;
        }
    }
}
