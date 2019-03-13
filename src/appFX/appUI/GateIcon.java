package appFX.appUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appSW.appUI.CircuitBoardRenderContext;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GateIcon  {

	public static final BasicStroke THICK = new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	public static final BasicStroke THIN = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
	public static final Polygon ARROW_HEAD = new Polygon(); 
	public static final Image MEASUREMENT = getMeasureIconWithNoLine();
	
	
	static {
		ARROW_HEAD.addPoint( 0, 0);
		ARROW_HEAD.addPoint( -2, -5);
		ARROW_HEAD.addPoint( 2,-5);
	}
	
	public static GateIcon getGateIcon(GateModel s) {
		if(s instanceof PresetGateModel) {
			PresetGateModel pgm = (PresetGateModel) s;
			
			switch(pgm.getPresetGateType()) {
			case CNOT:
				return getCNotIcon();
			case MEASUREMENT:
				return getMeasureIcon();
			case SWAP:
				return getSwapIcon();
			case TOFFOLI:
				return getToffoliIcon();
			default:
			}
		}
		return new GateIcon(s);
	}
	
	private Node icon;
	private static final int PADDING = 10;
	private static final int LINE_LENGTH = 5;
	
	private GateIcon(GateModel s) {
		icon = SolderableIcon.mkIcon(s);
	}
	
	private GateIcon(BufferedImage bi) {
		Image image = SwingFXUtils.toFXImage(bi, null);
		icon = new ImageView(image);
	}
	
	public Node getView () {
		return icon;
	}
	
	
	
	
	
	private static Image getMeasureIconWithNoLine() {
		int height = 20;
		int width = 20;
		
		BufferedImage bi = new BufferedImage(2 * (PADDING) + width,
				2 * PADDING + height, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, bi.getWidth() - 1, bi.getHeight() - 1);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, bi.getWidth() - 1, bi.getHeight() - 1);
		
		g2d.drawLine(0, bi.getHeight() / 2, 0, bi.getHeight() / 2);
		g2d.drawLine(bi.getWidth() - 1, bi.getHeight() / 2, bi.getWidth() - 1, bi.getHeight() / 2);
		
		g2d.drawArc(PADDING/2, PADDING, width + PADDING, height + PADDING, 0, 180);
		CircuitBoardRenderContext.drawArrow(g2d, ARROW_HEAD, PADDING + width / 2, 
				(int)(1.5f * PADDING) + height / 2, bi.getWidth() - 5, 5);
		
		g2d.dispose();
		return SwingFXUtils.toFXImage(bi, null);
	}
	
	
	
	
	
	
	
	private static GateIcon getMeasureIcon() {
		int height = 20;
		int width = 20;
		
		BufferedImage bi = new BufferedImage(2 * (PADDING + LINE_LENGTH) + width,
				2 * PADDING + height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
    	g2d.setColor(Color.WHITE);
		g2d.fillRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		
		g2d.drawLine(0, bi.getHeight() / 2, LINE_LENGTH, bi.getHeight() / 2);
		g2d.drawLine(bi.getWidth() - LINE_LENGTH, bi.getHeight() / 2, bi.getWidth(), bi.getHeight() / 2);
		
		g2d.drawArc(LINE_LENGTH + PADDING/2, PADDING, width + PADDING, height + PADDING, 0, 180);
		CircuitBoardRenderContext.drawArrow(g2d, ARROW_HEAD, LINE_LENGTH + PADDING + width / 2, 
				(int)(1.5f * PADDING) + height / 2, bi.getWidth() - LINE_LENGTH - 5, 5);
		
		g2d.dispose();
		return new GateIcon(bi);
	}
	
	private static GateIcon getSwapIcon() {
		final int vertLineLength = 25;
		final int xRadius = 10;
		final int lineLength = PADDING + LINE_LENGTH;
		final int vertPadding = 2;
		
		BufferedImage bi = new BufferedImage(2 * (lineLength + xRadius),
				2 * vertPadding + vertLineLength + 2 * xRadius, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		
        g2d.drawLine(0, vertPadding + xRadius, bi.getWidth(), vertPadding + xRadius);
        g2d.drawLine(0, bi.getHeight() - vertPadding - xRadius, bi.getWidth(), bi.getHeight() - vertPadding - xRadius);
        
    	g2d.setStroke(THIN);
    	g2d.drawLine(lineLength, bi.getHeight() - vertPadding - 2 * xRadius, bi.getWidth() - lineLength, bi.getHeight() - vertPadding);
    	g2d.drawLine(lineLength, bi.getHeight() - vertPadding, bi.getWidth() - lineLength, bi.getHeight() - vertPadding - 2 * xRadius);
    	g2d.drawLine(lineLength, vertPadding, bi.getWidth() - lineLength, vertPadding + 2 * xRadius);
    	g2d.drawLine(lineLength, vertPadding + 2 * xRadius, bi.getWidth() - lineLength, vertPadding);
    	g2d.drawLine(lineLength + xRadius, bi.getHeight() - vertPadding - xRadius, lineLength + xRadius, vertPadding + xRadius);
    	g2d.dispose();
    	return new GateIcon(bi);
	}
	
	private static GateIcon getCNotIcon() {
		final int vertLineLength = 25;
		final int circleRadius = 10;
		final int smallCircleRadius = 3;
		final int lineLength = PADDING + LINE_LENGTH;
		final int vertPadding = 2;
		
		BufferedImage bi = new BufferedImage(2 * (lineLength + circleRadius),
				2 * vertPadding + vertLineLength + 2 * circleRadius, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		
        g2d.drawLine(0, vertPadding + circleRadius, bi.getWidth(), vertPadding + circleRadius);
        g2d.drawLine(0, bi.getHeight() - vertPadding - circleRadius, bi.getWidth(), bi.getHeight() - vertPadding - circleRadius);
        
    	g2d.setStroke(THIN);
    	g2d.drawOval(lineLength, bi.getHeight() - vertPadding - 2 * circleRadius, 2 * circleRadius, 2 * circleRadius);
    	g2d.drawLine(lineLength, bi.getHeight() - vertPadding - circleRadius, lineLength + 2 * circleRadius, bi.getHeight() - vertPadding - circleRadius);
    	g2d.drawLine(lineLength + circleRadius, bi.getHeight() - vertPadding, lineLength + circleRadius, vertPadding + circleRadius);
    	g2d.fillOval(lineLength + circleRadius - smallCircleRadius, vertPadding + circleRadius - smallCircleRadius, 2 * smallCircleRadius, 2 * smallCircleRadius);
    	g2d.dispose();
    	return new GateIcon(bi);
	}
	
	private static GateIcon getToffoliIcon() {
		final int vertLineLength = 25;
		final int circleRadius = 6;
		final int smallCircleRadius = 3;
		final int lineLength = PADDING + LINE_LENGTH;
		final int vertPadding = 2;
		
		BufferedImage bi = new BufferedImage(2 * (lineLength + circleRadius),
				2 * vertPadding + vertLineLength + 2 * circleRadius, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		
        g2d.drawLine(0, vertPadding + circleRadius, bi.getWidth(), vertPadding + circleRadius);
        g2d.drawLine(0, bi.getHeight() / 2, bi.getWidth(), bi.getHeight() / 2);
        g2d.drawLine(0, bi.getHeight() - vertPadding - circleRadius, bi.getWidth(), bi.getHeight() - vertPadding - circleRadius);
        
    	g2d.setStroke(THIN);
    	g2d.drawOval(lineLength, bi.getHeight() - vertPadding - 2 * circleRadius, 2 * circleRadius, 2 * circleRadius);
    	g2d.drawLine(lineLength, bi.getHeight() - vertPadding - circleRadius, lineLength + 2 * circleRadius, bi.getHeight() - vertPadding - circleRadius);
    	g2d.drawLine(lineLength + circleRadius, bi.getHeight() - vertPadding, lineLength + circleRadius, vertPadding + circleRadius);
    	g2d.fillOval(lineLength + circleRadius - smallCircleRadius, vertPadding + circleRadius - smallCircleRadius, 2 * smallCircleRadius, 2 * smallCircleRadius);
    	g2d.fillOval(lineLength + circleRadius - smallCircleRadius, bi.getHeight() / 2 - smallCircleRadius, 2 * smallCircleRadius, 2 * smallCircleRadius);
    	g2d.dispose();
    	return new GateIcon(bi);
	}
	
}