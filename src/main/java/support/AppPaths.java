package support;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Centralized filesystem paths used by the desktop application. */
public final class AppPaths {
    public static final Path SYSTEM_ASSETS_DIR = Paths.get("src/main/resources/assets/systems");
    public static final Path HISTORY_FILE = Paths.get("src/main/resources/assets/history/history.json");
    public static final Path CONFIG_FILE = SYSTEM_ASSETS_DIR.resolve("config.json");
    public static final Path STYLES_FILE = SYSTEM_ASSETS_DIR.resolve("styles.json");
    public static final Path SYSTEM_IMAGES_DIR = Paths.get("src/main/resources/images/systems");
    public static final Path PC_IMAGES_DIR = Paths.get("src/main/resources/images/pc");
    public static final Path PYTHON_PREDICT_SCRIPT = Paths.get("PythonAI/helpers/predict.py");

    private AppPaths() {
    }

    public static Path languageFile(String language) {
        return SYSTEM_ASSETS_DIR.resolve("language_" + language + ".json");
    }

    public static Path systemImage(String fileName) {
        return SYSTEM_IMAGES_DIR.resolve(fileName);
    }

    public static Path pcImage(String fileName) {
        return PC_IMAGES_DIR.resolve(fileName);
    }
}
