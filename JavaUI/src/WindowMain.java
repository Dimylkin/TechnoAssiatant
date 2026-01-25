package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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

/**
 * Main window class for PC configuration evaluation application.
 * Provides a comprehensive form interface for entering computer specifications
 * and evaluating them using an AI model.
 */
public class WindowMain extends Application {
	
	/**
	 * Designer application instance providing UI styling and layout management.
	 */
	public DesignerApp designer;
	
	/**
	 * Root container for the main window layout.
	 */
	public VBox root;
	
	/**
	 * Primary stage reference for the main application window.
	 */
	public Stage primaryStage;

	/**
	 * ComboBox for selecting the operating system.
	 */
	public ComboBox<String> osComboBox;
	
	/**
	 * ComboBox for selecting whether the PC is new or used.
	 */
	public ComboBox<String> newComboBox;
	
	/**
	 * ComboBox for selecting the CPU socket type.
	 */
	public ComboBox<String> socketComboBox;
	
	/**
	 * ComboBox for selecting the RAM type (DDR generation).
	 */
	public ComboBox<String> ramTypeComboBox;

	/**
	 * Text field for entering the CPU model name.
	 */
	public TextField modelCpuField = new TextField();
	
	/**
	 * Text field for entering the number of CPU cores.
	 */
	public TextField coreField = new TextField();
	
	/**
	 * Text field for entering the CPU frequency in MHz.
	 */
	public TextField frequencyField = new TextField();
	
	/**
	 * Text field for entering the RAM capacity in GB.
	 */
	public TextField ramGbField = new TextField();
	
	/**
	 * Text field for entering the RAM frequency in MHz.
	 */
	public TextField ramGhzField = new TextField();
	
	/**
	 * Text field for entering the GPU model name.
	 */
	public TextField modelGpuField = new TextField();
	
	/**
	 * Text field for entering the VRAM capacity in GB.
	 */
	public TextField vramGbField = new TextField();
	
	/**
	 * Text field for entering the storage capacity in GB.
	 */
	public TextField storageGbField = new TextField();
	
	/**
	 * Text field for entering the motherboard model name.
	 */
	public TextField motherBoardField = new TextField();
	
	/**
	 * Text field for entering the power supply wattage.
	 */
	public TextField powerSupplyField = new TextField();
	
	/**
	 * Label for displaying validation and error messages to the user.
	 */
	public Label messageLabel = new Label();
    
	/**
	 * Observable list containing available operating system options.
	 */
	public ObservableList<String> osList = FXCollections.observableArrayList("Windows Vista", "Windows XP", "Windows 7",
    																		 "Windows 8", "Windows 10", "Windows 11",
    																		  "Windows 11", "Mac OS X 10", "macOS 11", 
    																		  "macOS 12", "macOS 13", "macOS 14",
    																		  "macOS 15", "Ubuntu", "Debian", 
    																		  "Manjaro", "Fedora", "MX",
    																		  "Mint", "Elementary", "SteamOS", 
    																		  "ZorinOS", "Arch", "CentOS");
    
	/**
	 * Observable list containing PC condition options (new or used).
	 */
	public ObservableList<String> newList = FXCollections.observableArrayList("Новый", "Б/У");
    
	/**
	 * Observable list containing available CPU socket types for AMD, Intel, and mobile platforms.
	 */
	public ObservableList<String> socketList = FXCollections.observableArrayList("AM5", "AM4", "AM3+",
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
																			     "FT3b", "FP2");

    
	/**
	 * Observable list containing available RAM types including DDR and LPDDR generations.
	 */
	public ObservableList<String> ramTypeList = FXCollections.observableArrayList("DDR5", "DDR4", "DDR3", 
																				  "DDR2", "DDR", "LPDDR5", 
																				  "LPDDR5X", "LPDDR4", "LPDDR4X", 
																				  "LPDDR3", "LPDDR2", "LPDDR");

	/**
	 * HashMap mapping form fields to their corresponding labels for validation highlighting.
	 */
	private HashMap<Object, Label> fieldLabels = new HashMap<>();
	
	/**
	 * Main entry point for the JavaFX application.
	 *
	 * @param args Command line arguments
	 */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and displays the main application window.
     * Creates the designer interface, content form, and fills test data.
     *
     * @param primary The primary stage for the application
     */
    @Override
    public void start(Stage primary) {
    	primaryStage = primary;
    	designer = new DesignerApp(primaryStage, "Techno Assistant");
    	root = designer.root;
    	createContent();
    	fillTestData("Хорошая");
    }
    
    /**
     * Fills form fields with predefined test data based on PC quality tier.
     * Supports three tiers: "Плохая" (poor), "Нормальная" (normal), "Хорошая" (good).
     *
     * @param type The quality tier of test data to fill ("Плохая", "Нормальная", or "Хорошая")
     */
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

