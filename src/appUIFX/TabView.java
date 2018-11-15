package appUIFX;

public enum TabView {
	CONSOLE(new Console()),
	DEFAULT_GATES_VIEW(new PresetGatesView()),
	PROJECT_HIERARCHY(new ProjectHierarchy());
	
	;
	
	private final AppView appView;
	private ViewListener listener;
	
	private TabView(AppView appView) {
		this.appView = appView;
	}
	
	public void setViewListener(ViewListener listener) {
		this.listener = listener;
	}
	
	public ViewListener getViewListener() {
		return listener;
	}
	
	public AppView getView() {
		return appView;
	}
	
	public static interface ViewListener {
		
		/**
		 * Called when this view was removed or added to the display
		 * @param wasRemoved true when view was added, false when view was removed
		 */
		public void viewChanged(boolean wasAdded);
	}
	
}
