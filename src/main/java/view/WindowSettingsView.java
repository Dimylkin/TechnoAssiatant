package view;

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
import logic.json.JsonObject;
import view.base.BaseWindowView;

public final class WindowSettingsView {
    public final TextField widthField;
    public final TextField heightField;
    public final ComboBox<String> themeComboBox;
    public final ComboBox<String> languageComboBox;
    public final ComboBox<String> modelVersionComboBox;

    private final BaseWindowView designer;
    private final JsonObject languageObject;
    private final JsonObject themeObject;

    public WindowSettingsView(
        BaseWindowView designer,
        JsonObject languageObject,
        JsonObject themeObject,
        double currentStageWidth,
        double currentStageHeight,
        Double configWidth,
        Double configHeight,
        String uiLanguage,
        String uiTheme,
        String model,
        Runnable saveAction
    ) {
        this.designer = designer;
        this.languageObject = languageObject;
        this.themeObject = themeObject;

        widthField = new TextField(String.valueOf((int) currentStageWidth));
        widthField.setPromptText(configWidth.toString());
        widthField.setStyle(t("input"));
        widthField.setPrefWidth(100);

        heightField = new TextField(String.valueOf((int) currentStageHeight));
        heightField.setPromptText(configHeight.toString());
        heightField.setStyle(t("input"));
        heightField.setPrefWidth(100);

        ObservableList<String> themes = FXCollections.observableArrayList(ws().getString("theme.light"), ws().getString("theme.dark"));
        themeComboBox = new ComboBox<>(themes);
        themeComboBox.setValue(uiTheme);
        themeComboBox.setMaxWidth(Double.MAX_VALUE);
        themeComboBox.setStyle(t("input"));

        ObservableList<String> languages = FXCollections.observableArrayList("Русский", "English");
        languageComboBox = new ComboBox<>(languages);
        languageComboBox.setValue(uiLanguage);
        languageComboBox.setMaxWidth(Double.MAX_VALUE);
        languageComboBox.setStyle(t("input"));

        ObservableList<String> modelVersions = FXCollections.observableArrayList("v1.0.0", "v1.0.1");
        modelVersionComboBox = new ComboBox<>(modelVersions);
        modelVersionComboBox.setValue(model);
        modelVersionComboBox.setMaxWidth(Double.MAX_VALUE);
        modelVersionComboBox.setStyle(t("input"));

        render(saveAction);
    }

    private void render(Runnable saveAction) {
        Label titleLabel = new Label(ws().getString("label.title"));
        titleLabel.setStyle(t("labelTitle") + " " + t("accentText") + " -fx-padding: 0 0 30 0;");

        Button saveButton = new Button(ws().getString("saveButton"));
        saveButton.setMaxWidth(Double.MAX_VALUE);
        setPrimaryButtonHover(saveButton);
        saveButton.setOnAction(event -> saveAction.run());
        VBox.setMargin(saveButton, new Insets(30, 0, 0, 0));

        designer.formCard.getChildren().addAll(
            titleLabel,
            createWindowSizeBlock(),
            createThemeBlock(),
            createLanguageBlock(),
            createModelVersionBlock(),
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

        Label heightLabel = new Label(ws().getString("label.windowHeight"));
        applyStyle(heightLabel, "labelSmall");
        heightLabel.setPrefWidth(80);

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

        Label descLabel = new Label(ws().getString("label.modelDesc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 12px;");

        block.getChildren().addAll(label, modelVersionComboBox, descLabel);
        VBox.setMargin(block, new Insets(0, 0, 20, 0));
        return block;
    }

    private JsonObject ws() { return languageObject.getJSONObject("WindowSettings"); }
    private String t(String key) { return themeObject != null ? themeObject.optString(key, "") : ""; }
    private void applyStyle(javafx.scene.Node node, String key) {
        String css = t(key);
        if (css != null && !css.isBlank()) node.setStyle(css);
    }
    private void setPrimaryButtonHover(Button button) {
        button.setStyle(t("primaryButtonOnMouseExited"));
        button.setOnMouseEntered(event -> button.setStyle(t("primaryButtonOnMouseEntered")));
        button.setOnMouseExited(event -> button.setStyle(t("primaryButtonOnMouseExited")));
    }
}
