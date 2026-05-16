package logic.base;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.json.JsonObject;
import view.base.BaseWindowView;

public abstract class BaseWindowLogic extends Application {
    protected Stage primaryStage;
    protected BaseWindowView designer;
    protected JsonObject languageObject;
    protected JsonObject themeObject;

    protected final void initialize(Stage primary, BaseWindowView.UsingWindow window) {
        primaryStage = primary;
        designer = new BaseWindowView(primaryStage, window);
        languageObject = designer.languageObject;
        themeObject = designer.themeObject;
    }

    protected final String t(String key) {
        return themeObject != null ? themeObject.optString(key, "") : "";
    }

    protected final void applyStyle(Node node, String key) {
        String css = t(key);
        if (css != null && !css.isBlank()) {
            node.setStyle(css);
        }
    }

    protected final void setPrimaryButtonHover(Button button) {
        button.setStyle(t("primaryButtonOnMouseExited"));
        button.setOnMouseEntered(event -> button.setStyle(t("primaryButtonOnMouseEntered")));
        button.setOnMouseExited(event -> button.setStyle(t("primaryButtonOnMouseExited")));
    }
}
