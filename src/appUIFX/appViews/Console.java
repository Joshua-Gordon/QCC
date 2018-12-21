package appUIFX.appViews;

import java.net.URL;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import appUIFX.LatexNode;
import framework.Main;
import framework2FX.AppCommand;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utils.PrintStream;
import utils.customCollections.CommandParameterList;
import utils.customCollections.CommandParameterList.ParameterOutOfBoundsException;







public class Console extends AppView implements PrintStream, Initializable, ChangeListener<Number> {
	
	public static final int PREVIOUS_COMMAND_LIMIT = 20;
	
	public TextFlow consoleArea;
	public TextField commandLine;
	public ScrollPane scroll;
	
	private LinkedList<String> previousCommands = new LinkedList<>();
	private ListIterator<String> commandsIterator = null;
	private int lastIndex = -2;
	
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
		print(text + "\n", color);
	}
	
	public void print(String text) {
		print(text, Color.BLACK);
	}
	
	public void enterCommand(ActionEvent ae) {
		commandsIterator = null;
		lastIndex = -2;
		
		String inputText = commandLine.getText();
		
		if(inputText.equals(""))
			return;
		
		addToPreviousCommands(inputText);
		
		commandLine.setText("");
		
		print("\n\n" + Main.APP_NAME + "> ", Color.CHOCOLATE);
		println(inputText + "\n");
		
		String[] parts = inputText.split("\\s+");
		
		if(parts.length != 0) {
			AppCommand ac = AppCommand.getbyName(parts[0]);
			
			if(ac == null) {
				printErrln("\nCommand \"" + parts[0] + "\" does not exist. (Type \"help\" for a list of commands)");
				return;
			}
			
			String[] parameters = new String[parts.length - 1];
			for(int i = 0; i < parameters.length; i++)
				parameters[i] = parts[i+1];
			try {
				AppCommand.doAction(this, ac, new CommandParameterList(parameters));
			} catch (ParameterOutOfBoundsException e) {
				printErrln("\n" + e.getMessage());
			}
		}
	}
	
	
	public void printLatex(String latex) {
		consoleArea.getChildren().add(new LatexNode(latex));
	}
	
	public void printLatex(String latex, float font) {
		consoleArea.getChildren().add(new LatexNode(latex, font));
	}
	
	public void printLatex(String latex, float font, String backgroundColor) {
		consoleArea.getChildren().add(new LatexNode(latex, font, backgroundColor));
	}
	
	public void printLatex(String latex, float font, String backgroundColor, String textColor) {
		consoleArea.getChildren().add(new LatexNode(latex, font, backgroundColor, textColor));
	}
	
	public void println(String text) {
		println(text, Color.BLACK);
	}
	
	public void printErrln(String text) {
		println(text, Color.RED);
	}
	
	public void printErr(String text) {
		print(text, Color.RED);
	}
	
	private void addToPreviousCommands (String command) {
		if(previousCommands.size() == PREVIOUS_COMMAND_LIMIT)
			previousCommands.removeLast();
		previousCommands.offerFirst(command);
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		commandLine.setOnKeyPressed((event) -> {
			if(commandLine.isFocused()) {
				if(event.getCode() == KeyCode.UP) {
					if(commandsIterator == null)
						commandsIterator = previousCommands.listIterator();
					if(lastIndex == commandsIterator.nextIndex())
						commandsIterator.next();
					if(commandsIterator.hasNext()) { 
						String text = commandsIterator.next();
						commandLine.setText(text);
						commandLine.positionCaret(text.length());
						lastIndex = commandsIterator.previousIndex();
					}
				} else if(event.getCode() == KeyCode.DOWN) {
					if(commandsIterator == null)
						commandsIterator = previousCommands.listIterator();
					if(lastIndex == commandsIterator.previousIndex())
						commandsIterator.previous();
					if(commandsIterator.hasPrevious()) {
						String text = commandsIterator.previous();
						commandLine.setText(text);
						commandLine.positionCaret(text.length());
						lastIndex = commandsIterator.nextIndex();
					}
				}
			}
		});
		return false;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		consoleArea.heightProperty().addListener(this);
		
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		scroll.setVvalue(scroll.getVmax());
	}
	
}
