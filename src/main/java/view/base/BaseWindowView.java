package view.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import logic.WindowHistoryLogic;
import logic.WindowMainLogic;
import logic.WindowSettingsLogic;
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
import logic.json.JsonObject;
import support.AppLogger;
import support.AppPaths;
import support.JsonFileStore;

/**
 * Main application class for the Designer application.
 * Provides a JavaFX-based UI with a title bar, scrollable content area, and customizable form card.
 */
public class BaseWindowView extends Application {

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

    public enum UsingWindow {
        HOME,
        RESULT,
        SETTINGS,
        HISTORY,
        HISTORY_REQUEST,
    }

    public UsingWindow usingWindow;

    public Double width = 900.0, height = 600.0;
    public String language = "ru", theme = "light", model = "";

    public JsonObject languageObject = new JsonObject();
    public JsonObject themeObject = new JsonObject();

    public BaseWindowView(Stage primary, UsingWindow usingWindow) {
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

    private void loadingConfig() {
        try {
            if (!Files.exists(AppPaths.CONFIG_FILE) || Files.size(AppPaths.CONFIG_FILE) == 0) {
                loadingLanguage(language);
                loadingTheme(theme);
                return;
            }

            JsonObject object = JsonFileStore.readObject(AppPaths.CONFIG_FILE);

            width = object.optDouble("width", width);
            height = object.optDouble("height", height);

            language = object.optString("language", language);
            loadingLanguage(language);

            theme = object.optString("theme", theme);
            loadingTheme(theme);

            model = object.optString("model", model);

            title = object.optString("title", title);
            if (title.isBlank() && languageObject != null) {
                title = languageObject.optString("appTitle", title);
            }

        } catch (Exception e) {
            AppLogger.error("Failed to load application config", e);
            loadingLanguage(language);
            loadingTheme(theme);
        }
    }

    private void loadingLanguage(String language) {
        try {
            if (!Files.exists(AppPaths.languageFile(language)) || Files.size(AppPaths.languageFile(language)) == 0) {
                languageObject = new JsonObject();
                return;
            }

            languageObject = JsonFileStore.readObject(AppPaths.languageFile(language));

        } catch (Exception e) {
            AppLogger.error("Failed to load language: " + language, e);
            languageObject = new JsonObject();
        }
    }

    private void loadingTheme(String theme) {
        try {
            if (!Files.exists(AppPaths.STYLES_FILE) || Files.size(AppPaths.STYLES_FILE) == 0) {
                themeObject = new JsonObject();
                return;
            }

            JsonObject root = JsonFileStore.readObject(AppPaths.STYLES_FILE);

            if (!root.has(theme) || root.isNull(theme)) {
                theme = "light";
            }

            themeObject = root.getJSONObject(theme);

        } catch (Exception e) {
            AppLogger.error("Failed to load theme: " + theme, e);
            themeObject = new JsonObject();
        }
    }

    public Boolean savingConfig(JsonObject object) {
        try {
            JsonFileStore.writeObject(AppPaths.CONFIG_FILE, object);
            return true;

        } catch (Exception e) {
            AppLogger.error("Failed to save application config", e);
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

        Button homeButton = createIconButton(Transition.HOME, AppPaths.systemImage("icon_home.png").toString());
        Button settingsButton = createIconButton(Transition.SETTINGS, AppPaths.systemImage("icon_settings.png").toString());
        Button historyButton = createIconButton(Transition.HISTORY, AppPaths.systemImage("icon_history.png").toString());

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
                    button.setOnAction(event -> new WindowHistoryLogic().start(primaryStage));
                }
            }
            case HOME -> {
                if (usingWindow != UsingWindow.HOME) {
                    button.setOnAction(event -> new WindowMainLogic().start(primaryStage));
                }
            }
            case SETTINGS -> {
                if (usingWindow != UsingWindow.SETTINGS) {
                    button.setOnAction(event -> new WindowSettingsLogic().start(primaryStage));
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
        try (InputStream is = Files.newInputStream(java.nio.file.Paths.get(iconPath))) {
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
            AppLogger.warning("Failed to load icon: " + iconPath, e);
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