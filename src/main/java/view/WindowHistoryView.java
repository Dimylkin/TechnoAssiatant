package view;

import java.util.function.BiConsumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.json.JsonArray;
import logic.json.JsonObject;
import view.base.BaseWindowView;

public final class WindowHistoryView {
    private final BaseWindowView designer;
    private final JsonObject language;
    private final JsonObject theme;

    public WindowHistoryView(BaseWindowView designer, JsonObject language, JsonObject theme) {
        this.designer = designer;
        this.language = language;
        this.theme = theme;
    }

    public void renderHistory(JsonArray historyArray, BiConsumer<JsonObject, Integer> openDetails, Runnable clearHistory) {
        Label titleLabel = new Label(wh().getString("label.history"));
        titleLabel.setStyle(t("labelTitle") + " " + t("accentText") + " -fx-padding: 0 0 20 0;");

        Label countLabel = new Label(wh().getString("label.countHistory") + historyArray.length());
        countLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

        designer.formCard.getChildren().addAll(titleLabel, countLabel);

        for (int i = historyArray.length() - 1; i >= 0; i--) {
            JsonObject entry = historyArray.optJSONObject(i);
            if (entry == null) continue;
            designer.formCard.getChildren().add(createHistoryButton(entry, i, openDetails));
        }

        Button clearHistoryButton = new Button(wh().getString("clearHistoryButton"));
        setPrimaryButtonHover(clearHistoryButton);
        clearHistoryButton.setOnAction(event -> clearHistory.run());
        designer.formCard.getChildren().add(clearHistoryButton);
    }

    public void renderEmptyHistory(Runnable backToMain) {
        Label emptyLabel = new Label(wh().getString("label.emptyHistory"));
        emptyLabel.setStyle(t("labelLarge") + " " + t("textMuted") + " -fx-padding: 40 0;");

        Label descLabel = new Label(wh().getString("label.desc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 14px;");

        Button backButton = new Button(wh().getString("label.backButton"));
        setPrimaryButtonHover(backButton);
        backButton.setOnAction(event -> backToMain.run());

        VBox emptyBox = new VBox(20, emptyLabel, descLabel, backButton);
        emptyBox.setAlignment(Pos.CENTER);
        designer.formCard.getChildren().add(emptyBox);
    }

    public void renderError() {
        Label errorLabel = new Label(wh().getString("label.error"));
        errorLabel.setStyle(t("labelLarge") + " " + t("dangerText") + " -fx-padding: 40 0;");
        designer.formCard.getChildren().add(errorLabel);
    }

    private Button createHistoryButton(JsonObject entry, int index, BiConsumer<JsonObject, Integer> openDetails) {
        String timestamp = entry.optString("timestamp", wh().getString("label.unknownTime"));
        String result = entry.optString("result", wh().getString("label.unknownHistory"));

        Button button = new Button(timestamp + " — " + result);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefHeight(50);
        setPrimaryButtonHover(button);
        button.setOnAction(event -> openDetails.accept(entry, index));
        return button;
    }

    private JsonObject wh() { return language.getJSONObject("WindowHistory"); }
    private String t(String key) { return theme != null ? theme.optString(key, "") : ""; }
    private void setPrimaryButtonHover(Button button) {
        button.setStyle(t("primaryButtonOnMouseExited"));
        button.setOnMouseEntered(event -> button.setStyle(t("primaryButtonOnMouseEntered")));
        button.setOnMouseExited(event -> button.setStyle(t("primaryButtonOnMouseExited")));
    }
}
