package view;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import logic.json.JsonObject;
import support.AppLogger;
import support.AppPaths;
import view.base.BaseWindowView;

public final class WindowMainView {
    public final ComboBox<String> osComboBox;
    public final ComboBox<String> newComboBox;
    public final ComboBox<String> socketComboBox;
    public final ComboBox<String> ramTypeComboBox;

    public final TextField modelCpuField = new TextField();
    public final TextField coreField = new TextField();
    public final TextField frequencyField = new TextField();
    public final TextField ramGbField = new TextField();
    public final TextField ramGhzField = new TextField();
    public final TextField modelGpuField = new TextField();
    public final TextField vramGbField = new TextField();
    public final TextField storageGbField = new TextField();
    public final TextField motherBoardField = new TextField();
    public final TextField powerSupplyField = new TextField();
    public final TextField priceField = new TextField();

    public final Label messageLabel = new Label();
    private final Label aiVersionLabel = new Label();
    public final Map<Object, Label> fieldLabels = new HashMap<>();

    private final BaseWindowView designer;
    private final JsonObject language;
    private final JsonObject theme;

    public WindowMainView(
        BaseWindowView designer,
        JsonObject language,
        JsonObject theme,
        ObservableList<String> osList,
        ObservableList<String> newList,
        ObservableList<String> socketList,
        ObservableList<String> ramTypeList
    ) {
        this.designer = designer;
        this.language = language;
        this.theme = theme;
        osComboBox = new ComboBox<>(osList);
        newComboBox = new ComboBox<>(newList);
        socketComboBox = new ComboBox<>(socketList);
        ramTypeComboBox = new ComboBox<>(ramTypeList);
    }

    public void render(Runnable submitAction) {
        messageLabel.setStyle(t("dangerText") + " -fx-font-size: 20px;");

        GridPane gridPane = createFormGrid();

        Button submitButton = new Button(wm().getString("submitButton"));
        submitButton.setMaxWidth(Double.MAX_VALUE);
        setPrimaryButtonHover(submitButton);
        submitButton.setOnAction(event -> {
            event.consume();
            submitAction.run();
        });

        aiVersionLabel.setText(
            wm().getString("label.aiVersion")
            + designer.model
            + wm().getString("label.aiCanMisstake"));

        aiVersionLabel.setStyle(t("textSecondary") + " -fx-font-size: 11px;");
        aiVersionLabel.setAlignment(Pos.CENTER);
        aiVersionLabel.setMaxWidth(Double.MAX_VALUE);

        designer.formCard.getChildren().addAll(
            gridPane,
            messageLabel,
            submitButton,
            aiVersionLabel
        );
    }

    public Stage createLoadingDialog(Stage owner) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.setTitle(wm().getString("dialogDownload"));
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setOnCloseRequest(event -> event.consume());

        VBox dialogVBox = new VBox(15);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        applyStyle(dialogVBox, "card");

        Label statusLabel = new Label(wm().getString("label.status"));
        statusLabel.setId("statusLabel");
        statusLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px;");

        try (InputStream is = Files.newInputStream(AppPaths.systemImage("loading.png"))) {
            Image loadingImage = new Image(is);
            ImageView imageLoading = new ImageView(loadingImage);
            imageLoading.setFitWidth(175);
            imageLoading.setFitHeight(175);
            imageLoading.setPreserveRatio(true);

            RotateTransition rotate = new RotateTransition(Duration.seconds(4), imageLoading);
            rotate.setByAngle(-720);
            rotate.setCycleCount(RotateTransition.INDEFINITE);
            rotate.play();

            dialogVBox.getChildren().addAll(imageLoading, statusLabel);
        } catch (Exception e) {
            AppLogger.warning("Failed to load loading image", e);
            dialogVBox.getChildren().add(statusLabel);
        }