    /**
     * Creates and configures all form content including the grid layout,
     * input fields, and submit button with validation handling.
     */
    private void createContent() {
    	messageLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
    	
        GridPane gridPane = createFormGrid();
        
        Button submitButton = new Button("Оценить ПК");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setStyle(designer.getStyleButton(false));
        
        submitButton.setOnMouseEntered(_ -> {
        	submitButton.setStyle(designer.getStyleButton(true));
        });
        
        submitButton.setOnMouseExited(_ -> {
        	submitButton.setStyle(designer.getStyleButton(false));
        });
        
        submitButton.setOnAction(e -> {
        	clearingLabel();
        	if (checkingField() == true) {
        		designer.dimApplication(true);
        		e.consume();
        		windowLoading(primaryStage);
        	}
        	else {
        		messageLabel.setText("Пожалуйста, заполните корректно все поля!");
        	}
        	
        });
        
        designer.formCard.getChildren().addAll(gridPane, messageLabel, submitButton);
    }
    
    /**
     * Creates and configures the form grid layout with all input fields organized by sections.
     * Includes sections for general parameters, CPU, RAM, GPU, and system components.
     *
     * @return Configured GridPane containing all form fields
     */
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
        
        row = addSection(gridPane, "Общие параметры", row);
        row = addFormRow(gridPane, "Операционная система:", osComboBox, 
                         "Актуальность:", newComboBox, row);
        
        row = addSection(gridPane, "Процессор", row);
        row = addFormRow(gridPane, "Модель:", modelCpuField, 
                         "Количество ядер:", coreField, row);
        row = addFormRow(gridPane, "Частота (MHz):", frequencyField, null, null, row);
        
        row = addSection(gridPane, "Оперативная память", row);
        row = addFormRow(gridPane, "Объем (GB):", ramGbField, 
                         "Тип:", ramTypeComboBox, row);
        row = addFormRow(gridPane, "Частота (MHz):", ramGhzField, null, null, row);
        
        row = addSection(gridPane, "Видеокарта", row);
        row = addFormRow(gridPane, "Модель:", modelGpuField, 
                         "VRAM (GB):", vramGbField, row);
        
        row = addSection(gridPane, "Система и питание", row);
        row = addFormRow(gridPane, "Накопитель (GB):", storageGbField, 
                         "Материнская плата:", motherBoardField, row);
        row = addFormRow(gridPane, "Сокет:", socketComboBox, 
                         "Блок питания (W):", powerSupplyField, row);
        
        styleFormFields();
        
