package src;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Result window class for displaying PC evaluation results.
 * Shows the evaluation outcome with corresponding image, description, and navigation options.
 */
public class WindowResult extends Application {
	
	/**
	 * Primary stage reference for the result window.
	 */
	public Stage primaryStage;
		
	/**
	 * Designer application instance providing UI styling and layout management.
	 */
	public DesignerApp designer;
	
	/**
	 * Evaluation result string indicating PC performance category.
	 * Expected values: "Плохая" (poor), "Нормальная" (normal), "Хорошая" (good), or unknown.
	 */
	public String estimating;
	
	/**
	 * Constructs a WindowResult instance with the evaluation result.
	 * Cleans the result string by removing newline and carriage return characters.
	 *
	 * @param estimating The evaluation result from the AI model
	 */
	public WindowResult(String estimating) {
		this.estimating = estimating.replace("\n", "").replace("\r", "");
	}
	
	/**
	 * Main entry point for the JavaFX application.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and displays the result window with evaluation outcome.
     * Creates the designer interface and populates content based on evaluation result.
     *
     * @param primary The primary stage for the result window
     */
    @Override
    public void start(Stage primary) {
    	primaryStage = primary;
    	designer = new DesignerApp(primaryStage, "Результат оценки");
    	createContent();
    }
    
    /**
     * Creates and configures all content for the result window.
     * Displays appropriate image, status text, and details based on evaluation result.
     * Supports four evaluation categories: poor, normal, good, and unknown.
     */
    public void createContent() {        
        String imagePath = "";
        String resultText = "";
        String detailText = "";
        String statusStyle = "";
        
        if (estimating.equals("Плохая")) {
            imagePath = "file:resources/images/pc/old_pc.png";
            resultText = "Ваш компьютер считается устаревшим";
            detailText = "Такой результат связан с возможной неактуальностью комплектующих, установленных в вашем ПК. Рекомендуется обновление основных компонентов для улучшения производительности.";
            statusStyle = "-fx-text-fill: #e74c3c; -fx-font-size: 20px; -fx-font-weight: bold;";
        } else if (estimating.equals("Нормальная")) {
            imagePath = "file:resources/images/pc/normal_pc.png";
            resultText = "Ваш компьютер имеет среднюю производительность";
            detailText = "Ваша конфигурация подходит для повседневных задач и большинства современных приложений. Для улучшения производительности можно рассмотреть обновление отдельных компонентов.";
            statusStyle = "-fx-text-fill: #f39c12; -fx-font-size: 20px; -fx-font-weight: bold;";
        } else if (estimating.equals("Хорошая")) {
            imagePath = "file:resources/images/pc/future_pc.png";
            resultText = "Ваш компьютер имеет отличную производительность";
            detailText = "Отличная конфигурация! Ваш компьютер справится с требовательными задачами, современными играми и профессиональными приложениями.";
            statusStyle = "-fx-text-fill: #27ae60; -fx-font-size: 20px; -fx-font-weight: bold;";
        } else {
            imagePath = "file:resources/images/pc/stranger_pc.png";
            resultText = "Неизвестно";
            detailText = "Неизвестный результат оценки. Попробуйте еще раз!";
            statusStyle = "-fx-text-fill: #667eea; -fx-font-size: 20px; -fx-font-weight: bold;";
        }
        
        try {
            Image pcImage = new Image(imagePath);
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
        
        Label statusLabel = new Label(resultText);
        statusLabel.setStyle(statusStyle);
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(700);  
        
        Label detailLabel = new Label(detailText);
        detailLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #555;" +
            "-fx-text-alignment: center;"
        );
        detailLabel.setWrapText(true);
        detailLabel.setMaxWidth(700);
        detailLabel.setAlignment(Pos.CENTER);
        
        Button backButton = new Button("Вернуться к форме");
        backButton.setStyle(designer.getStyleButton(true));
        
        backButton.setOnMouseEntered(_ -> {
        	backButton.setStyle(designer.getStyleButton(true));
        });
        
        backButton.setOnMouseExited(_ -> {
        	backButton.setStyle(designer.getStyleButton(false));
        });
        
        backButton.setOnAction(_ -> {
        	WindowMain windowMain = new WindowMain();
        	windowMain.start(primaryStage);
        });
        designer.formCard.getChildren().addAll(statusLabel, detailLabel, backButton);
    }
}
