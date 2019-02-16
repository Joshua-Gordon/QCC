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
	private static final ToolAction DO_NOTHING = new ToolAction() {
		public void reset() {}
		public void buttonPressed(int row, int column) {}
		public boolean isCursorDisplayed() {
			return false;
		}
	};
	
	private ToolAction currentToolAction = DO_NOTHING;
	private final ChangeListener<Toggle> toolChanged, gateModelChanged;
	private final CircuitBoardView cbv;
	
	public SelectCursor(CircuitBoardView cbv) {
		this.cbv = cbv;
		
		setStyle("-fx-background-color: #BDBDBD66");
		setOnMouseClicked(this);
		
		toolChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
			currentToolAction = getNextToolAction(newV);
		};
		
		gateModelChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
		};
	}
	
	private ToolAction getNextToolAction(Toggle oldV) {
		MainScene ms = AppStatus.get().getMainScene();
		
		if(oldV == ms.selectTool) {
		} else if(oldV == ms.solderTool) {
			return new SolderRegionToolAction(cbv);
		} else if(oldV == ms.editTool) {
			
		} else if(oldV == ms.controlTool) {
			return new ControlToolAction(cbv, Control.CONTROL_TRUE);
		} else if(oldV == ms.controlNotTool) {
			return new ControlToolAction(cbv, Control.CONTROL_FALSE);
		} else if(oldV == ms.addColumnTool) {
		
		} else if(oldV == ms.removeColumnTool) {
		
		} else if(oldV == ms.addRowTool) {
		
		} else if(oldV == ms.removeRowTool) {
		
		}
		
		return DO_NOTHING;
	}

	public ChangeListener<Toggle> getToolChangedListener() {
		return toolChanged;
	}
	
	public ChangeListener<Toggle> getModelChangedListener() {
		return gateModelChanged;
	}
	
	public void setPosition(int row, int column) {
		GridPane.setRowIndex(this, row);
		GridPane.setColumnIndex(this, column + 1);
	}
	
	public void showTool(boolean show) {
		setManaged(show);
		setVisible(show);
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
