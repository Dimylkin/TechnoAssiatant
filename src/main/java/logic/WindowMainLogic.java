package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.base.BaseWindowLogic;
import logic.json.JsonArray;
import logic.json.JsonObject;
import support.AppLogger;
import support.AppPaths;
import support.JsonFileStore;
import view.WindowMainView;
import view.base.BaseWindowView;

public class WindowMainLogic extends BaseWindowLogic {
    private String model;
    private WindowMainView view;

    private final ObservableList<String> osList = FXCollections.observableArrayList(
        "Windows Vista", "Windows XP", "Windows 7",
        "Windows 8", "Windows 10", "Windows 11",
        "Mac OS X 10", "macOS 11",
        "macOS 12", "macOS 13", "macOS 14",
        "macOS 15", "Ubuntu", "Debian",
        "Manjaro", "Fedora", "MX",
        "Mint", "Elementary", "SteamOS",
        "ZorinOS", "Arch", "CentOS"
    );

    private final ObservableList<String> socketList = FXCollections.observableArrayList(
        "AM5", "AM4", "AM3+", "AM3", "AM2+", "AM2", "AM1", "FM2+", "FM2", "FM1", "TR4", "sTRX4",
        "LGA 1851", "LGA 1700", "LGA 1200", "LGA 1151", "LGA 1150", "LGA 1155", "LGA 1156", "LGA 775",
        "LGA 2066", "LGA 2011-v3", "LGA 2011", "LGA 1366", "BGA 1090", "BGA 1168", "BGA 1234",
        "BGA 1356", "BGA 1440", "BGA 1515", "BGA 1744", "BGA 2270", "BGA 413", "FP8", "FP7", "FP6",
        "FP5", "FP4", "FT3", "FT3b", "FP2"
    );

    private final ObservableList<String> ramTypeList = FXCollections.observableArrayList(
        "DDR5", "DDR4", "DDR3", "DDR2", "DDR", "LPDDR5", "LPDDR5X", "LPDDR4", "LPDDR4X", "LPDDR3", "LPDDR2", "LPDDR"
    );

    @Override
    public void start(Stage primary) {
        initialize(primary, BaseWindowView.UsingWindow.HOME);
        model = designer.model;

        ObservableList<String> newList = FXCollections.observableArrayList(
            wm().getString("newList.new"),
            wm().getString("newList.no")
        );

        view = new WindowMainView(designer, languageObject, themeObject, osList, newList, socketList, ramTypeList);
        view.render(this::submit);
    }

    private JsonObject wm() {
        return languageObject.getJSONObject("WindowMain");
    }

    private void submit() {
        view.resetValidationStyles();
        if (!checkingField()) {
            view.showValidationError();
            return;
        }

        designer.dimApplication(true);
        windowLoading(primaryStage);
    }

    private void windowLoading(Stage owner) {
        Stage dialogStage = view.createLoadingDialog(owner);
        Label statusLabel = view.getLoadingStatusLabel(dialogStage);
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
                dialogStage.close();
                designer.dimApplication(false);
                new WindowResultLogic(getValue()).start(primaryStage);
            }

