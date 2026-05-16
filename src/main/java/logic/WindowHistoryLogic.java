package logic;

import java.nio.file.Files;

import javafx.stage.Stage;
import logic.base.BaseWindowLogic;
import logic.json.JsonArray;
import logic.json.JsonObject;
import support.AppLogger;
import support.AppPaths;
import support.JsonFileStore;
import view.WindowHistoryView;
import view.base.BaseWindowView;

public class WindowHistoryLogic extends BaseWindowLogic {
    private WindowHistoryView view;

    public WindowHistoryLogic() {}

    @Override
    public void start(Stage primary) {
        initialize(primary, BaseWindowView.UsingWindow.HISTORY);
        view = new WindowHistoryView(designer, languageObject, themeObject);
        loadHistory();
    }

    private void loadHistory() {
        try {
            if (!Files.exists(AppPaths.HISTORY_FILE) || Files.size(AppPaths.HISTORY_FILE) == 0) {
                view.renderEmptyHistory(this::openMain);
                return;
            }

            JsonArray historyArray;
            try {
                historyArray = JsonFileStore.readArray(AppPaths.HISTORY_FILE);
            } catch (Exception parseError) {
                view.renderEmptyHistory(this::openMain);
                return;
            }

            if (historyArray.length() == 0) {
                view.renderEmptyHistory(this::openMain);
                return;
            }

            view.renderHistory(historyArray, this::openDetailedView, this::clearHistory);
        } catch (Exception e) {
            AppLogger.error("Failed to load request history", e);
            view.renderError();
        }
    }

    private void clearHistory() {
        try {
            if (Files.exists(AppPaths.HISTORY_FILE)) {
                Files.delete(AppPaths.HISTORY_FILE);
            }

            new WindowHistoryLogic().start(primaryStage);
        } catch (Exception e) {
            AppLogger.error("Failed to clear request history", e);
            view.renderError();
        }
    }

    private void openDetailedView(JsonObject entry, int index) {
        new WindowRequestLogic(entry, index).start(primaryStage);
    }

    private void openMain() {
        new WindowMainLogic().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
