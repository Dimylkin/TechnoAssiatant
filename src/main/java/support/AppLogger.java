package support;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {
    private static final Logger LOGGER = Logger.getLogger("TechnoAssistant");

    private AppLogger() {
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }

    public static void error(String message, Throwable error) {
        LOGGER.log(Level.SEVERE, message, error);
    }

    public static void warning(String message, Throwable error) {
        LOGGER.log(Level.WARNING, message, error);
    }
}
