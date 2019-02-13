package appTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import appUI.CircuitBoardRenderContext;
import appUI.Window;

public class RemoveRowTool extends Tool{
	
	private static final int GATE_PIXEL_SIZE = CircuitBoardRenderContext.GATE_PIXEL_SIZE;
	public static final BasicStroke HEAVY = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private Point prevHoveredGrid = null;
	
	public RemoveRowTool( Window window, ImageIcon icon) {
		super("Remove Row Tool", window, icon);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		
		if(prevHoveredGrid == null || p.distance(prevHoveredGrid) != 0) {
			BufferedImage bi = window.getRenderContext().getOverlay();
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			
			g2d.setColor(new Color(255, 0, 0, 120));
			g2d.fillRect(0, p.y * GATE_PIXEL_SIZE, bi.getWidth() - 1, GATE_PIXEL_SIZE - 1);
			g2d.setColor(Color.RED);
			g2d.setStroke(HEAVY);
			g2d.drawRect(0, p.y * GATE_PIXEL_SIZE, bi.getWidth() - 1, GATE_PIXEL_SIZE - 1);
			
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

		window.getSelectedBoard().removeRow(p.y);
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
		mouseCoords.x = -1;
		mouseCoords.y = (int) Math.floor((double) mouseCoords.y / GATE_PIXEL_SIZE);
	}
}
