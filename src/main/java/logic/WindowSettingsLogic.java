package logic;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import logic.base.BaseWindowLogic;
import logic.json.JsonObject;
import view.WindowSettingsView;
import view.base.BaseWindowView;

public class WindowSettingsLogic extends BaseWindowLogic {
    private static final int MIN_WIDTH = 800;
    private static final int MAX_WIDTH = 3840;
    private static final int MIN_HEIGHT = 600;
    private static final int MAX_HEIGHT = 2160;

    public enum ActionWithData { CONVERT, UNCONVERT }

    private WindowSettingsView view;
    private Double width;
    private Double height;
    private String language;
    private String theme;
    private String model;

    public WindowSettingsLogic() {}

    @Override
    public void start(Stage primary) {
        initialize(primary, BaseWindowView.UsingWindow.SETTINGS);
        loadingConfig();
        view = new WindowSettingsView(
            designer,
            languageObject,
            themeObject,
            primaryStage.getWidth(),
            primaryStage.getHeight(),
            width,
            height,
            language,
            translatingTheme(theme),
            model,
            this::saveSettings
        );
    }

    private JsonObject ws() {
        return languageObject.getJSONObject("WindowSettings");
    }

    private void loadingConfig() {
        width = designer.width;
        height = designer.height;
        model = designer.model;
        language = parseLanguage(ActionWithData.CONVERT, designer.language);
        theme = designer.theme;
    }

    private boolean isUiRussian() {
        return "Русский".equalsIgnoreCase(language);
    }

    private String parseLanguage(ActionWithData type, String value) {
        if (type == ActionWithData.CONVERT) {
            if ("russian".equalsIgnoreCase(value)) return "Русский";
            if ("english".equalsIgnoreCase(value)) return "English";
            return "Русский";
        }

        if ("Русский".equalsIgnoreCase(value)) return "russian";
        if ("English".equalsIgnoreCase(value)) return "english";
        return "russian";
    }

    private String translatingTheme(String themeKey) {
        if (isUiRussian()) {
            if ("light".equalsIgnoreCase(themeKey)) return ws().getString("theme.light");
            if ("dark".equalsIgnoreCase(themeKey)) return ws().getString("theme.dark");
            return ws().getString("theme.light");
        }

        if ("light".equalsIgnoreCase(themeKey)) return ws().getString("theme.light");
        if ("dark".equalsIgnoreCase(themeKey)) return ws().getString("theme.dark");
        return ws().getString("theme.light");
    }

    private String parseTheme(ActionWithData type, String themeValue) {
        String lightUi = ws().getString("theme.light");
        String darkUi = ws().getString("theme.dark");

        if (type == ActionWithData.CONVERT) {
            if ("light".equalsIgnoreCase(themeValue)) return lightUi;
            if ("dark".equalsIgnoreCase(themeValue)) return darkUi;
            return lightUi;
        }

        if (lightUi.equalsIgnoreCase(themeValue)) return "light";
        if (darkUi.equalsIgnoreCase(themeValue)) return "dark";
        return "light";
    }

    private void saveSettings() {
        if (!applyWindowSize()) return;

        width = safeParseDouble(view.widthField.getText(), width);
        height = safeParseDouble(view.heightField.getText(), height);
        language = parseLanguage(ActionWithData.UNCONVERT, view.languageComboBox.getValue());
        theme = parseTheme(ActionWithData.UNCONVERT, view.themeComboBox.getValue());
        model = view.modelVersionComboBox.getValue();

        JsonObject obj = new JsonObject();
        obj.put("width", width);
        obj.put("height", height);
        obj.put("language", language);
        obj.put("theme", theme);
        obj.put("model", model);

        boolean isSaving = designer.savingConfig(obj);
        if (isSaving) showSuccessMessage();
        else showErrorMessage(ws().getString("label.errorSaving"));
    }

    private double safeParseDouble(String s, double fallback) {
        if (s == null) return fallback;
        String v = s.trim();
        if (v.isEmpty()) return fallback;
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private boolean applyWindowSize() {
        try {
            int w = Integer.parseInt(view.widthField.getText().trim());
            int h = Integer.parseInt(view.heightField.getText().trim());

            if (w < MIN_WIDTH || w > MAX_WIDTH) {
                showErrorMessage(ws().getString("label.errorWidth"));
                return false;
            }

            if (h < MIN_HEIGHT || h > MAX_HEIGHT) {
                showErrorMessage(ws().getString("label.errorHeight"));
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showErrorMessage(ws().getString("label.errorFormatWindow"));
            return false;
        }
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ws().getString("label.success"));
        alert.setHeaderText(null);
        alert.setContentText(ws().getString("label.successDesc"));
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ws().getString("label.error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
