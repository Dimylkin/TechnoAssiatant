package logic;

import javafx.stage.Stage;
import logic.base.BaseWindowLogic;
import logic.json.JsonObject;
import view.WindowRequestView;
import view.base.BaseWindowView;

public class WindowRequestLogic extends BaseWindowLogic {
    private final JsonObject entry;
    private final int index;

    public WindowRequestLogic(JsonObject entry, int index) {
        this.entry = entry;
        this.index = index;
    }

    @Override
    public void start(Stage primary) {
        initialize(primary, BaseWindowView.UsingWindow.HISTORY_REQUEST);
        new WindowRequestView(designer, languageObject, themeObject).render(entry, index, this::openHistory);
    }

    private void openHistory() {
        new WindowHistoryLogic().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
