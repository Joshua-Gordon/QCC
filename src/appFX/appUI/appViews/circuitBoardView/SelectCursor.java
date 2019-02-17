package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.MainScene;
import appFX.framework.AppStatus;
import appFX.framework.exportGates.Control;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class SelectCursor extends Region implements EventHandler<MouseEvent> {
	private static final ToolAction DO_NOTHING = new ToolAction(false) {
		
		public void reset() {}
		public void buttonPressed(int row, int column) {}
		public boolean isCursorDisplayed() {
			return false;
		}
	};
	
	private ToolAction currentToolAction;
	private final ChangeListener<Toggle> toolChanged, gateModelChanged;
	private final CircuitBoardView cbv;
	
	public SelectCursor(CircuitBoardView cbv) {
		this.cbv = cbv;
		MainScene ms = AppStatus.get().getMainScene();
		this.currentToolAction = getToolAction(ms.getSelectedTool());
		
		setStyle("-fx-background-color: #BDBDBD66");
		setOnMouseClicked(this);
		
		toolChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
			currentToolAction = getToolAction(newV);
		};
		
		gateModelChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
		};
	}
	
	private ToolAction getToolAction(Toggle toggle) {
		MainScene ms = AppStatus.get().getMainScene();
		
		if(toggle == ms.selectTool) {
		} else if(toggle == ms.solderTool) {
			return new SolderRegionToolAction(cbv);
		} else if(toggle == ms.editTool) {
			
		} else if(toggle == ms.controlTool) {
			return new ControlToolAction(cbv, Control.CONTROL_TRUE);
		} else if(toggle == ms.controlNotTool) {
			return new ControlToolAction(cbv, Control.CONTROL_FALSE);
		} else if(toggle == ms.addColumnTool) {
			
		} else if(toggle == ms.removeColumnTool) {
			
		} else if(toggle == ms.addRowTool) {
			
		} else if(toggle == ms.removeRowTool) {
			
		}
		hideTool();
		return DO_NOTHING;
	}

	public ChangeListener<Toggle> getToolChangedListener() {
		return toolChanged;
	}
	
	public ChangeListener<Toggle> getModelChangedListener() {
		return gateModelChanged;
	}
	
	public void showTool() {
		if(currentToolAction.isCursorDisplayed()) {
			setManaged(true);
			setVisible(true);
		}
	}
	
	public void hideTool() {
		setManaged(false);
		setVisible(false);
	}
	
	private int getRow() {
		return GridPane.getRowIndex(this);
	}
	
	private int getColumn () {
		return GridPane.getColumnIndex(this) - 1;
	}

	@Override
	public void handle(MouseEvent event) {
		currentToolAction.buttonPressed(getRow(), getColumn());
	}
	
	public ToolAction getCurrentTool() {
		return currentToolAction;
	}
}
