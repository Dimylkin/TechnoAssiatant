package ui;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DesignerApp;

/**
 * Settings window class for application configuration.
 * Provides options for window size, theme, language, and AI model version.
 */
public class WindowSettings extends Application {

    private static final int MIN_WIDTH = 800;
    private static final int MAX_WIDTH = 3840;
    private static final int MIN_HEIGHT = 600;
    private static final int MAX_HEIGHT = 2160;

    public enum ActionWithData {
        CONVERT,
        UNCONVERT
    }

    public Stage primaryStage;
    public DesignerApp designer;

    public JSONObject languageObject;
    public JSONObject themeObject;

    private TextField widthField;
    private TextField heightField;
    private ComboBox<String> themeComboBox;
    private ComboBox<String> languageComboBox;
    private ComboBox<String> modelVersionComboBox;

    private Double width, height;
    private String language, theme, model;

    public WindowSettings() {}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        primaryStage = primary;
        designer = new DesignerApp(primaryStage, DesignerApp.UsingWindow.SETTINGS);

        languageObject = designer.languageObject;
        themeObject = designer.themeObject;

        loadingConfig();
        createContent();
    }

    private JSONObject ws() {
        return languageObject.getJSONObject("WindowSettings");
    }

    private String t(String key) {
        return themeObject != null ? themeObject.optString(key, "") : "";
    }

    private void applyStyle(javafx.scene.Node node, String key) {
        String css = t(key);
        if (css != null && !css.isBlank()) node.setStyle(css);
    }

    private void setPrimaryButtonHover(Button btn) {
        btn.setStyle(t("primaryButtonOnMouseExited"));
        btn.setOnMouseEntered(_ -> btn.setStyle(t("primaryButtonOnMouseEntered")));
        btn.setOnMouseExited(_ -> btn.setStyle(t("primaryButtonOnMouseExited")));
    }

    private void loadingConfig() {
        width = designer.width;
        height = designer.height;
        model = designer.model;

        // language/theme хранятся в config как "russian/english" и "light/dark"
        language = parseLanguage(ActionWithData.CONVERT, designer.language); // -> "Русский"/"English"
        theme = designer.theme; // "light"/"dark"
    }

    private boolean isUiRussian() {
        // language здесь уже "Русский"/"English" (после CONVERT)
        return "Русский".equalsIgnoreCase(language);
    }

    private String parseLanguage(ActionWithData type, String language) {
        if (type == ActionWithData.CONVERT) {
            if ("russian".equalsIgnoreCase(language)) return "Русский";
            if ("english".equalsIgnoreCase(language)) return "English";
            return "Русский";
        } else {
            if ("Русский".equalsIgnoreCase(language)) return "russian";
            if ("English".equalsIgnoreCase(language)) return "english";
            return "russian";
        }
    }

    private String translatingTheme(String themeKey) {
        // themeKey: "light"/"dark"
        if (isUiRussian()) {
            if ("light".equalsIgnoreCase(themeKey)) return ws().getString("theme.light"); // "Светлая"
            if ("dark".equalsIgnoreCase(themeKey)) return ws().getString("theme.dark");   // "Темная"
            return ws().getString("theme.light");
        } else {
            if ("light".equalsIgnoreCase(themeKey)) return ws().getString("theme.light"); // "Light"
            if ("dark".equalsIgnoreCase(themeKey)) return ws().getString("theme.dark");   // "Dark"
            return ws().getString("theme.light");
        }
    }

    private String parseTheme(ActionWithData type, String themeValue) {
        // UI значение может быть "Светлая/Темная" или "Light/Dark" (из language json)
        String lightUi = ws().getString("theme.light");
        String darkUi = ws().getString("theme.dark");

        if (type == ActionWithData.CONVERT) {
            // сюда обычно не используем, но оставим корректно
            if ("light".equalsIgnoreCase(themeValue)) return lightUi;
            if ("dark".equalsIgnoreCase(themeValue)) return darkUi;
            return lightUi;
        } else {
            if (lightUi.equalsIgnoreCase(themeValue)) return "light";
            if (darkUi.equalsIgnoreCase(themeValue)) return "dark";
            return "light";
        }
    }

    /**
     * Creates and configures all content for the settings window.
     */
    public void createContent() {
        Label titleLabel = new Label(ws().getString("label.title"));
        titleLabel.setStyle(t("labelTitle") + " " + t("accentText") + " -fx-padding: 0 0 30 0;");

        VBox windowSizeBlock = createWindowSizeBlock();
        VBox themeBlock = createThemeBlock();
        VBox languageBlock = createLanguageBlock();
        VBox modelVersionBlock = createModelVersionBlock();

        Button saveButton = new Button(ws().getString("saveButton"));
        saveButton.setMaxWidth(Double.MAX_VALUE);
        setPrimaryButtonHover(saveButton);
        saveButton.setOnAction(_ -> saveSettings());

        VBox.setMargin(saveButton, new Insets(30, 0, 0, 0));

        designer.formCard.getChildren().addAll(
            titleLabel,
            windowSizeBlock,
            themeBlock,
            languageBlock,
            modelVersionBlock,
            saveButton
        );
    }

    private VBox createWindowSizeBlock() {
        VBox block = new VBox(10);
        applyStyle(block, "cardSmall");

        Label label = new Label(ws().getString("label.windowSize"));
        applyStyle(label, "labelMiddle");

        HBox fieldsBox = new HBox(15);
        fieldsBox.setAlignment(Pos.CENTER_LEFT);

        Label widthLabel = new Label(ws().getString("label.windowWidth"));
        applyStyle(widthLabel, "labelSmall");
        widthLabel.setPrefWidth(80);

        widthField = new TextField(String.valueOf((int) primaryStage.getWidth()));
        widthField.setPromptText(width.toString());
        widthField.setStyle(t("input"));
        widthField.setPrefWidth(100);

        Label heightLabel = new Label(ws().getString("label.windowHeight"));
        applyStyle(heightLabel, "labelSmall");
        heightLabel.setPrefWidth(80);

        heightField = new TextField(String.valueOf((int) primaryStage.getHeight()));
        heightField.setPromptText(height.toString());
        heightField.setStyle(t("input"));
        heightField.setPrefWidth(100);

        fieldsBox.getChildren().addAll(widthLabel, widthField, heightLabel, heightField);

        Label descLabel = new Label(ws().getString("label.windowDesc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 12px;");

        block.getChildren().addAll(label, fieldsBox, descLabel);
        VBox.setMargin(block, new Insets(0, 0, 20, 0));

        return block;
    }

    private VBox createThemeBlock() {
        VBox block = new VBox(10);
        applyStyle(block, "cardSmall");

        Label label = new Label(ws().getString("label.theme"));
        applyStyle(label, "labelMiddle");

        ObservableList<String> themes = FXCollections.observableArrayList(
            ws().getString("theme.light"),
            ws().getString("theme.dark")
        );

        themeComboBox = new ComboBox<>(themes);
        themeComboBox.setValue(translatingTheme(theme)); // theme: "light"/"dark"
        themeComboBox.setMaxWidth(Double.MAX_VALUE);
        themeComboBox.setStyle(t("input"));

        Label descLabel = new Label(ws().getString("label.themeDesc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 12px;");

        block.getChildren().addAll(label, themeComboBox, descLabel);
        VBox.setMargin(block, new Insets(0, 0, 20, 0));

        return block;
    }

    private VBox createLanguageBlock() {
        VBox block = new VBox(10);
        applyStyle(block, "cardSmall");

        Label label = new Label(ws().getString("label.language"));
        applyStyle(label, "labelMiddle");

        ObservableList<String> languages = FXCollections.observableArrayList("Русский", "English");

        languageComboBox = new ComboBox<>(languages);
        languageComboBox.setValue(language);
        languageComboBox.setMaxWidth(Double.MAX_VALUE);
        languageComboBox.setStyle(t("input"));

        Label descLabel = new Label(ws().getString("label.languageDesc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 12px;");

        block.getChildren().addAll(label, languageComboBox, descLabel);
        VBox.setMargin(block, new Insets(0, 0, 20, 0));

        return block;
    }

    private VBox createModelVersionBlock() {
        VBox block = new VBox(10);
        applyStyle(block, "cardSmall");

        Label label = new Label(ws().getString("label.model"));
        applyStyle(label, "labelMiddle");

        ObservableList<String> modelVersions = FXCollections.observableArrayList("v1.0.0", "v1.0.1");

        modelVersionComboBox = new ComboBox<>(modelVersions);
        modelVersionComboBox.setValue(model);
        modelVersionComboBox.setMaxWidth(Double.MAX_VALUE);
        modelVersionComboBox.setStyle(t("input"));

        Label descLabel = new Label(ws().getString("label.modelDesc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 12px;");

        block.getChildren().addAll(label, modelVersionComboBox, descLabel);
        VBox.setMargin(block, new Insets(0, 0, 20, 0));

        return block;
    }

    private void saveSettings() {
        // 1) валидация размеров (логика была, просто раньше не использовалась)
        if (!applyWindowSize()) return;

        // 2) применяем значения
        width = safeParseDouble(widthField.getText(), width);
        height = safeParseDouble(heightField.getText(), height);

        language = parseLanguage(ActionWithData.UNCONVERT, languageComboBox.getValue());
        theme = parseTheme(ActionWithData.UNCONVERT, themeComboBox.getValue());
        model = modelVersionComboBox.getValue();

        JSONObject obj = new JSONObject();
        obj.put("width", width);
        obj.put("height", height);
        obj.put("language", language);
        obj.put("theme", theme);
        obj.put("model", model);

        boolean isSaving = designer.savingConfig(obj);

        if (isSaving) showSuccessMessage();
        else showErrorMessage(ws().getString("label.errorSaving"));
    }

    private double safeParseDouble(String s, double fallback) {
        if (s == null) return fallback;
        String v = s.trim();
        if (v.isEmpty()) return fallback;
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Validates window size values. Returns true if ok.
     */
    private boolean applyWindowSize() {
        try {
            int w = Integer.parseInt(widthField.getText().trim());
            int h = Integer.parseInt(heightField.getText().trim());

            if (w < MIN_WIDTH || w > MAX_WIDTH) {
                showErrorMessage(ws().getString("label.errorWidth"));
                return false;
            }

            if (h < MIN_HEIGHT || h > MAX_HEIGHT) {
                showErrorMessage(ws().getString("label.errorHeight"));
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            showErrorMessage(ws().getString("label.errorFormatWindow"));
            return false;
        }
    }

    private void showSuccessMessage() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );

        alert.setTitle(ws().getString("label.success"));
        alert.setHeaderText(null);
        alert.setContentText(ws().getString("label.successDesc"));
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );

        alert.setTitle(ws().getString("label.error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}