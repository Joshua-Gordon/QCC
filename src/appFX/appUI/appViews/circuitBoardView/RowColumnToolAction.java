package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.AppAlerts;
import appFX.framework.AppStatus;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class RowColumnToolAction extends ToolAction {

	private static final String formatString = "-fx-background-color: #%s77;-fx-border-color: #%s;-fx-border-width: 3;";
	private CircuitBoardView cbv;
	private final boolean addRemove;
	private final boolean rowColumn;
	
	public RowColumnToolAction(CircuitBoardView cbv, boolean addRemove, boolean rowColumn) {
		super(true);
		this.cbv = cbv;
		this.addRemove = addRemove;
		this.rowColumn = rowColumn;
	}
	
	@Override
	public void initToolCursorRender(Region cursor) {
		String color = addRemove? "00FF00" : "FF0000";
		String style = String.format(formatString, color, color);
		cursor.setStyle(style);
	}
	
	@Override
	public void updateCursorPosition(Region cursor, int row, int column) {
		if(rowColumn)
			GridPane.setConstraints(cursor, 1, row, GridPane.REMAINING, 1);
		else
			GridPane.setConstraints(cursor, column + 1, 0, 1, GridPane.REMAINING);
	}
	
	@Override
	public void buttonPressed(int row, int column) {
		try {
			if(rowColumn)
				if(addRemove)
					cbv.getCircuitBoardModel().addRows(row, 1);
				else
					cbv.getCircuitBoardModel().removeRows(row, row + 1);
			else
				if(addRemove)
					cbv.getCircuitBoardModel().addColumns(column, 1);
				else
					cbv.getCircuitBoardModel().removeColumns(column, column + 1);
		} catch(IllegalArgumentException iae) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), 
					"Could not " + (addRemove? "add":"remove") + " " + (rowColumn? "Row":"Column"),
					iae.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void reset() {
		
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}

}
