package ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DesignerApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * History window class for displaying evaluation history.
 */
public class WindowHistory extends Application {

    private static final String HISTORY_PATH = "resources/assets/history/history.json";

    public Stage primaryStage;
    public DesignerApp designer;

    public JSONObject language;
    public JSONObject themeObject;

    public WindowHistory() {}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        primaryStage = primary;
        designer = new DesignerApp(primaryStage, DesignerApp.UsingWindow.HISTORY);

        language = designer.languageObject;
        themeObject = designer.themeObject;

        createContent();
    }

    private JSONObject wh() {
        return language.getJSONObject("WindowHistory");
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

    public void createContent() {
        try {
            Path path = Paths.get(HISTORY_PATH);

            if (!Files.exists(path) || Files.size(path) == 0) {
                showEmptyHistory();
                return;
            }

            String content = Files.readString(path, StandardCharsets.UTF_8).trim();
            if (content.isEmpty()) {
                showEmptyHistory();
                return;
            }

            JSONArray historyArray;
            try {
                historyArray = new JSONArray(content);
            } catch (Exception parseErr) {
                // если файл повреждён/не массив — считаем что истории нет (поведение “не падать”)
                showEmptyHistory();
                return;
            }

            if (historyArray.length() == 0) {
                showEmptyHistory();
                return;
            }

            Label titleLabel = new Label(wh().getString("label.history"));
            titleLabel.setStyle(t("labelTitle") + " " + t("accentText") + " -fx-padding: 0 0 20 0;");

            Label countLabel = new Label(wh().getString("label.countHistory") + historyArray.length());
            countLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

            designer.formCard.getChildren().addAll(titleLabel, countLabel);

            for (int i = historyArray.length() - 1; i >= 0; i--) {
                JSONObject entry = historyArray.optJSONObject(i);
                if (entry == null) continue;

                Button historyButton = createHistoryButton(entry, i);
                designer.formCard.getChildren().add(historyButton);
            }

            Button clearHistoryButton = new Button(wh().getString("clearHistoryButton"));
            setPrimaryButtonHover(clearHistoryButton);
            clearHistoryButton.setOnAction(_ -> clearHistory());

            designer.formCard.getChildren().add(clearHistoryButton);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    private void clearHistory() {
        try {
            Path path = Paths.get(HISTORY_PATH);

            // Логика "очистить" та же, просто делаем надёжно:
            // 1) пробуем удалить, 2) если нельзя — перезаписываем пустым массивом.
            boolean cleared = false;

            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    cleared = true;
                } catch (Exception ignored) {
                    // например, файл занят/нет прав
                }
            } else {
                cleared = true;
            }

            if (!cleared) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "[]", StandardCharsets.UTF_8);
            }

            new WindowHistory().start(primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    private Button createHistoryButton(JSONObject entry, int index) {
        String timestamp = entry.optString("timestamp", wh().getString("label.unknownTime"));
        String result = entry.optString("result", wh().getString("label.unknownHistory"));

        String buttonText = timestamp + " — " + result;

        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefHeight(50);

        setPrimaryButtonHover(button);
        button.setOnAction(_ -> openDetailedView(entry, index));

        return button;
    }

    private void openDetailedView(JSONObject entry, int index) {
        WindowRequest detailWindow = new WindowRequest(entry, index);
        detailWindow.start(primaryStage);
    }

    private void showEmptyHistory() {
        Label emptyLabel = new Label(wh().getString("label.emptyHistory"));
        emptyLabel.setStyle(t("labelLarge") + " " + t("textMuted") + " -fx-padding: 40 0;");

        Label descLabel = new Label(wh().getString("label.desc"));
        descLabel.setStyle(t("textMuted") + " -fx-font-size: 14px;");

        Button backButton = new Button(wh().getString("label.backButton"));
        setPrimaryButtonHover(backButton);
        backButton.setOnAction(_ -> new WindowMain().start(primaryStage));

        VBox emptyBox = new VBox(20, emptyLabel, descLabel, backButton);
        emptyBox.setAlignment(Pos.CENTER);

        designer.formCard.getChildren().add(emptyBox);
    }

    private void showErrorMessage() {
        Label errorLabel = new Label(wh().getString("label.error"));
        errorLabel.setStyle(t("labelLarge") + " " + t("dangerText") + " -fx-padding: 40 0;");

        designer.formCard.getChildren().add(errorLabel);
    }
}