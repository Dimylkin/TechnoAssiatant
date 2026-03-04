package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.DesignerApp;

/**
 * Main window class for PC configuration evaluation application.
 */
public class WindowMain extends Application {

    private static final String HISTORY_PATH = "resources/assets/history/history.json";
    private static final String PY_SCRIPT_REL = "../PythonAI/helpers/predict.py";
    private static final String LOADING_IMG_PATH = "resources/images/systems/loading.png";

    public DesignerApp designer;
    public String model;

    public VBox root;
    public Stage primaryStage;

    public ComboBox<String> osComboBox;
    public ComboBox<String> newComboBox;
    public ComboBox<String> socketComboBox;
    public ComboBox<String> ramTypeComboBox;

    public TextField modelCpuField = new TextField();
    public TextField coreField = new TextField();
    public TextField frequencyField = new TextField();
    public TextField ramGbField = new TextField();
    public TextField ramGhzField = new TextField();
    public TextField modelGpuField = new TextField();
    public TextField vramGbField = new TextField();
    public TextField storageGbField = new TextField();
    public TextField motherBoardField = new TextField();
    public TextField powerSupplyField = new TextField();

    public Label messageLabel = new Label();

    public JSONObject languageObject;
    public JSONObject themeObject;

    public ObservableList<String> osList = FXCollections.observableArrayList(
        "Windows Vista", "Windows XP", "Windows 7",
        "Windows 8", "Windows 10", "Windows 11",
        "Windows 11", "Mac OS X 10", "macOS 11",
        "macOS 12", "macOS 13", "macOS 14",
        "macOS 15", "Ubuntu", "Debian",
        "Manjaro", "Fedora", "MX",
        "Mint", "Elementary", "SteamOS",
        "ZorinOS", "Arch", "CentOS"
    );

    public ObservableList<String> newList = FXCollections.observableArrayList();

    public ObservableList<String> socketList = FXCollections.observableArrayList(
        "AM5", "AM4", "AM3+",
        "AM3", "AM2+", "AM2",
        "AM1", "FM2+", "FM2",
        "FM1", "TR4", "sTRX4",

        "LGA 1851", "LGA 1700", "LGA 1200",
        "LGA 1151", "LGA 1150", "LGA 1155",
        "LGA 1156", "LGA 775", "LGA 2066",
        "LGA 2011-v3", "LGA 2011", "LGA 1366",

        "BGA 1090", "BGA 1168", "BGA 1234",
        "BGA 1356", "BGA 1440", "BGA 1515",
        "BGA 1744", "BGA 2270", "BGA 413",

        "FP8", "FP7", "FP6",
        "FP5", "FP4", "FT3",
        "FT3b", "FP2"
    );

    public ObservableList<String> ramTypeList = FXCollections.observableArrayList(
        "DDR5", "DDR4", "DDR3",
        "DDR2", "DDR", "LPDDR5",
        "LPDDR5X", "LPDDR4", "LPDDR4X",
        "LPDDR3", "LPDDR2", "LPDDR"
    );

