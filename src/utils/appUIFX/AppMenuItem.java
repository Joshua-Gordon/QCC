package appUIFX;

import appPreferencesFX.AppPreferences;
import javafx.scene.control.MenuItem;

public class AppMenuItem extends MenuItem implements AppPreferences{
	
	private KeyShortCuts shortCut;
	
	public AppMenuItem() {
		this(null);
	}
	
	
	public AppMenuItem(KeyShortCuts shortCut) {
		super();
		this.shortCut = shortCut;
		applyShortCut();
	}
	
	
	public void applyShortCut() {
		if(shortCut != null) setAccelerator(shortCut.get());
	}
	
	
	
}
