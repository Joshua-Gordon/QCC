package appFX.appUI.appViews.circuitBoardView;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public abstract class ToolAction {
	private final boolean showCursor;
	
	public ToolAction (boolean showCursor) {
		this.showCursor = showCursor;
	}
	
	public abstract void buttonPressed(int row, int column);
	public abstract void reset();
	public abstract boolean isCursorDisplayed();
	
	public void initToolCursorRender(Region cursor) {
		cursor.setStyle("-fx-background-color: #BDBDBD66");
	}
	
	public void updateCursorPosition(Region cursor, int row, int column) {
		GridPane.setConstraints(cursor, column + 1, row, 1, 1);		
	}
	
	
	public boolean shouldShowTool() {
		return showCursor;
	}
}