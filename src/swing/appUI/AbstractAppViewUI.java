package swing.appUI;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractAppViewUI extends JPanel implements VisibilityListener{
	
	private final String NAME;
	private VisibilityListener vl = this;
	
	public AbstractAppViewUI(String name) {
		this.NAME = name;
	}
	
	public String getName() {
		return NAME;
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		vl.preparingVisibleChange(aFlag);
		super.setVisible(aFlag);
	}
	
	public void setVisibilityListener(VisibilityListener vl) {
		this.vl = vl;
	}
	
	@Override
	public void preparingVisibleChange(boolean visible) {}
}
