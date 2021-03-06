package appFX.appUI.appViews.gateChooser;

import java.util.Iterator;

import appFX.appUI.GateIcon;
import appFX.appUI.GateModelContextMenu;
import appFX.appUI.appViews.AppView;
import appFX.framework.AppCommand;
import appFX.framework.gateModels.GateModel;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

public abstract class AbstractGateChooser extends AppView implements Initializable {
	
	public Button button;
	public HBox buttonBox;
	public TilePane list;
	
	private static ToggleGroup tg = new ToggleGroup();
	
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
	
	
	
	public void addGateModel(GateModel s) {
		ObservableList<Node> buttons = list.getChildren();
		GateToggleButton stb = new GateToggleButton(s);
		buttons.add(stb);
		tg.getToggles().add(stb);
	}
	
//	public void putGateModel(GateModel s) {
//		ObservableList<Node> buttons = list.getChildren();
//		Iterator<Node> iter = buttons.iterator();
//		while(iter.hasNext()) {
//			GateToggleButton stb = (GateToggleButton) iter.next();
//			if(stb.gateModel.getFormalName().equals(s.getFormalName())) {
//				iter.remove();
//				break;
//			}
//		}
//		GateToggleButton stb = new GateToggleButton(s);
//		buttons.add(stb);
//		tg.getToggles().add(stb);
//	}
	
	public void removeGateModelByName(String name) {
		Iterator<Node> buttons = list.getChildren().iterator();
		while(buttons.hasNext()) {
			GateToggleButton stb = (GateToggleButton) buttons.next();
			if(stb.gateModel.getFormalName().equals(name)) {
				buttons.remove();
				tg.getToggles().remove(stb);
				return;
			}
		}
	}
	
//	public void removeGateModel(GateModel s) {
//		Iterator<Node> buttons = list.getChildren().iterator();
//		while(buttons.hasNext()) {
//			GateToggleButton stb = (GateToggleButton) buttons.next();
//			if(stb.gateModel == s) {
//				buttons.remove();
//				tg.getToggles().remove(stb);
//				return;
//			}
//		}
//	}
	
	public void removeAllGateModels() {
		ObservableList<Node> buttons = list.getChildren();
		buttons.clear();
	}
	
	public static GateModel getSelected() {
		GateToggleButton stb = (GateToggleButton) tg.getSelectedToggle();
		return stb == null ? null : stb.gateModel;
	}
	
	public static void addToggleListener(ChangeListener<? super Toggle> listener) {
		tg.selectedToggleProperty().addListener(listener);
	}
	
	public static void removeToggleListener(ChangeListener<? super Toggle> listener) {
		tg.selectedToggleProperty().removeListener(listener);
	}
	
	
	
	public static class GateToggleButton extends ToggleButton {
		
		private final GateModel gateModel;
		
		public GateToggleButton (GateModel gateModel) {
			this.gateModel = gateModel;
			GateIcon gi = GateIcon.getGateIcon(gateModel);
			setTooltip(new Tooltip(gateModel.getName()));
			setGraphic(gi.getView());
			setOnMouseClicked((mouseEvent) -> {
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY))
		            if(mouseEvent.getClickCount() == 2)
		            	AppCommand.doAction(AppCommand.OPEN_GATE, gateModel.getFormalName());
		                
			});
			setContextMenu(new GateModelContextMenu(null, gateModel));
		}
		
		public GateModel getGateModel() {
			return gateModel;
		}
	}
}
