package appTools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import appUI.CircuitBoardRenderContext;
import appUI.Window;

public class AddColumnTool extends Tool{

	private static final int GATE_PIXEL_SIZE = CircuitBoardRenderContext.GATE_PIXEL_SIZE;
	private int columnPixelSelection = 0;
	private Point prevHoveredGrid = null;
	
	public AddColumnTool( Window window, ImageIcon icon) {
		super("Add Column Tool", window, icon);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		
		
		
		
		prevHoveredGrid = p;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		
		prevHoveredGrid = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		

		prevHoveredGrid = p;
	}
	
	@Override
	public void onSelected() {
		prevHoveredGrid = null;
	}

	@Override
	public void onUnselected() {
		window.getRenderContext().removeOverlay();
	}
	
	public void setGridLocation(Point mouseCoords) {
		int[] params = window.getRenderContext().getGridColumnPosition(mouseCoords.x);
		mouseCoords.x = params[0];
		columnPixelSelection = params[1];
		mouseCoords.y = (int) (mouseCoords.getY() / GATE_PIXEL_SIZE);
	}

}
