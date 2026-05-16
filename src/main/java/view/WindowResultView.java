package view;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.json.JsonObject;
import support.AppLogger;
import view.base.BaseWindowView;

public final class WindowResultView {
    public record ResultViewData(String imageFilePath, String resultText, String detailText, String statusColorKey) {}

    private final BaseWindowView designer;
    private final JsonObject language;
    private final JsonObject theme;

    public WindowResultView(BaseWindowView designer, JsonObject language, JsonObject theme) {
        this.designer = designer;
        this.language = language;
        this.theme = theme;
    }

    public void render(ResultViewData data, Runnable backToMain) {
        addImageWithFade(data.imageFilePath());

        Label statusLabel = new Label(data.resultText());
        String colorCss = t(data.statusColorKey());
        if (colorCss == null || colorCss.isBlank()) colorCss = t("textPrimary");
        statusLabel.setStyle(t("labelLarge") + " " + colorCss);
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(700);

        Label detailLabel = new Label(data.detailText());
        detailLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px; -fx-text-alignment: center;");
        detailLabel.setWrapText(true);
        detailLabel.setMaxWidth(700);
        detailLabel.setAlignment(Pos.CENTER);

        Button backButton = new Button(wr().getString("backButton"));
        setPrimaryButtonHover(backButton);
        backButton.setOnAction(event -> backToMain.run());

        designer.formCard.getChildren().addAll(statusLabel, detailLabel, backButton);
    }

    private void addImageWithFade(String filePath) {
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            ImageView imageView = new ImageView(new Image(is));
            imageView.setFitWidth(400);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            designer.formCard.getChildren().add(imageView);
        } catch (Exception e) {
            AppLogger.warning("Failed to load result image: " + filePath, e);
        }
    }

    private JsonObject wr() { return language.getJSONObject("WindowResult"); }
    private String t(String key) { return theme != null ? theme.optString(key, "") : ""; }
    private void setPrimaryButtonHover(Button button) {
        button.setStyle(t("primaryButtonOnMouseExited"));
        button.setOnMouseEntered(event -> button.setStyle(t("primaryButtonOnMouseEntered")));
        button.setOnMouseExited(event -> button.setStyle(t("primaryButtonOnMouseExited")));
    }
}
