package appFX.appUI.appViews.circuitBoardView;

public abstract class ToolAction {
	private final boolean showCursor;
	
	public ToolAction (boolean showCursor) {
		this.showCursor = showCursor;
	}
	
	public abstract void buttonPressed(int row, int column);
	public abstract void reset();
	public abstract boolean isCursorDisplayed();
	
	public boolean shouldShowTool() {
		return showCursor;
	}
}