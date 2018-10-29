package appUIFX;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;



//TODO:  please make TextFlow scrollable




public class Console extends AppView{
	
	public TextFlow consoleArea;
	
	public Console() {
		super("Console.fxml", "Console", Layout.BOTTOM);
	}
	
	public void clearConsole() {
		clearConsole(null);
	}
	
	public void clearConsole(ActionEvent event) {
		consoleArea.getChildren().clear();
	}
	
	public void print(String text, Color color) {
		Text text1 = new Text(text);
		text1.setFill(color);
		consoleArea.getChildren().add(text1);
	}
	
	public void println(String text, Color color) {
		print("\n" + text, color);
	}
	
	public void print(String text) {
		println(text, Color.BLACK);
	}
	
	public void println(String text) {
		print(text, Color.BLACK);
	}
	
	public void printErrln(String text) {
		println(text, Color.RED);
	}
	
	public void printErr(String text) {
		print(text, Color.RED);
	}

	@Override
	public void receive(Object source, String methodName, Object... args) {
		
	}
	
}
