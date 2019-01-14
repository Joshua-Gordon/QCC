package swing.appUI.appTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import swing.appUI.CircuitBoardRenderContext;
import swing.appUI.Window;

public class AddRowTool extends Tool{

	private static final int GATE_PIXEL_SIZE = CircuitBoardRenderContext.GATE_PIXEL_SIZE;
	public static final BasicStroke HEAVY = new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Point prevHoveredGrid = null;
	
	public AddRowTool( Window window, ImageIcon icon) {
		super("Add Row Tool", window, icon);
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
			g2d.drawLine(0, (p.x + p.y) * GATE_PIXEL_SIZE, bi.getWidth() - 1, (p.x + p.y) * GATE_PIXEL_SIZE);
			
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

		window.getSelectedBoard().addRow(p.x + p.y);
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
		int fract = (int) Math.round((double)(mouseCoords.y % GATE_PIXEL_SIZE) / GATE_PIXEL_SIZE);
		mouseCoords.x = fract;
		mouseCoords.y = (int) Math.floor((double) mouseCoords.y / GATE_PIXEL_SIZE);
	}
}
