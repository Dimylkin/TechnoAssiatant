package ui;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DesignerApp;

/**
 * Result window class for displaying PC evaluation results.
 * Shows the evaluation outcome with corresponding image, description, and navigation options.
 */
public class WindowResult extends Application {

    public Stage primaryStage;
    public DesignerApp designer;

    public JSONObject language;
    public JSONObject themeObject;

    public String estimating;

    public WindowResult(String estimating) {
        this.estimating = normalizeEstimating(estimating);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        primaryStage = primary;
        designer = new DesignerApp(primaryStage, DesignerApp.UsingWindow.RESULT);

        language = designer.languageObject;
        themeObject = designer.themeObject;

        createContent();
    }

    private String normalizeEstimating(String s) {
        if (s == null) return "";
        return s.replace("\n", "").replace("\r", "").trim();
    }

    private JSONObject wr() {
        return language.getJSONObject("WindowResult");
    }

    private String t(String key) {
        return themeObject != null ? themeObject.optString(key, "") : "";
    }

    private void setPrimaryButtonHover(Button btn) {
        btn.setStyle(t("primaryButtonOnMouseExited"));
        btn.setOnMouseEntered(_ -> btn.setStyle(t("primaryButtonOnMouseEntered")));
        btn.setOnMouseExited(_ -> btn.setStyle(t("primaryButtonOnMouseExited")));
    }

    private static final class ResultViewData {
        final String imageFilePath;   // filesystem path (без "file:")
        final String resultText;
        final String detailText;
        final String statusColorKey;

        ResultViewData(String imageFilePath, String resultText, String detailText, String statusColorKey) {
            this.imageFilePath = imageFilePath;
            this.resultText = resultText;
            this.detailText = detailText;
            this.statusColorKey = statusColorKey;
        }
    }

    private ResultViewData resolveResult(String estimating) {
        // Логика та же: Плохая / Нормальная / Хорошая / иначе Unknown
        if ("Плохая".equals(estimating)) {
            return new ResultViewData(
                "resources/images/pc/old_pc.png",
                wr().getString("badPCResultText"),
                wr().getString("badPCDetailText"),
                "dangerText"
            );
        }
        if ("Нормальная".equals(estimating)) {
            return new ResultViewData(
                "resources/images/pc/normal_pc.png",
                wr().getString("normalPCResultText"),
                wr().getString("normalPCDetailText"),
                "warningText"
            );
        }
        if ("Хорошая".equals(estimating)) {
            return new ResultViewData(
                "resources/images/pc/future_pc.png",
                wr().getString("goodPCResultText"),
                wr().getString("goodPCDetailText"),
                "successText"
            );
        }

        return new ResultViewData(
            "resources/images/pc/stranger_pc.png",
            wr().getString("unknownPCResultText"),
            wr().getString("unknownPCDetailText"),
            "accentText"
        );
    }

    private void addImageWithFade(String filePath) {
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            Image pcImage = new Image(is);
            ImageView imageView = new ImageView(pcImage);

            imageView.setFitWidth(400);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            designer.formCard.getChildren().add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createContent() {
        ResultViewData data = resolveResult(estimating);

        addImageWithFade(data.imageFilePath);

        Label statusLabel = new Label(data.resultText);

        String colorCss = t(data.statusColorKey);
        if (colorCss == null || colorCss.isBlank()) {
            colorCss = t("textPrimary");
        }

        statusLabel.setStyle(t("labelLarge") + " " + colorCss);
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(700);

        Label detailLabel = new Label(data.detailText);
        detailLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px; -fx-text-alignment: center;");
        detailLabel.setWrapText(true);
        detailLabel.setMaxWidth(700);
        detailLabel.setAlignment(Pos.CENTER);

        Button backButton = new Button(wr().getString("backButton"));
        setPrimaryButtonHover(backButton);
        backButton.setOnAction(_ -> new WindowMain().start(primaryStage));

        designer.formCard.getChildren().addAll(statusLabel, detailLabel, backButton);
    }
}