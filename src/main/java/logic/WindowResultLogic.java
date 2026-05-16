package logic;

import javafx.stage.Stage;
import logic.base.BaseWindowLogic;
import logic.json.JsonObject;
import support.AppPaths;
import view.WindowResultView;
import view.base.BaseWindowView;

public class WindowResultLogic extends BaseWindowLogic {
    private final String estimating;

    public WindowResultLogic(String estimating) {
        this.estimating = normalizeEstimating(estimating);
    }

    @Override
    public void start(Stage primary) {
        initialize(primary, BaseWindowView.UsingWindow.RESULT);
        new WindowResultView(designer, languageObject, themeObject).render(resolveResult(estimating), this::openMain);
    }

    private String normalizeEstimating(String value) {
        if (value == null) return "";
        return value.replace("\n", "").replace("\r", "").trim();
    }

    private JsonObject wr() {
        return languageObject.getJSONObject("WindowResult");
    }

    private WindowResultView.ResultViewData resolveResult(String value) {
        if ("Плохая".equals(value)) {
            return new WindowResultView.ResultViewData(
                AppPaths.pcImage("old_pc.png").toString(),
                wr().getString("badPCResultText"),
                wr().getString("badPCDetailText"),
                "dangerText"
            );
        }
        if ("Нормальная".equals(value)) {
            return new WindowResultView.ResultViewData(
                AppPaths.pcImage("normal_pc.png").toString(),
                wr().getString("normalPCResultText"),
                wr().getString("normalPCDetailText"),
                "warningText"
            );
        }
        if ("Хорошая".equals(value)) {
            return new WindowResultView.ResultViewData(
                AppPaths.pcImage("future_pc.png").toString(),
                wr().getString("goodPCResultText"),
                wr().getString("goodPCDetailText"),
                "successText"
            );
        }

        return new WindowResultView.ResultViewData(
            AppPaths.pcImage("stranger_pc.png").toString(),
            wr().getString("unknownPCResultText"),
            wr().getString("unknownPCDetailText"),
            "accentText"
        );
    }

    private void openMain() {
        new WindowMainLogic().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
