package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DesignerApp;

import org.json.JSONObject;

/**
 * Detailed history view window for displaying full evaluation information.
 */
public class WindowRequest extends Application {

    public Stage primaryStage;
    public DesignerApp designer;

    public JSONObject entry;
    public int index;

    public JSONObject language;
    public JSONObject themeObject;

    public WindowRequest(JSONObject entry, int index) {
        this.entry = entry;
        this.index = index;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        primaryStage = primary;
        designer = new DesignerApp(primaryStage, DesignerApp.UsingWindow.HISTORY_REQUEST);

        language = designer.languageObject;
        themeObject = designer.themeObject;

        createContent();
    }

    private JSONObject wr() {
        return language.getJSONObject("WindowRequest");
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

    private void createContent() {
        Label titleLabel = new Label(wr().getString("label.title") + (index + 1));
        titleLabel.setStyle(t("labelTitle") + " " + t("accentText") + " -fx-padding: 0 0 10 0;");

        String result = entry.optString("result", wr().getString("label.unknownHistory"));

        Label resultLabel = new Label(wr().getString("label.result") + result);
        resultLabel.setStyle(t("labelLarge") + " " + getColorStyleByResult(result) + " -fx-padding: 0 0 20 0;");

        String timestamp = entry.optString("timestamp", wr().getString("label.unknownTime"));

        Label timeLabel = new Label(wr().getString("label.time") + timestamp);
        timeLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px; -fx-padding: 0 0 30 0;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(15);
        applyStyle(gridPane, "cardSmall");

        // Логика та же, просто без копипаста
        String[][] rows = new String[][] {
            {"ОС:", entry.optString("os", "—")},
            {"Актуальность:", entry.optString("new", "—")},
            {"Процессор:", entry.optString("model_cpu", "—")},
            {"Ядер:", entry.optString("core", "—")},
            {"Частота CPU (MHz):", entry.optString("frequency_ghz", "—")},
            {"Сокет:", entry.optString("socket", "—")},
            {"ОЗУ (GB):", entry.optString("ram_gb", "—")},
            {"Тип ОЗУ:", entry.optString("ram_type", "—")},
            {"Частота ОЗУ (MHz):", entry.optString("ram_ghz", "—")},
            {"Видеокарта:", entry.optString("model_gpu", "—")},
            {"VRAM (GB):", entry.optString("vram_gb", "—")},
            {"Накопитель (GB):", entry.optString("storage_gb", "—")},
            {"Материнская плата:", entry.optString("mother_board", "—")},
            {"Блок питания (W):", entry.optString("power_supply", "—")},
        };

        for (int i = 0; i < rows.length; i++) {
            addGridRow(gridPane, rows[i][0], rows[i][1], i);
        }

        Button backButton = new Button(wr().getString("label.backButton"));
        backButton.setMaxWidth(Double.MAX_VALUE);
        setPrimaryButtonHover(backButton);
        backButton.setOnAction(_ -> new WindowHistory().start(primaryStage));

        VBox.setMargin(backButton, new Insets(20, 0, 0, 0));

        designer.formCard.getChildren().addAll(
            titleLabel,
            resultLabel,
            timeLabel,
            gridPane,
            backButton
        );
    }

    private void addGridRow(GridPane grid, String label, String value, int row) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle(t("labelSmall") + " -fx-font-weight: bold;");

        Label lblValue = new Label(value);
        lblValue.setStyle(t("textPrimary") + " -fx-font-size: 13px;");

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    private String getColorStyleByResult(String result) {
        String fallback = t("textPrimary");
        String pick;

        if (result != null && result.contains("Хорошая")) pick = t("successText");
        else if (result != null && result.contains("Нормальная")) pick = t("warningText");
        else if (result != null && result.contains("Плохая")) pick = t("dangerText");
        else pick = t("accentText");

        return (pick == null || pick.isBlank()) ? fallback : pick;
    }
}