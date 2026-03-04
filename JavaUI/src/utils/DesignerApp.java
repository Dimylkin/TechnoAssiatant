package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.WindowHistory;
import ui.WindowMain;
import ui.WindowSettings;

/**
 * Main application class for the Designer application.
 * Provides a JavaFX-based UI with a title bar, scrollable content area, and customizable form card.
 */
public class DesignerApp extends Application {

    private static final String SYSTEMS_DIR = "resources/assets/systems/";
    private static final String IMAGES_SYSTEMS_DIR = "resources/images/systems/";
    private static final String CONFIG_PATH = SYSTEMS_DIR + "config.json";
    private static final String STYLES_PATH = SYSTEMS_DIR + "styles.json";

    /** Root container for the entire application layout. */
    public VBox root;

    /** Container for the main content area within the scroll pane. */
    public VBox contentBox;

    /** Card container for form elements with rounded corners and shadow effect. */
    public VBox formCard;

    /** Scroll pane that wraps the content area to enable vertical scrolling. */
    public ScrollPane scrollPane;

    /** Primary stage (main window) of the application. */
    public Stage primaryStage;

    /** Color adjustment effect used to dim the application interface. */
    public final ColorAdjust dimEffect = new ColorAdjust();

    /** Title text displayed in the application's title bar and window title. */
    public String title = "";

    public enum Transition {
        HOME,
        SETTINGS,
        HISTORY,
    }

    public static enum UsingWindow {
        HOME,
        RESULT,
        SETTINGS,
        HISTORY,
        HISTORY_REQUEST,
    }

    public UsingWindow usingWindow;

    public Double width = 900.0, height = 600.0;
    public String language = "ru", theme = "light", model = "";

    public JSONObject languageObject = new JSONObject();
    public JSONObject themeObject = new JSONObject();

    public DesignerApp(Stage primary, UsingWindow usingWindow) {
        this.primaryStage = primary;
        this.usingWindow = usingWindow;

        loadingConfig();
        start(primary);
    }

    private String t(String key) {
        return themeObject != null ? themeObject.optString(key, "") : "";
    }

    private void applyStyle(javafx.scene.Node node, String key) {
        String css = t(key);
        if (css != null && !css.isBlank()) node.setStyle(css);
    }

