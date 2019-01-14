package appFX.appUI;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public class AppTab extends Tab {
	
	private final AppFXMLComponent component;
	
	public AppTab(String name, Node content, AppFXMLComponent component) {
		super(name, content);
		this.component = component;
	}
	
	public AppTab(String name, AppFXMLComponent component) {
		super(name);
		this.component = component;
	}
	
	public AppTab(AppFXMLComponent component) {
		this.component = component;
	}
	
	
	public AppFXMLComponent getAppFXMLComponent() {
		return component;
	}
}
