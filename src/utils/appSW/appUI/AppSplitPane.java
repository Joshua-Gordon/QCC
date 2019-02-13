package appSW.appUI;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JSplitPane;


@SuppressWarnings("serial")
public class AppSplitPane extends JSplitPane implements VisibilityListener, ComponentListener{
	private int previousPosition;
	private AbstractAppViewUI aavUI;
	
	public AppSplitPane(AbstractAppViewUI aavUI, int dividerLocation, int orientation) {
		super(orientation);
		this.aavUI = aavUI;
		aavUI.setVisibilityListener(this);
		aavUI.addComponentListener(this);
		this.previousPosition = dividerLocation;
		setDividerLocation(dividerLocation);
	}
	
	@Override
	public int getDividerLocation() {
		if(aavUI.isVisible())
			return super.getDividerLocation();
		else
			return previousPosition;
	}

	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {
		setDividerLocation(previousPosition);
		setEnabled(true);
	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void preparingVisibleChange(boolean visible) {
		if(!visible) {
			previousPosition = getDividerLocation();
			setEnabled(false);
		}
	}
}
