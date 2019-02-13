package appSW.appUI.appTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import appSW.appUI.CircuitBoardRenderContext;
import appSW.appUI.Window;

public class AddColumnTool extends Tool{

	private static final int GATE_PIXEL_SIZE = CircuitBoardRenderContext.GATE_PIXEL_SIZE;
	public static final BasicStroke HEAVY = new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private int columnPixelSelection = 0;
	private Point prevHoveredGrid = null;
	
	public AddColumnTool( Window window, ImageIcon icon) {
		super("Add Column Tool", window, icon);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		
		if(prevHoveredGrid == null || p.distance(prevHoveredGrid) != 0) {
			BufferedImage bi = window.getRenderContext().getOverlay();
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			
			g2d.setColor(Color.GREEN);
			g2d.setStroke(HEAVY);
			g2d.drawLine(columnPixelSelection, 0, columnPixelSelection, bi.getHeight());
			
			window.getRenderContext().paintBaseImageWithOverlay(bi);
		}
		
		
		prevHoveredGrid = p;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {

		window.getRenderContext().removeOverlay();
		prevHoveredGrid = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		Point p = e.getPoint();
		setGridLocation(p);

		window.getSelectedBoard().addColumn(p.x + p.y);
		window.getRenderContext().paintRerenderedBaseImageOnly();
		
		prevHoveredGrid = null;
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
		int columnWidth = window.getRenderContext().getColumnWidth(params[0]) * GATE_PIXEL_SIZE;
		double r = mouseCoords.x - params[1];
		int fract = (int) Math.round( r / (double) columnWidth);
		mouseCoords.x = params[0];
		mouseCoords.y = fract;

		columnPixelSelection = (int) (params[1] + fract * columnWidth);
	}

}
