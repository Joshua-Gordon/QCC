package appUIFX;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

public abstract class AbstractGateChooser extends AppView implements Initializable{
	
	public Button button;
	public HBox buttonBox;
	public TilePane list;
	
	protected static ToggleGroup tg = new ToggleGroup();
	
	public AbstractGateChooser(String viewName) {
		super("GateChooser.fxml", viewName, Layout.RIGHT);
	}
	
	public abstract void buttonAction();
	
	public void setButtonVisible(boolean visible) {
		buttonBox.setVisible(visible);
		buttonBox.setManaged(visible);
	}
	
	public void setButtonText(String label) {
		button.setText(label);
	}
	
}
