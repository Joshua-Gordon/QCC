package appUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import utils.ResourceLoader;




@SuppressWarnings("serial")
public class GateIcon extends ImageIcon{

	public static final BasicStroke THICK = new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
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
	
	public GateIcon() {
		this("", false);
	}

	private BufferedImage getBufferedImage() {
		Rectangle2D rect = CircuitBoardRenderContext.getStringBounds(ResourceLoader.VAST_SHADOW, name);
		BufferedImage bi = new BufferedImage(2 * (PADDING + LINE_LENGTH) + (int) rect.getWidth(),
				2 * PADDING + (int) rect.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(ResourceLoader.VAST_SHADOW);
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