    private final HashMap<Object, Label> fieldLabels = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primary) {
        primaryStage = primary;
        designer = new DesignerApp(primaryStage, DesignerApp.UsingWindow.HOME);

        model = designer.model;

        languageObject = designer.languageObject;
        themeObject = designer.themeObject;

        newList = FXCollections.observableArrayList(
            wm().getString("newList.new"),
            wm().getString("newList.no")
        );

        root = designer.root;
        createContent();

        // как было
        fillTestData("Хорошая");
    }

    private JSONObject wm() {
        return languageObject.getJSONObject("WindowMain");
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

    private void fillTestData(String type) {
        switch (type) {
            case "Плохая":
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
                break;

            case "Нормальная":
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
                break;

            case "Хорошая":
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
                break;

            default:
                fillTestData("Нормальная");
                break;
        }
    }

    private void createContent() {
        messageLabel.setStyle(t("dangerText") + " -fx-font-size: 20px;");

        GridPane gridPane = createFormGrid();

        Button submitButton = new Button(wm().getString("submitButton"));
        submitButton.setMaxWidth(Double.MAX_VALUE);
        setPrimaryButtonHover(submitButton);

        submitButton.setOnAction(e -> {
            clearingLabel();
            if (checkingField()) {
                designer.dimApplication(true);
                e.consume();
                windowLoading(primaryStage);
            } else {
                messageLabel.setText(wm().getString("messageLabel"));
            }
        });

        designer.formCard.getChildren().addAll(gridPane, messageLabel, submitButton);
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

        osComboBox = new ComboBox<>(osList);
        osComboBox.setEditable(true);

        newComboBox = new ComboBox<>(newList);
        newComboBox.setEditable(true);

        ramTypeComboBox = new ComboBox<>(ramTypeList);
        ramTypeComboBox.setEditable(true);

        socketComboBox = new ComboBox<>(socketList);
        socketComboBox.setEditable(true);

        int row = 0;

        row = addSection(gridPane, wm().getString("label.genearal"), row);
        row = addFormRow(gridPane, wm().getString("label.os"), osComboBox,
                         wm().getString("label.new"), newComboBox, row);

        row = addSection(gridPane, wm().getString("label.cpu"), row);
        row = addFormRow(gridPane, wm().getString("label.cpuModel"), modelCpuField,
                         wm().getString("label.core"), coreField, row);
        row = addFormRow(gridPane, wm().getString("label.cpuGHZ"), frequencyField, null, null, row);

        row = addSection(gridPane, wm().getString("label.ram"), row);
        row = addFormRow(gridPane, wm().getString("label.ramGB"), ramGbField,
                         wm().getString("label.ramType"), ramTypeComboBox, row);
        row = addFormRow(gridPane, wm().getString("label.ramGHZ"), ramGhzField, null, null, row);

        row = addSection(gridPane, wm().getString("label.gpu"), row);
        row = addFormRow(gridPane, wm().getString("label.gpuModel"), modelGpuField,
                         wm().getString("label.gpuGB"), vramGbField, row);

        row = addSection(gridPane, wm().getString("label.powerAndStorage"), row);
        row = addFormRow(gridPane, wm().getString("label.storageGB"), storageGbField,
                         wm().getString("label.motherboard"), motherBoardField, row);
        row = addFormRow(gridPane, wm().getString("label.socket"), socketComboBox,
                         wm().getString("label.power"), powerSupplyField, row);

        styleFormFields();
        return gridPane;
    }

    private int addSection(GridPane grid, String title, int row) {
        Label label = new Label(title);
        label.setStyle(t("labelMiddle") + " -fx-padding: 10 0 5 0;");
        label.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row, 4, 1);
        return row + 1;
    }

    private int addFormRow(GridPane grid, String label1, javafx.scene.Node field1,
                           String label2, javafx.scene.Node field2, int row) {
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
            modelCpuField, coreField, frequencyField, ramGbField,
            ramGhzField, modelGpuField, vramGbField, storageGbField,
            motherBoardField, powerSupplyField
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

    private void windowLoading(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle(wm().getString("dialogDownload"));
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setOnCloseRequest(e -> e.consume());

        VBox dialogVBox = new VBox(15);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        applyStyle(dialogVBox, "card");

        Label statusLabel = new Label(wm().getString("label.status"));
        statusLabel.setStyle(t("textSecondary") + " -fx-font-size: 14px;");

        try (InputStream is = Files.newInputStream(Paths.get(LOADING_IMG_PATH))) {
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
            e.printStackTrace();
            dialogVBox.getChildren().add(statusLabel);
        }

        Scene dialogScene = new Scene(dialogVBox, 500, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.show();

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage(wm().getString("label.sandingData"));
                String jsonData = parsingData();

                String result = getEstimating(jsonData);
                creatingHistory(jsonData, result);

                updateMessage(wm().getString("label.rendering"));
                return result;
            }

            @Override
            protected void succeeded() {
                String result = getValue();
                dialogStage.close();
                designer.dimApplication(false);
                new WindowResult(result).start(primaryStage);
            }

            @Override
            protected void failed() {
                dialogStage.close();
                designer.dimApplication(false);

                getException().printStackTrace();
                new WindowResult("Сбой").start(primaryStage);
            }
        };

        statusLabel.textProperty().bind(task.messageProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String getEstimating(String jsonData) {
        try {
            String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            File classDir = new File(classPath).getParentFile();

            File pythonScript = new File(classDir, PY_SCRIPT_REL);
            String scriptPath = pythonScript.getCanonicalPath();

            ProcessBuilder pb = new ProcessBuilder("py", "-3", scriptPath);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (var os = process.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            process.waitFor();
            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Сбой";
        }
    }

    public static Double tryParseDouble(String number) {
        try {
            if (number == null) return null;
            return Double.parseDouble(number.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void clearingLabel() {
        messageLabel.setText("");

        String base = t("labelSmall");
        for (Map.Entry<Object, Label> entry : fieldLabels.entrySet()) {
            entry.getValue().setStyle(base);
        }
    }

    // ЛОГИКА та же, просто исправлен сравнение строк (== -> equals)
    private boolean checkingData(Object object, String type, Label label) {
        if (label == null) return false;

        if (object instanceof TextField tf) {
            String text = tf.getText();
            if (text == null || text.trim().isEmpty()) {
                label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
                return false;
            }
            return validateTyped(text, type, label);
        }

        if (object instanceof ComboBox<?> cb) {
            Object val = cb.getValue();
            String text = (val == null) ? null : val.toString();
            if (text == null || text.trim().isEmpty()) {
                label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
                return false;
            }
            return validateTyped(text, type, label);
        }

        return false;
    }

    private boolean validateTyped(String text, String type, Label label) {
        // "String" означает: НЕ число
        if ("String".equals(type)) {
            if (tryParseDouble(text) != null) {
                label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
                return false;
            }
            return true;
        }

        // "Double" означает: число
        if ("Double".equals(type)) {
            if (tryParseDouble(text) == null) {
                label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
                return false;
            }
            return true;
        }

        // неизвестный тип — считаем невалидным
        label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
        return false;
    }

    private boolean checkingField() {
        // Логика та же: все поля должны быть валидны
        return checkingData(osComboBox, "String", fieldLabels.get(osComboBox))
            && checkingData(newComboBox, "String", fieldLabels.get(newComboBox))
            && checkingData(ramTypeComboBox, "String", fieldLabels.get(ramTypeComboBox))
            && checkingData(socketComboBox, "String", fieldLabels.get(socketComboBox))

            && checkingData(modelCpuField, "String", fieldLabels.get(modelCpuField))
            && checkingData(coreField, "Double", fieldLabels.get(coreField))
            && checkingData(frequencyField, "Double", fieldLabels.get(frequencyField))
            && checkingData(ramGbField, "Double", fieldLabels.get(ramGbField))
            && checkingData(ramGhzField, "Double", fieldLabels.get(ramGhzField))
            && checkingData(modelGpuField, "String", fieldLabels.get(modelGpuField))
            && checkingData(vramGbField, "Double", fieldLabels.get(vramGbField))
            && checkingData(storageGbField, "Double", fieldLabels.get(storageGbField))
            && checkingData(motherBoardField, "String", fieldLabels.get(motherBoardField))
            && checkingData(powerSupplyField, "Double", fieldLabels.get(powerSupplyField));
    }

    private String parsingData() {
        String os = safeLower(osComboBox.getValue());

        String newValue = (newComboBox.getValue() == null) ? "" : newComboBox.getValue();
        String relevance = ("б/у".equalsIgnoreCase(newValue) || "used".equalsIgnoreCase(newValue)) ? "no" : "new";

        JSONObject obj = new JSONObject();
        obj.put("os", os);
        obj.put("new", relevance);
        obj.put("model_cpu", safeLower(modelCpuField.getText()));

        obj.put("core", tryParseDouble(coreField.getText()));
        obj.put("frequency_ghz", parseToFourDigit(tryParseDouble(frequencyField.getText())));
        obj.put("socket", safeLower(socketComboBox.getValue()));

        obj.put("ram_gb", tryParseDouble(ramGbField.getText()));
        obj.put("ram_type", safeLower(ramTypeComboBox.getValue()));
        obj.put("ram_ghz", parseToFourDigit(tryParseDouble(ramGhzField.getText())));

        obj.put("model_gpu", safeLower(modelGpuField.getText()));
        obj.put("vram_gb", tryParseDouble(vramGbField.getText()));
        obj.put("storage_gb", tryParseDouble(storageGbField.getText()));

        obj.put("mother_board", safeLower(motherBoardField.getText()));
        obj.put("power_supply", tryParseDouble(powerSupplyField.getText()));

        obj.put("model", model);

        return obj.toString();
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    public static Double parseToFourDigit(Double value) {
        if (value == null) return null;
        if (value >= 100) return value;
        return value * 1000;
    }

    public static void creatingHistory(String jsonData, String result) {
        JSONObject newEntry = new JSONObject(jsonData);
        newEntry.put("result", (result == null ? "" : result).replace("\n", "").replace("\r", ""));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        newEntry.put("timestamp", now.format(formatter));

        try {
            Path path = Paths.get(HISTORY_PATH);
            Files.createDirectories(path.getParent());

            JSONArray historyArray;

            if (Files.exists(path) && Files.size(path) > 0) {
                String content = Files.readString(path, StandardCharsets.UTF_8).trim();
                try {
                    historyArray = content.isEmpty() ? new JSONArray() : new JSONArray(content);
                } catch (Exception parseErr) {
                    historyArray = new JSONArray(); // файл повреждён — начинаем заново
                }
            } else {
                historyArray = new JSONArray();
            }

            historyArray.put(newEntry);

            try (FileWriter writer = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
                writer.write(historyArray.toString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}