            @Override
            protected void failed() {
                dialogStage.close();
                designer.dimApplication(false);
                AppLogger.error("Failed to estimate PC configuration", getException());
                new WindowResultLogic("Сбой").start(primaryStage);
            }
        };

        statusLabel.textProperty().bind(task.messageProperty());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String getEstimating(String jsonData) {
        try {
            String scriptPath = AppPaths.PYTHON_PREDICT_SCRIPT.toAbsolutePath().normalize().toString();
            Process process = startPythonProcess(scriptPath);

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

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                AppLogger.error("Python script failed with exit code " + exitCode + ": " + output);
                return "Сбой";
            }

            return output.toString();
        } catch (Exception e) {
            AppLogger.error("Failed to run Python estimator", e);
            return "Сбой";
        }
    }

    private Process startPythonProcess(String scriptPath) throws IOException {
        Path pythonExecutable = getPythonExecutable();

        if (!Files.exists(pythonExecutable)) {
            throw new IOException(
                "Python virtual environment not found: " + pythonExecutable
                    + ". Run './app install' first."
            );
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
            pythonExecutable.toString(),
            scriptPath
        );

        processBuilder.redirectErrorStream(true);
        processBuilder.environment().put("PYTHONUTF8", "1");

        return processBuilder.start();
    }

    private Path getPythonExecutable() {
        Path pythonDir = AppPaths.PYTHON_PREDICT_SCRIPT
            .toAbsolutePath()
            .normalize()
            .getParent()
            .getParent();

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return pythonDir
                .resolve(".venv")
                .resolve("Scripts")
                .resolve("python.exe")
                .toAbsolutePath()
                .normalize();
        }

        return pythonDir
            .resolve(".venv")
            .resolve("bin")
            .resolve("python")
            .toAbsolutePath()
            .normalize();
    }

    public static Double tryParseDouble(String number) {
        try {
            if (number == null) return null;
            return Double.parseDouble(number.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean checkingData(Object object, InputValueType type, Label label) {
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

    private boolean validateTyped(String text, InputValueType type, Label label) {
        if (type == InputValueType.TEXT) {
            if (tryParseDouble(text) != null) {
                label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
                return false;
            }
            return true;
        }

        if (type == InputValueType.NUMBER) {
            if (tryParseDouble(text) == null) {
                label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
                return false;
            }
            return true;
        }

        label.setStyle(t("dangerText") + " -fx-font-size: 13px;");
        return false;
    }

    private boolean checkingField() {
        return checkingData(view.osComboBox, InputValueType.TEXT, view.fieldLabels.get(view.osComboBox))
            && checkingData(view.newComboBox, InputValueType.TEXT, view.fieldLabels.get(view.newComboBox))
            && checkingData(view.ramTypeComboBox, InputValueType.TEXT, view.fieldLabels.get(view.ramTypeComboBox))
            && checkingData(view.socketComboBox, InputValueType.TEXT, view.fieldLabels.get(view.socketComboBox))
            && checkingData(view.modelCpuField, InputValueType.TEXT, view.fieldLabels.get(view.modelCpuField))
            && checkingData(view.coreField, InputValueType.NUMBER, view.fieldLabels.get(view.coreField))
            && checkingData(view.frequencyField, InputValueType.NUMBER, view.fieldLabels.get(view.frequencyField))
            && checkingData(view.ramGbField, InputValueType.NUMBER, view.fieldLabels.get(view.ramGbField))
            && checkingData(view.ramGhzField, InputValueType.NUMBER, view.fieldLabels.get(view.ramGhzField))
            && checkingData(view.modelGpuField, InputValueType.TEXT, view.fieldLabels.get(view.modelGpuField))
            && checkingData(view.vramGbField, InputValueType.NUMBER, view.fieldLabels.get(view.vramGbField))
            && checkingData(view.storageGbField, InputValueType.NUMBER, view.fieldLabels.get(view.storageGbField))
            && checkingData(view.motherBoardField, InputValueType.TEXT, view.fieldLabels.get(view.motherBoardField))
            && checkingData(view.powerSupplyField, InputValueType.NUMBER, view.fieldLabels.get(view.powerSupplyField))
            && checkingPriceField();
    }

    private String parsingData() {
        String os = safeLower(view.osComboBox.getValue());
        String newValue = (view.newComboBox.getValue() == null) ? "" : view.newComboBox.getValue();
        String relevance = ("б/у".equalsIgnoreCase(newValue) || "used".equalsIgnoreCase(newValue)) ? "no" : "new";

        JsonObject obj = new JsonObject();
        obj.put("os", os);
        obj.put("new", relevance);
        obj.put("model_cpu", safeLower(view.modelCpuField.getText()));
        obj.put("core", tryParseDouble(view.coreField.getText()));
        obj.put("frequency_ghz", parseToFourDigit(tryParseDouble(view.frequencyField.getText())));
        obj.put("socket", safeLower(view.socketComboBox.getValue()));
        obj.put("ram_gb", tryParseDouble(view.ramGbField.getText()));
        obj.put("ram_type", safeLower(view.ramTypeComboBox.getValue()));
        obj.put("ram_ghz", parseToFourDigit(tryParseDouble(view.ramGhzField.getText())));
        obj.put("model_gpu", safeLower(view.modelGpuField.getText()));
        obj.put("vram_gb", tryParseDouble(view.vramGbField.getText()));
        obj.put("storage_gb", tryParseDouble(view.storageGbField.getText()));
        obj.put("mother_board", safeLower(view.motherBoardField.getText()));
        obj.put("power_supply", tryParseDouble(view.powerSupplyField.getText()));

        if ("v1.0.0".equals(model)) {
            obj.put("price", tryParseDouble(view.priceField.getText()));
        }

        obj.put("model", model);
        return obj.toString();
    }

    private boolean checkingPriceField() {
        if (!"v1.0.0".equals(model)) {
            return true;
        }

        return checkingData(
            view.priceField,
            InputValueType.NUMBER,
            view.fieldLabels.get(view.priceField)
        );
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
        JsonObject newEntry = new JsonObject(jsonData);
        newEntry.put("result", (result == null ? "" : result).replace("\n", "").replace("\r", ""));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        newEntry.put("timestamp", now.format(formatter));

        try {
            JsonArray historyArray;
            try {
                historyArray = JsonFileStore.readArray(AppPaths.HISTORY_FILE);
            } catch (Exception parseErr) {
                historyArray = new JsonArray();
            }

            historyArray.put(newEntry);
            JsonFileStore.writeArray(AppPaths.HISTORY_FILE, historyArray);
        } catch (Exception e) {
            AppLogger.error("Failed to append request history", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
