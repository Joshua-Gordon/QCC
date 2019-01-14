package javafx.appUI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

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
	
	public static class Prompt implements EventHandler<WindowEvent>{
		private Stage stage;
		private TextField responseField;
		private Optional<ButtonType> closeType = null;
		private boolean opened = false;
		private AcceptInputRunnable runnable;
		
		public Prompt(Window w, String title, String promptMessage, String defaultText, int width, int height) {
			this(w, title, promptMessage, defaultText, null, width, height);
		}
		
		public Prompt(Window w, String title, String promptMessage, String defaultText, AcceptInputRunnable runnable, int width, int height) {
			stage = new Stage();
			stage.setTitle(title);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(w);
			stage.setOnCloseRequest(this);
			
			this.runnable = runnable;
			
			Insets insets = new Insets(10, 10, 10, 10);
			Insets insets2 = new Insets (0, 0, 10, 0);
			
			Label l = new Label(promptMessage);
			l.setPadding(insets2);
			l.setStyle("-fx-font-weight: bold");
			
			responseField = new TextField(defaultText);
			
			Button b1 = new Button("Apply");
			Button b2 = new Button("Cancel");
			
			b1.setOnAction((e) -> {
				if(this.runnable == null || this.runnable.accept(responseField.getText())) {
					closeType = Optional.of(ButtonType.APPLY);
					stage.close();
				}
			});
			
			b2.setOnAction((e) -> {
				closeType = Optional.of(ButtonType.CANCEL);
				stage.close();
			});
			
			
			HBox hbox = new HBox(b1, b2);
			hbox.setAlignment(Pos.CENTER_RIGHT);
			hbox.setPadding(insets);
			HBox.setMargin(b2, new Insets(0, 0, 0, 10));
			
			VBox vbox = new VBox(l, responseField, hbox);
			vbox.setPadding(insets);
			
			Scene scene = new Scene(vbox , width, height);
			
			stage.setScene(scene);
		}
		
		public void setAcceptRunnable(AcceptInputRunnable runnable) {
			this.runnable = runnable;
		}
		
		public Window getPromptWindow() {
			return stage;
		}
		
		public Optional<ButtonType> showAndWait() {
			if(!opened) { 
				stage.showAndWait();
				opened = true;
				return closeType;
			} else {
				throw new RuntimeException("This prompt has alread been opened");
			}
		}
		
		public String getReply() {
			return responseField.getText();
		}

		@Override
		public void handle(WindowEvent arg0) {
			if(closeType == null)
				closeType = Optional.of(ButtonType.CLOSE);
		}
	}
	
	public static interface AcceptInputRunnable {
		public boolean accept(String s);
	}
	
}
