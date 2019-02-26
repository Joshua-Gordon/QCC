package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.MainScene;
import appFX.framework.AppStatus;
import appFX.framework.exportGates.Control;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class SelectCursor implements EventHandler<MouseEvent> {
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
	private final Region cursorDisp;
	private int row, column;
	
	public SelectCursor(CircuitBoardView cbv) {
		this.cbv = cbv;
		this.cursorDisp = new Region();
		MainScene ms = AppStatus.get().getMainScene();
		this.currentToolAction = getToolAction(ms.getSelectedTool());
		currentToolAction.initToolCursorRender(cursorDisp);
		setPosition(0, 0);
		
		cursorDisp.setOnMouseClicked(this);
		
		toolChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
			currentToolAction = getToolAction(newV);
			currentToolAction.initToolCursorRender(cursorDisp);
			setPosition(row, column);
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
			return new RowColumnToolAction(cbv, true, false);
		} else if(toggle == ms.removeColumnTool) {
			return new RowColumnToolAction(cbv, false, false);
		} else if(toggle == ms.addRowTool) {
			return new RowColumnToolAction(cbv, true, true);
		} else if(toggle == ms.removeRowTool) {
			return new RowColumnToolAction(cbv, false, true);
		}
		hideCursor();
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
			cursorDisp.setManaged(true);
			cursorDisp.setVisible(true);
		}
	}
	
	public void addToNodeList(ObservableList<Node> nodes) {
		nodes.add(cursorDisp);
	}
	
	public void hideCursor() {
		cursorDisp.setManaged(false);
		cursorDisp.setVisible(false);
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn () {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}
	
	public void setRow(int row) {
		this.row = row;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}
	
	public void setPosition(int row, int column) {
		this.row = row;
		this.column = column;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}

	@Override
	public void handle(MouseEvent event) {
		currentToolAction.buttonPressed(getRow(), getColumn());
	}
	
	public ToolAction getCurrentTool() {
		return currentToolAction;
	}
}
