package appUIFX;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public abstract class AppView extends AppFXMLComponent implements ReceivedEvent {
	
	public static enum Layout {
		CENTER, LEFT, RIGHT, BOTTOM;
	}
	
	private final Layout layout;
	private final String viewName;
	
	public AppView(String fxmlFilename, String viewName, Layout layout) {
		super(fxmlFilename);
		this.layout = layout;
		this.viewName = viewName;
	}
	
	
	public Layout getLayout() {
		return layout;
	}
	
	public Tab getTab(Stage stage) {
		Tab tab = new Tab(viewName);
		tab.setContent(loadAsNode(stage));
		return tab;
	}
	
	public String getName() {
		return viewName;
	}
	
}
