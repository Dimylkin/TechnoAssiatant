import javafx.application.Application;
import logic.WindowMainLogic;
import support.AppLogger;

public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
            AppLogger.error("Uncaught exception in thread: " + thread.getName(), error)
        );

        Application.launch(WindowMainLogic.class, args);
    }
}