package appUIFX;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import appUI.CircuitBoardRenderContext;
import framework2FX.DefaultGate;
import framework2FX.DefaultGate.DefaultGateModel;
import utils.ResourceLoader;

@SuppressWarnings("serial")
public class GateIcon extends ImageIcon{

	public static final BasicStroke THICK = new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	public static final BasicStroke THIN = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
	public static final Polygon ARROW_HEAD = new Polygon(); 
	
	static {
		ARROW_HEAD.addPoint( 0, 0);
		ARROW_HEAD.addPoint( -2, -5);
		ARROW_HEAD.addPoint( 2,-5);
	}
	
	
	
	
	
	public static GateIcon getDefaultGateIcon(DefaultGate dg) {
		switch(dg) {
		case Cnot:
			return getCNotIcon();
		case Measurement:
			return getMeasureIcon();
		case Swap:
			return getSwapIcon();
		case Toffoli:
			return getToffoliIcon();
		default:
			DefaultGateModel dgm = (DefaultGateModel) dg.getModel();
			return new GateIcon(dgm.getSymbol() , dgm.isMultiQubitGate());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private BufferedImage image;
	private boolean multiQubit;
	private String name;
	private static final int PADDING = 10;
	private static final int LINE_LENGTH = 5;
	
	public GateIcon(String name, boolean multiQubit) {
		super();
		this.name = name;
		this.multiQubit = multiQubit;
		setImage(getBufferedImage());
	}
	
	private GateIcon(String name, boolean multiQubit, BufferedImage bi) {
		super();
		this.image = bi;
		this.name = name;
		this.multiQubit = multiQubit;
		setImage(bi);
	}
	
	public GateIcon() {
		this("", false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static GateIcon getMeasureIcon() {
		Font f = ResourceLoader.VAST_SHADOW.deriveFont(12f);
		Rectangle2D rect = CircuitBoardRenderContext.getStringBounds(f, "M");
		BufferedImage bi = new BufferedImage(2 * (PADDING + LINE_LENGTH) + (int) rect.getWidth(),
				2 * PADDING + (int) rect.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(f);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		
		g2d.drawLine(0, bi.getHeight() / 2, LINE_LENGTH, bi.getHeight() / 2);
		g2d.drawLine(bi.getWidth() - LINE_LENGTH, bi.getHeight() / 2, bi.getWidth(), bi.getHeight() / 2);
		
		g2d.drawArc(LINE_LENGTH + PADDING/2, PADDING, (int) rect.getWidth() + PADDING, (int) rect.getHeight() + PADDING, 0, 180);
		CircuitBoardRenderContext.drawArrow(g2d, ARROW_HEAD, LINE_LENGTH + PADDING + (int)(rect.getWidth() / 2), 
				(int)(1.5f * PADDING) + (int) (rect.getHeight() / 2), bi.getWidth() - LINE_LENGTH - 5, 5);
		
		g2d.dispose();
		return new GateIcon("Measure", false, bi);
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
    	return new GateIcon("SWAP", true, bi);
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
    	return new GateIcon("CNOT", true, bi);
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
    	return new GateIcon("CNOT", true, bi);
	}
	
	private BufferedImage getBufferedImage() {
		Font f = ResourceLoader.VAST_SHADOW.deriveFont(12f);
		Rectangle2D rect = CircuitBoardRenderContext.getStringBounds(f, name);
		BufferedImage bi = new BufferedImage(2 * (PADDING + LINE_LENGTH) + (int) rect.getWidth(),
				2 * PADDING + (int) rect.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(f);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(LINE_LENGTH, 0, bi.getWidth() - 2*LINE_LENGTH, bi.getHeight() - 1);
		int offset = 0;
		if(multiQubit) {
			offset = 3;
			g2d.setStroke(THICK);
		}
		g2d.drawLine(0, bi.getHeight() / 2, LINE_LENGTH - offset, bi.getHeight() / 2);
		g2d.drawLine(bi.getWidth() - LINE_LENGTH + offset, bi.getHeight() / 2, bi.getWidth(), bi.getHeight() / 2);
		CircuitBoardRenderContext.drawCenteredString(g2d, name, 0, 0, bi.getWidth(), bi.getHeight());
		g2d.dispose();
		return bi;
	}

	
	
	
	
	
	
	
	
	public boolean isMultiQubit() {
		return multiQubit;
	}

	public void setMultiQubit(boolean multiQubit) {
		this.multiQubit = multiQubit;
		setImage(getBufferedImage());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setImage(getBufferedImage());
	}
	
}