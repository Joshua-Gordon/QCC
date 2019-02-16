package appFX.appUI.appViews.circuitBoardView;

public interface ToolAction {
	public void buttonPressed(int row, int column);
	public void reset();
	public boolean isCursorDisplayed();
}