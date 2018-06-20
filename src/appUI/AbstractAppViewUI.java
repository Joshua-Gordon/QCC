package appUI;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractAppViewUI extends JPanel{
	
	private final String NAME;
	
	public abstract void visibilityChanged(boolean visible);
	
	public AbstractAppViewUI(String name) {
		this.NAME = name;
	}
	
	public String getName() {
		return NAME;
	}
	
	
}