        Scene dialogScene = new Scene(dialogVBox, 500, 300);
        dialogStage.setScene(dialogScene);
        return dialogStage;
    }

    public Label getLoadingStatusLabel(Stage dialogStage) {
        return (Label) dialogStage.getScene().lookup("#statusLabel");
    }

    public void resetValidationStyles() {
        messageLabel.setText("");
        String base = t("labelSmall");
        for (Map.Entry<Object, Label> entry : fieldLabels.entrySet()) {
            entry.getValue().setStyle(base);
        }
    }

    public void showValidationError() {
        messageLabel.setText(wm().getString("messageLabel"));
    }

    public void fillTestData(String type) {
        switch (type) {
            case "Плохая" -> {
                osComboBox.setValue("Windows XP");
                newComboBox.setValue("Б/У");
                modelCpuField.setText("intel pentium 4");
                coreField.setText("1");
                frequencyField.setText("2400");
                ramGbField.setText("2");
                ramTypeComboBox.setValue("DDR2");
                ramGhzField.setText("800");
                modelGpuField.setText("nvidia geforce 6600");
                vramGbField.setText("256");
                storageGbField.setText("80");
                motherBoardField.setText("asus p5gd1");
                socketComboBox.setValue("LGA 775");
                powerSupplyField.setText("250");
                priceField.setText("50000");
            }
            case "Нормальная" -> {
                osComboBox.setValue("Windows 10");
                newComboBox.setValue("Б/У");
                modelCpuField.setText("intel core i5-10400");
                coreField.setText("6");
                frequencyField.setText("4300");
                ramGbField.setText("16");
                ramTypeComboBox.setValue("DDR4");
                ramGhzField.setText("3200");
                modelGpuField.setText("nvidia geforce gtx 1660");
                vramGbField.setText("6");
                storageGbField.setText("512");
                motherBoardField.setText("asus prime b460m-a");
                socketComboBox.setValue("LGA 1200");
                powerSupplyField.setText("450");
                priceField.setText("50000");
            }
            case "Хорошая" -> {
                osComboBox.setValue("Windows 11");
                newComboBox.setValue("Новый");
                modelCpuField.setText("amd ryzen 7 7800x3d");
                coreField.setText("8");
                frequencyField.setText("5000");
                ramGbField.setText("32");
                ramTypeComboBox.setValue("DDR5");
                ramGhzField.setText("6000");
                modelGpuField.setText("nvidia geforce rtx 4070");
                vramGbField.setText("12");
                storageGbField.setText("1024");
                motherBoardField.setText("asus rog strix b650");
                socketComboBox.setValue("AM5");
                powerSupplyField.setText("750");
                priceField.setText("50000");
            }
            default -> fillTestData("Нормальная");
        }
    }

    private GridPane createFormGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(15);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }

        osComboBox.setEditable(true);
        newComboBox.setEditable(true);
        ramTypeComboBox.setEditable(true);
        socketComboBox.setEditable(true);

        int row = 0;
        row = addSection(gridPane, wm().getString("label.genearal"), row);
        row = addFormRow(gridPane, wm().getString("label.os"), osComboBox, wm().getString("label.new"), newComboBox, row);

        row = addSection(gridPane, wm().getString("label.cpu"), row);
        row = addFormRow(gridPane, wm().getString("label.cpuModel"), modelCpuField, wm().getString("label.core"), coreField, row);
        row = addFormRow(gridPane, wm().getString("label.cpuGHZ"), frequencyField, null, null, row);

        row = addSection(gridPane, wm().getString("label.ram"), row);
        row = addFormRow(gridPane, wm().getString("label.ramGB"), ramGbField, wm().getString("label.ramType"), ramTypeComboBox, row);
        row = addFormRow(gridPane, wm().getString("label.ramGHZ"), ramGhzField, null, null, row);

        row = addSection(gridPane, wm().getString("label.gpu"), row);
        row = addFormRow(gridPane, wm().getString("label.gpuModel"), modelGpuField, wm().getString("label.gpuGB"), vramGbField, row);

        row = addSection(gridPane, wm().getString("label.powerAndStorage"), row);
        row = addFormRow(gridPane, wm().getString("label.storageGB"), storageGbField, wm().getString("label.motherboard"), motherBoardField, row);
        row = addFormRow(gridPane, wm().getString("label.socket"), socketComboBox, wm().getString("label.power"), powerSupplyField, row);

        // IMPORTANT:
        // For AI model version v1.0.1 price must not be displayed at all.
        if (!isModelVersion("v1.0.1")) {
            addFormRow(gridPane, wm().getString("label.price"), priceField, null, null, row);
        }

        styleFormFields();
        return gridPane;
    }

    private boolean isModelVersion(String version) {
        return version != null && version.equals(designer.model);
    }

    private int addSection(GridPane grid, String title, int row) {
        Label label = new Label(title);
        label.setStyle(t("labelMiddle") + " -fx-padding: 10 0 5 0;");
        label.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row, 4, 1);
        return row + 1;
    }

    private int addFormRow(GridPane grid, String label1, javafx.scene.Node field1, String label2, javafx.scene.Node field2, int row) {
        Label lbl1 = new Label(label1);
        applyStyle(lbl1, "labelSmall");
        grid.add(lbl1, 0, row);
        grid.add(field1, 1, row);
        fieldLabels.put(field1, lbl1);

        if (label2 != null && field2 != null) {
            Label lbl2 = new Label(label2);
            applyStyle(lbl2, "labelSmall");
            grid.add(lbl2, 2, row);
            grid.add(field2, 3, row);
            fieldLabels.put(field2, lbl2);
        }
        return row + 1;
    }

    private void styleFormFields() {
        TextField[] fields = {
            modelCpuField,
            coreField,
            frequencyField,
            ramGbField,
            ramGhzField,
            modelGpuField,
            vramGbField,
            storageGbField,
            motherBoardField,
            powerSupplyField,
            priceField
        };
        for (TextField field : fields) {
            applyStyle(field, "input");
            field.setPromptText(wm().getString("fieldPrompt"));
        }

        ComboBox<?>[] comboBoxes = {osComboBox, newComboBox, socketComboBox, ramTypeComboBox};
        for (ComboBox<?> combo : comboBoxes) {
            applyStyle(combo, "input");
            combo.setPromptText(wm().getString("comboBoxPrompt"));
        }
    }

    private JsonObject wm() { return language.getJSONObject("WindowMain"); }
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
