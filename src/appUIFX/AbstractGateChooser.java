package appUIFX;

import framework2FX.solderedGates.Solderable;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
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
	
	public boolean containsSolderable (Solderable s) {
		ObservableList<Node> buttons = list.getChildren();
		for(Node b : buttons) {
			SolderableToggleButton stb = (SolderableToggleButton) b;
			if(stb.solderable == s) return true;
		}
		return false;
	}
	
	public void addSolderable(Solderable s) {
		ObservableList<Node> buttons = list.getChildren();
		if(!containsSolderable(s)) {
			SolderableToggleButton stb = new SolderableToggleButton(s);
			buttons.add(stb);
			tg.getToggles().add(stb);
		}
	}
	
	public void removeSolderable(Solderable s) {
		ObservableList<Node> buttons = list.getChildren();
		for(Node b : buttons) {
			SolderableToggleButton stb = (SolderableToggleButton) b;
			if(stb.solderable == s) {
				buttons.remove(stb);
				tg.getToggles().remove(stb);
				return;
			}
		}
	}
	
	public void removeAllSolderables() {
		ObservableList<Node> buttons = list.getChildren();
		for(Node node : buttons) {
			SolderableToggleButton button = (SolderableToggleButton) node;
			buttons.remove(button);
			tg.getToggles().remove(button);
		}
	}
	
	public Solderable getSelected() {
		SolderableToggleButton stb = (SolderableToggleButton) tg.getSelectedToggle();
		return stb.solderable;
	}
	
	public static class SolderableToggleButton extends ToggleButton {
		
		private final Solderable solderable;
		
		public SolderableToggleButton (Solderable solderable) {
			this.solderable = solderable;
			GateIcon gi = GateIcon.getGateIcon(solderable);
			setGraphic(gi.getView());
		}
		
		public Solderable getSolderable() {
			return solderable;
		}
	}
}