        return gridPane;
    }
    
    /**
     * Adds a section header label to the grid at the specified row.
     *
     * @param grid The GridPane to add the section header to
     * @param title The section title text
     * @param row The current row index
     * @return The next available row index after adding the section
     */
    private int addSection(GridPane grid, String title, int row) {
        Label label = new Label(title);
        label.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #667eea;" +
            "-fx-padding: 10 0 5 0;"
        );
        label.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row, 4, 1);
        return row + 1;
    }
    
    /**
     * Adds a form row with one or two label-field pairs to the grid.
     * Associates labels with fields for validation highlighting.
     *
     * @param grid The GridPane to add the row to
     * @param label1 The first field label text
     * @param field1 The first input field node
     * @param label2 The second field label text (optional)
     * @param field2 The second input field node (optional)
     * @param row The current row index
     * @return The next available row index after adding the row
     */
    private int addFormRow(GridPane grid, String label1, javafx.scene.Node field1,
                           String label2, javafx.scene.Node field2, int row) {
        Label lbl1 = new Label(label1);
        lbl1.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        grid.add(lbl1, 0, row);
        grid.add(field1, 1, row);
        
        fieldLabels.put(field1, lbl1);
        
        if (label2 != null && field2 != null) {
            Label lbl2 = new Label(label2);
            lbl2.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
            grid.add(lbl2, 2, row);
            grid.add(field2, 3, row);
            
            fieldLabels.put(field2, lbl2);
        }
        
        return row + 1;
    }
    
    /**
     * Applies consistent styling to all text fields and combo boxes in the form.
     * Sets background colors, borders, padding, and placeholder text.
     */
    private void styleFormFields() {
        String fieldStyle = 
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;";
        
        TextField[] fields = {modelCpuField, coreField, frequencyField, ramGbField,
                              ramGhzField, modelGpuField, vramGbField, storageGbField,
                              motherBoardField, powerSupplyField};
        
        for (TextField field : fields) {
            field.setStyle(fieldStyle);
            field.setPromptText("Введите значение");
        }
        
        ComboBox<?>[] comboBoxes = {osComboBox, newComboBox, socketComboBox, ramTypeComboBox};
        
        for (ComboBox<?> combo : comboBoxes) {
            combo.setStyle(fieldStyle);
            combo.setPromptText("Выберите");
        }
    }

    /**
     * Creates and displays a modal loading window while the AI evaluation is being processed.
     * Shows animated loading indicator and status updates during the evaluation task.
     *
     * @param primaryStage The parent stage for the modal dialog
     */
    private void windowLoading(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle("Загрузка оценки");
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setOnCloseRequest(e -> e.consume());

        VBox dialogVBox = new VBox(15);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );
        
        Label statusLabel = new Label("Анализ конфигурации...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        
        try {
            String imagePath = "file:resources/images/systems/loading.png";
            Image loadingImage = new Image(imagePath);
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
        }
        
        Scene dialogScene = new Scene(dialogVBox, 500, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Отправка данных в модель...");
                String jsonData = parsingData();
                String result = getEstimating(jsonData);
                updateMessage("Обработка результата...");
                return result;
            }
            
            @Override
            protected void succeeded() {
                String result = getValue();
                dialogStage.close();
                designer.dimApplication(false);
                WindowResult windowResult = new WindowResult(result);
                windowResult.start(primaryStage);
            }
            
            @Override
            protected void failed() {
                dialogStage.close();
                designer.dimApplication(false);
                
                getException().printStackTrace();
                
                WindowResult windowResult = new WindowResult("Сбой");
                windowResult.start(primaryStage);
            }
        };
        
        statusLabel.textProperty().bind(task.messageProperty());
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Executes the Python AI model script to evaluate the PC configuration.
     * Passes JSON data to the Python script and captures the evaluation result.
     *
     * @param jsonData JSON string containing PC configuration data
     * @return The evaluation result from the AI model as a string
     */
    private String getEstimating(String jsonData) {
    	 try {
    		 String classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		     File classDir = new File(classPath).getParentFile();
		    
		     File pythonScript = new File(classDir, "../TechnoAssiatant/PythonAI/helpers/predict.py");
		     String scriptPath = pythonScript.getCanonicalPath();
             ProcessBuilder pb = new ProcessBuilder("python", scriptPath, jsonData);
             
             pb.redirectErrorStream(true);
             Process process = pb.start();

             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(process.getInputStream(), "UTF-8")
             );
             
             StringBuilder output = new StringBuilder();
             String line;
             while ((line = reader.readLine()) != null) {
                 output.append(line).append("\n");
             }
             
             process.waitFor();
             
             
             return output.toString();

         } catch (Exception e) {
        	 System.out.println("Ошибка Java: " + e.getMessage());
             e.printStackTrace();
             return "Сбой";
         }
	} 	
    
    /**
     * Attempts to parse a string to a Double value.
     * Returns null if parsing fails.
     *
     * @param number The string to parse
     * @return Parsed Double value or null if parsing fails
     */
    public static Double tryParseDouble(String number) {
        try {
            return Double.parseDouble(number.trim());
        } 
        catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Clears all validation messages and resets label colors to default state.
     */
    private void clearingLabel() {
    	messageLabel.setText("");
    	
    	for (Map.Entry<Object, Label> entry : fieldLabels.entrySet()) {
    		entry.getValue().setStyle("-fx-font-size: 13px; -fx-text-fill: #555;"); 
    	}
    }
    
    /**
     * Validates a single form field based on expected data type.
     * Highlights the associated label in red if validation fails.
     *
     * @param object The form field object (TextField or ComboBox) to validate
     * @param type Expected data type ("String" or "Double")
     * @param label The label associated with the field for highlighting
     * @return true if validation passes, false otherwise
     */
    private boolean checkingData(Object object, String type, Label label) {
        if (object instanceof TextField) {
            TextField textField = (TextField) object;
            String text = textField.getText();
            if (text != null && !text.trim().isEmpty()) {
	            if (type == "String") {
	            	if (tryParseDouble(text) != null) {
	            		label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
	            		return false;
	            	}
	            }
	            if (type == "Double") {
	            	if (tryParseDouble(text) == null) {
	            		label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
	            		return false;
	            	}
	            }
            }
            
            else {
            	label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
        		return false;
            }
        }
        else if (object instanceof ComboBox) {
            ComboBox<String> comboBox = (ComboBox<String>) object;
            String value = comboBox.getValue();
            if (value != null && !value.trim().isEmpty()) {
            	String text = value.toString();
            	
            	if (type == "String") {
                	if (tryParseDouble(text) != null) {
                		label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
                		return false;
                	}
                }
                if (type == "Double") {
                	if (tryParseDouble(text) == null) {
                		label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
                		return false;
                	}
                }
            }
            
            else {
            	label.setStyle("-fx-font-size: 13px; -fx-text-fill: red;");
        		return false;
            }
        }
        return true;
    }
    
    /**
     * Validates all form fields by checking each field's data type and content.
     * Highlights invalid fields in red.
     *
     * @return true if all fields pass validation, false otherwise
     */
    private boolean checkingField() {
    	boolean isCorrectOsComboBox = checkingData(osComboBox, "String", fieldLabels.get(osComboBox));
    	boolean isCorrectNewComboBox = checkingData(newComboBox, "String", fieldLabels.get(newComboBox));
    	boolean isCorrectRamTypeComboBox = checkingData(ramTypeComboBox, "String", fieldLabels.get(ramTypeComboBox));
    	boolean isCorrectSocketComboBox = checkingData(socketComboBox, "String", fieldLabels.get(socketComboBox));
    	
    	boolean isCorrectModelCpuField = checkingData(modelCpuField, "String", fieldLabels.get(modelCpuField));
    	boolean isCorrectCoreField = checkingData(coreField, "Double", fieldLabels.get(coreField));
    	boolean isCorrectFrequencyField = checkingData(frequencyField, "Double", fieldLabels.get(frequencyField));
    	boolean isCorrectRamGbField = checkingData(ramGbField, "Double", fieldLabels.get(ramGbField));
    	boolean isCorrectRamGhzFieldd = checkingData(ramGhzField, "Double", fieldLabels.get(ramGhzField));
    	boolean isCorrectModelGpuField = checkingData(modelGpuField, "String", fieldLabels.get(modelGpuField));
    	boolean isCorrectVramGbField = checkingData(vramGbField, "Double", fieldLabels.get(vramGbField));
    	boolean isCorrectStorageGbField = checkingData(storageGbField, "Double", fieldLabels.get(storageGbField));
    	boolean isCorrectMotherBoardField = checkingData(motherBoardField, "String", fieldLabels.get(motherBoardField));
    	boolean isCorrectPowerSupplyField = checkingData(powerSupplyField, "Double", fieldLabels.get(powerSupplyField));
    	
    	if (isCorrectOsComboBox && isCorrectNewComboBox && isCorrectRamTypeComboBox && isCorrectModelCpuField &&
    		    isCorrectCoreField && isCorrectFrequencyField && isCorrectRamGbField && isCorrectRamGhzFieldd && 
    		    isCorrectModelGpuField && isCorrectVramGbField && isCorrectStorageGbField && 
    		    isCorrectMotherBoardField && isCorrectPowerSupplyField && isCorrectSocketComboBox) {
    		   return true; 
		}
    	
    	else {
		    return false;
		}
    }
    
    /**
     * Parses all form field values into a JSON-formatted string for AI model input.
     * Converts all text to lowercase and normalizes frequency values.
     *
     * @return JSON string containing all PC configuration data
     */
    private String parsingData() {
    	String os = osComboBox.getValue().toLowerCase();
    	
    	String relevance = "";
    	if (newComboBox.getValue().toLowerCase() == "Б/У") {
    		relevance = "new";
    	}
    	else {
    		relevance = "no";
    	}
    	
    	String socket = socketComboBox.getValue().toLowerCase();
    	String ramType = ramTypeComboBox.getValue().toLowerCase();
    	
    	String cpu = modelCpuField.getText().toLowerCase();
    	String gpu = modelGpuField.getText().toLowerCase();
    	String mb = motherBoardField.getText().toLowerCase();
    	
    	Double cpuCore = tryParseDouble(coreField.getText());
    	Double cpuHz = parseToFourDigit(tryParseDouble(frequencyField.getText()));
    	Double ramGb = tryParseDouble(ramGbField.getText());
    	Double ramHz = parseToFourDigit(tryParseDouble(ramGhzField.getText()));
    	Double vramGb = tryParseDouble(vramGbField.getText());
    	Double storageGb = tryParseDouble(storageGbField.getText());
    	Double powerSupply = tryParseDouble(powerSupplyField.getText());
    	
    	
    	String jsonData = String.format(
    		    "{'os':'%s','new':'%s','model_cpu':'%s'," +
    		    "'core':'%s','frequency_ghz':'%s','socket':'%s','ram_gb':'%s'," +
    		    "'ram_type':'%s','ram_ghz':'%s','model_gpu':'%s','vram_gb':'%s'," +
    		    "'storage_gb':'%s','mother_board':'%s','power_supply':'%s'}",
    		    os, relevance, cpu, cpuCore, cpuHz,
    		    socket, ramGb, ramType, ramHz, gpu, 
    		    vramGb, storageGb, mb, powerSupply
    		);
    	
    	return jsonData;
    }
    
    /**
     * Converts frequency values to four-digit format (MHz).
     * Multiplies values less than 100 by 1000 to convert GHz to MHz.
     *
     * @param value The frequency value to convert
     * @return Converted frequency in MHz or null if input is null
     */
    public static Double parseToFourDigit(Double value) {
        if (value == null) {
            return null;
        }
        
        if (value >= 100) {
            return value;
        }
        
        return (Double) (value * 1000);
    }

}
