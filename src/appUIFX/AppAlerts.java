package appUIFX;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Window;

public class AppAlerts {
	
	public static Optional<ButtonType> showMessage(Window window, String title, String message, AlertType type){
		return constructBasicAlert(window, title, message, type).showAndWait();
	}
	
	
	public static void showJavaExceptionMessage(Window window, String title, String message, Exception e) {
		Alert alert = constructBasicAlert(window, title, message, AlertType.ERROR);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();
		
		
		Label label = new Label("The exception stacktrace was:");
		
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
		
		alert.showAndWait();
	}
	
	
	private static Alert constructBasicAlert(Window window, String title, String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(message);
		if(window != null) {
			alert.initOwner(window);
			alert.initModality(Modality.APPLICATION_MODAL);
		}
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		return alert;
	}
	
	
	
}
