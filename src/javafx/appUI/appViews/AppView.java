package javafx.appUI.appViews;

import javafx.appUI.AppFXMLComponent;
import javafx.appUI.AppTab;
import javafx.framework.AppStatus;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public abstract class AppView extends AppFXMLComponent implements ReceivedEvent {
	
	public static enum Layout {
		CENTER, LEFT, RIGHT, BOTTOM;
	}
	
	private final Layout layout;
	private final String viewName;
	private ViewListener viewListener = null;
	
	public AppView(String fxmlFilename, String viewName, Layout layout) {
		super(fxmlFilename);
		this.layout = layout;
		this.viewName = viewName;
	}
	
	public void setViewListener(ViewListener listener) {
		viewListener = listener;
	}
	
	public ViewListener getViewListener() {
		return viewListener;
	}
	
	public Layout getLayout() {
		return layout;
	}
	
	public AppTab getTab() {
		AppTab tab = new AppTab(viewName, this);
		tab.setContent(loadAsNode());
		return tab;
	}
	
	public String getName() {
		return viewName;
	}
	
	public void closeView() {
		AppStatus.get().getMainScene().removeView(this);
	}
	
	public void addToReceiveEventListener() {
		AppStatus.get().addAppChangedListener(this);
	}
	
	public void removeEventListener() {
		AppStatus.get().removeAppChangedListener(this);
	}
	
	
	public static interface ViewListener {
		
		/**
		 * Called when this view was removed or added to the display
		 * @param wasRemoved true when view was added, false when view was removed
		 */
		public void viewChanged(boolean wasAdded);
	}
	
}