    private static String readFileUtf8(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private void loadingConfig() {
        try {
            Path path = Paths.get(CONFIG_PATH);
            if (!Files.exists(path) || Files.size(path) == 0) {
                // оставляем дефолты
                loadingLanguage(language);
                loadingTheme(theme);
                return;
            }

            String content = readFileUtf8(path);
            JSONObject object = new JSONObject(content);

            width = object.optDouble("width", width);
            height = object.optDouble("height", height);

            language = object.optString("language", language);
            loadingLanguage(language);

            theme = object.optString("theme", theme);
            loadingTheme(theme);

            model = object.optString("model", model);

            // Если есть title в config — берём, иначе можно взять из languageObject (если у тебя там есть ключ).
            title = object.optString("title", title);
            if (title.isBlank() && languageObject != null) {
                title = languageObject.optString("appTitle", title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // даже если конфиг сломан — попытаться подгрузить язык/тему по дефолтам
            loadingLanguage(language);
            loadingTheme(theme);
        }
    }

    private void loadingLanguage(String language) {
        try {
            Path path = Paths.get(SYSTEMS_DIR + "language_" + language + ".json");
            if (!Files.exists(path) || Files.size(path) == 0) {
                languageObject = new JSONObject();
                return;
            }

            String content = readFileUtf8(path);
            languageObject = new JSONObject(content);

        } catch (Exception e) {
            e.printStackTrace();
            languageObject = new JSONObject();
        }
    }

    private void loadingTheme(String theme) {
        try {
            Path path = Paths.get(STYLES_PATH);

            if (!Files.exists(path) || Files.size(path) == 0) {
                themeObject = new JSONObject();
                return;
            }

            String content = readFileUtf8(path);
            JSONObject root = new JSONObject(content);

            if (!root.has(theme) || root.isNull(theme)) {
                theme = "light";
            }

            themeObject = root.getJSONObject(theme);

        } catch (Exception e) {
            e.printStackTrace();
            themeObject = new JSONObject();
        }
    }

    public Boolean savingConfig(JSONObject object) {
        try {
            File file = new File(CONFIG_PATH);
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();

            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write(object.toString(2));
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        root = new VBox();
        applyStyle(root, "background");

        HBox titleHBox = createTitleBar();

        // Важно: НЕ затеняем поле локальной переменной
        scrollPane = createContentArea();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(titleHBox, scrollPane);

        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("Techno Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public HBox createTitleBar() {
        HBox titleHBox = new HBox(15);
        titleHBox.setAlignment(Pos.CENTER);
        titleHBox.setPadding(new Insets(20));
        applyStyle(titleHBox, "title");

        Button homeButton = createIconButton(Transition.HOME, IMAGES_SYSTEMS_DIR + "icon_home.png");
        Button settingsButton = createIconButton(Transition.SETTINGS, IMAGES_SYSTEMS_DIR + "icon_settings.png");
        Button historyButton = createIconButton(Transition.HISTORY, IMAGES_SYSTEMS_DIR + "icon_history.png");

        Label titleLabel = new Label(title);
        applyStyle(titleLabel, "labelTitle");

        titleHBox.getChildren().addAll(homeButton, settingsButton, historyButton, titleLabel);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        return titleHBox;
    }

    private void setNavButtonStyle(Button button) {
        button.setStyle(t("navButtonOnMouseExited"));
        button.setOnMouseEntered(e -> button.setStyle(t("navButtonOnMouseEntered")));
        button.setOnMouseExited(e -> button.setStyle(t("navButtonOnMouseExited")));
    }

    private void setNavActionIfNeeded(Button button, Transition type) {
        // логика та же: если уже на нужном окне — action не ставим
        switch (type) {
            case HISTORY -> {
                if (usingWindow != UsingWindow.HISTORY) {
                    button.setOnAction(_ -> new WindowHistory().start(primaryStage));
                }
            }
            case HOME -> {
                if (usingWindow != UsingWindow.HOME) {
                    button.setOnAction(_ -> new WindowMain().start(primaryStage));
                }
            }
            case SETTINGS -> {
                if (usingWindow != UsingWindow.SETTINGS) {
                    button.setOnAction(_ -> new WindowSettings().start(primaryStage));
                }
            }
        }
    }

    public Button createIconButton(Transition type, String iconPath) {
        Button button = new Button("");
        button.setPrefSize(35, 35);
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);

        // Надёжнее грузить через stream: работает и из IDE, и при сборке, если пути реальные.
        try (InputStream is = Files.newInputStream(Paths.get(iconPath))) {
            Image image = new Image(is);
            if (image.isError()) throw new IOException("Image error: " + iconPath);

            ImageView icon = new ImageView(image);
            icon.setFitWidth(25);
            icon.setFitHeight(25);
            icon.setPreserveRatio(true);

            button.setGraphic(icon);
            button.setText("");

        } catch (Exception e) {
            button.setGraphic(null);
            button.setText("?");
            System.out.println("Ошибка загрузки: " + iconPath);
        }

        setNavButtonStyle(button);
        setNavActionIfNeeded(button, type);

        return button;
    }

    private ScrollPane createContentArea() {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        applyStyle(sp, "scrollPane");

        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        formCard = createFormCard();
        contentBox.getChildren().add(formCard);

        sp.setContent(contentBox);
        return sp;
    }

    private VBox createFormCard() {
        VBox card = new VBox(20);
        card.setMaxWidth(800);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        applyStyle(card, "card");
        return card;
    }

    public void dimApplication(boolean dim) {
        dimEffect.setBrightness(dim ? -0.5 : 0.0);
        root.setEffect(dimEffect);
    }

    public String getStyleButton(boolean hover) {
        return t(hover ? "buttonOnMouseEntered" : "buttonOnMouseExited");
    }
}