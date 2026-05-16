package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import logic.json.JsonObject;
import view.base.BaseWindowView;

public final class WindowRequestView {
    private final BaseWindowView designer;
    private final JsonObject language;
    private final JsonObject theme;

    public WindowRequestView(BaseWindowView designer, JsonObject language, JsonObject theme) {
        this.designer = designer;
        this.language = language;
        this.theme = theme;
    }

    public void render(JsonObject entry, int index, Runnable backToHistory) {
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
        backButton.setOnAction(event -> backToHistory.run());
        VBox.setMargin(backButton, new Insets(20, 0, 0, 0));

        designer.formCard.getChildren().addAll(titleLabel, resultLabel, timeLabel, gridPane, backButton);
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

    private JsonObject wr() { return language.getJSONObject("WindowRequest"); }
    private String t(String key) { return theme != null ? theme.optString(key, "") : ""; }
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
