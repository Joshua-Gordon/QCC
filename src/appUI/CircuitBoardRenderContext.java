package appUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import framework.CircuitBoard;
import framework.ExportGatesRunnable;
import framework.ExportedGate;
import utils.ResourceLoader;


public class CircuitBoardRenderContext {
	
	public static final int GATE_PIXEL_SIZE = 64;
	public static final int MARGIN1 = GATE_PIXEL_SIZE >> 1;
	public static final int MARGIN2 = GATE_PIXEL_SIZE >> 2;
	public static final int MARGIN3 = GATE_PIXEL_SIZE >> 3;
	public static final int MARGIN4 = GATE_PIXEL_SIZE >> 4;
	public static final int MARGIN5 = GATE_PIXEL_SIZE >> 5;
	
	public static final Polygon ARROW_HEAD = new Polygon(); 
	static {
		ARROW_HEAD.addPoint( 0, 0);
		ARROW_HEAD.addPoint( -4, -8);
		ARROW_HEAD.addPoint( 4,-8);
	}
	
	
	public static final int REGISTER_NUM_PADDING = 5;
	
	public static final BasicStroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.f}, 0.0f);
	public static final BasicStroke HEAVY = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final BasicStroke MEDIUM = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final BasicStroke BASIC = new BasicStroke(1);
	private static final BufferedImage DUMMY = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
	
	private Window w;
	private BufferedImage baseImage;
    private ArrayList<Integer> boardWidths = new ArrayList<>();
	private WidthScanner widthScanner = new WidthScanner();
	
	
	public static Rectangle2D getStringBounds(Font f, String text) {
		Graphics g = DUMMY.getGraphics();
		g.setFont(f);
		return g.getFontMetrics().getStringBounds(text, g);
	}
	
	public static void drawCenteredString(Graphics g, String text, int x, int y, int width, int height) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r2 = fm.getStringBounds(text, g);
		int xc = (int) (x + (width - r2.getWidth()) / 2);
		int yc = (int) (y + (height + r2.getHeight()) / 2 - fm.getDescent());
		g.drawString(text, xc, yc);
	}

	
	
	
	
	public CircuitBoardRenderContext(Window w) {
		this.w = w;
	}
	
	
	
	
	
	public BufferedImage renderBaseImage(boolean withGrid){
		CircuitBoard cb = w.getSelectedBoard();
        
		int gridWidth = widthScanner.scanBoard(cb);
		
        BufferedImage image = new BufferedImage(gridWidth * GATE_PIXEL_SIZE, cb.getRows() * GATE_PIXEL_SIZE, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,image.getWidth(),image.getHeight());
        
        g2d.setFont(ResourceLoader.VAST_SHADOW);
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	
    	ExportedGate.exportGates(cb, new ExportGatesRunnable() {
    		private int columnPixelPosition = 0;
    		private int columnWidth;
    		
			@Override
			public void gateExported(ExportedGate eg, int x, int y) {
				
        		x = columnPixelPosition;
        		y *= GATE_PIXEL_SIZE;

		        for(int i = 0; i < eg.getHeight(); i++){
					g2d.setColor(Color.BLACK);
        			drawIdentity(g2d, x, y + i * GATE_PIXEL_SIZE, columnWidth);
		        	if(withGrid) {
    					g2d.setColor(Color.LIGHT_GRAY);
    					drawGrid(g2d, columnPixelPosition, y + i * GATE_PIXEL_SIZE, columnWidth);
    				}
		        }
		        
		        g2d.setColor(Color.BLACK);
		        g2d.setStroke(BASIC);
		        
        		if(eg.getAbstractGate().getWidth() < columnWidth)
        			x += (columnWidth - eg.getAbstractGate().getWidth()) * MARGIN1;
        		
        		switch(eg.getAbstractGate().getType()) {
        		case I:
        			break;
                case CNOT:
                	drawCNOT(g2d, x, y, eg.getRegisters());
                	break;
                case TOFFOLI:
                	drawToffoli(g2d, x, y, eg.getRegisters());
                	break;
                case SWAP:
                	drawSWAP(g2d, x, y, eg.getRegisters());
                	break;
                case MEASURE:
                	drawMeasure(g2d, x, y);
                	break;
                default:
                	drawGate(g2d, x, y, eg);
                	break;
        		}
        		
        	}
			@Override
			public void nextColumnEvent(int column) {
				if(column > 0)
					columnPixelPosition += columnWidth * GATE_PIXEL_SIZE;
				columnWidth = getColumnWidth(column);
			}
			@Override
			public void columnEndEvent(int column) {}
		});
    	return image;
    }
	
	private void drawToffoli(Graphics2D g2d, int x, int y, int[] registers) {
		g2d.setStroke(HEAVY);
    	drawCNOTHead(g2d, x, registers[0]);
    	drawCNOTTail(g2d, x, registers[0], registers[1]);
    	drawCNOTTail(g2d, x, registers[0], registers[2]);
	}

	public static void drawArrow(Graphics2D g2d, int xi, int yi, int xf, int yf) {
		drawArrow(g2d, ARROW_HEAD, xi, yi, xf, yf);
	}
	
	public static void drawArrow(Graphics2D g2d, Polygon g, int xi, int yi, int xf, int yf) {
		AffineTransform af = new AffineTransform();
		AffineTransform saved = g2d.getTransform();
		af.setToIdentity();
		double angle = Math.atan2(yf-yi, xf-xi);
	    af.translate(xf, yf);
	    af.rotate((angle-Math.PI/2d));
		g2d.drawLine(xi, yi, xf, yf);
		g2d.setTransform(af);
		g2d.fill(g);
		g2d.setTransform(saved);
	}
	
	private void drawIdentity(Graphics2D g2d, int x, int y, int width){
		g2d.setStroke(BASIC);
		g2d.drawLine(x, y + MARGIN1, x + width * GATE_PIXEL_SIZE, y + MARGIN1);
	}
	
	private void drawGate(Graphics2D g2d, int x, int y, ExportedGate eg) {
        g2d.setStroke(BASIC);
        g2d.setFont(ResourceLoader.MPLUS);
        int offset = 0;
        int width = GATE_PIXEL_SIZE * eg.getAbstractGate().getWidth();
        int height = GATE_PIXEL_SIZE * eg.getHeight();
        g2d.setColor(Color.WHITE);
    	g2d.fillRect(x, y, width - 1, height - 1);
        g2d.setColor(Color.BLACK);
    	g2d.drawRect(x, y, width - 1, height - 1);
        
        if(eg.getRegisters().length > 1) {
	        for(int i = 0; i < eg.getRegisters().length; i++)
	        	g2d.drawString(Integer.toString(i), x + REGISTER_NUM_PADDING, (int) ((eg.getRegisters()[i] + .5d ) * GATE_PIXEL_SIZE));
	        Rectangle2D rect = getStringBounds(ResourceLoader.MPLUS, Integer.toString(eg.getRegisters().length - 1));
	        offset = (int) (2 * REGISTER_NUM_PADDING + rect.getWidth());
        }
        
	    g2d.setFont(ResourceLoader.VAST_SHADOW);
    	drawCenteredString(g2d, eg.getAbstractGate().getName(), x + offset, y, width - offset - 1, height - 1);
	}
	
	private void drawMeasure(Graphics2D g2d, int x, int y){
		g2d.setStroke(BASIC);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(x, y, GATE_PIXEL_SIZE - 1, GATE_PIXEL_SIZE - 1);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(x, y, GATE_PIXEL_SIZE - 1, GATE_PIXEL_SIZE - 1);
		g2d.drawArc(x + MARGIN3, y + MARGIN2, GATE_PIXEL_SIZE - MARGIN2 - 1, GATE_PIXEL_SIZE - MARGIN2 - 1, 0, 180);
    	g2d.fillOval(x + MARGIN1 - MARGIN5, y + MARGIN1 - MARGIN5 + MARGIN3, MARGIN4, MARGIN4);
		g2d.setStroke(MEDIUM);
		drawArrow(g2d, x + MARGIN1, y + MARGIN1 + MARGIN3, x + GATE_PIXEL_SIZE - MARGIN3, y + MARGIN3);
	}
	
	private void drawSWAP(Graphics2D g2d, int x, int y, int[] registers) {
		g2d.setStroke(HEAVY);
		drawSWAPHead(g2d, x, registers[0]);
		drawSWAPHead(g2d, x, registers[1]);
		g2d.drawLine(x + MARGIN1, registers[0] * GATE_PIXEL_SIZE + MARGIN1, x + MARGIN1, registers[1] * GATE_PIXEL_SIZE + MARGIN1);
	}
	
	public static void drawSWAPHead(Graphics2D g2d, int x, int boardRegister) {
		int y = boardRegister * GATE_PIXEL_SIZE;
		g2d.drawLine(x + MARGIN2, y + MARGIN2, x + GATE_PIXEL_SIZE - MARGIN2, y + GATE_PIXEL_SIZE - MARGIN2);
		g2d.drawLine(x + MARGIN2, y + GATE_PIXEL_SIZE - MARGIN2, x + GATE_PIXEL_SIZE - MARGIN2, y + MARGIN2);
	}
	
	private void drawCNOT(Graphics2D g2d, int x, int y, int[] registers) {
    	g2d.setStroke(HEAVY);
    	drawCNOTHead(g2d, x, registers[0]);
    	drawCNOTTail(g2d, x, registers[0], registers[1]);
	}
	
	
	public static void drawCNOTHead(Graphics2D g2d, int x, int boardRegister) {
		int centerX = x + MARGIN2;
    	int centerY = boardRegister * GATE_PIXEL_SIZE + MARGIN2;
    	g2d.drawLine(centerX, centerY + MARGIN2, centerX + MARGIN1, centerY + MARGIN2);
    	g2d.drawLine(centerX + MARGIN2, centerY, centerX + MARGIN2, centerY + MARGIN1);
    	g2d.drawOval(centerX,centerY, MARGIN1, MARGIN1);
	}
	
	public static void drawCNOTTail(Graphics2D g2d, int x, int bodyRegister, int controlRegister) {
    	g2d.fillOval(x + MARGIN1 - MARGIN4, controlRegister * GATE_PIXEL_SIZE + MARGIN1 - MARGIN4, MARGIN3, MARGIN3);
    	g2d.drawLine(x + MARGIN1, bodyRegister * GATE_PIXEL_SIZE + MARGIN1, x + MARGIN1, controlRegister * GATE_PIXEL_SIZE + MARGIN1);
	}
	
	private void drawGrid(Graphics2D g2d, int x, int y, int columnWidth) {
    	g2d.setStroke(DASHED);
    	g2d.draw(new RoundRectangle2D.Double(x, y, columnWidth * GATE_PIXEL_SIZE - 1, GATE_PIXEL_SIZE - 1, 10, 10));
	}
	
	
	
	
	public synchronized void paintRerenderedBaseImageOnly() {
		baseImage = renderBaseImage(true);
		w.getDisplay().setIcon(new ImageIcon(baseImage));
	}
	
	public BufferedImage getOverlay() {
		return new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public synchronized void removeOverlay() {
		w.getDisplay().setIcon(new ImageIcon(baseImage));
	}
	
	
	public synchronized void paintBaseImageWithOverlay(BufferedImage overlay) {
		BufferedImage baseImageWithOverlay = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = (Graphics2D) baseImageWithOverlay.getGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2d.drawImage(baseImage, 0, 0, baseImage.getWidth(), baseImage.getHeight(), null);
    	g2d.drawImage(overlay, 0, 0, overlay.getWidth(), overlay.getHeight(), null);
		w.getDisplay().setIcon(new ImageIcon(baseImageWithOverlay));
	}
	
	public int getColumnPixelPosition(int column) {
		int sum = 0;
		for(int i = 0; i < column; i++)
			sum += getColumnWidth(i) * GATE_PIXEL_SIZE;
		return sum;
	}
	
	public int[] getGridColumnPosition(int gridPixelX) {
		gridPixelX = (int) Math.floor((double)gridPixelX/GATE_PIXEL_SIZE);
		int column = 0;
		int columnWidth = getColumnWidth(0);
		int columnWithSum = 0;
		while(columnWithSum + columnWidth <= gridPixelX) {
			column++;
			columnWithSum += columnWidth;
			columnWidth = getColumnWidth(column);
		}
		return new int[]{column, columnWithSum * GATE_PIXEL_SIZE};
	}
	
	
	/**
	 * @param column
	 * @return
	 * the amount of grid spaces the specified column takes up on this {@link CircuitBoard}.
	 */
	public int getColumnWidth(int column) {
		return boardWidths.get(column);
	}
	

	private class WidthScanner implements ExportGatesRunnable{
		
		private int cummulativeWidth;
		private int largestWidth;
		
		public int scanBoard(CircuitBoard cb) {
			boardWidths.clear();
			cummulativeWidth = 0;
			largestWidth = 0;
			ExportedGate.exportGates(cb, this);
			return cummulativeWidth;
		}
		
		@Override
		public void gateExported(ExportedGate eg, int x, int y) {
			int width = eg.getAbstractGate().getWidth();
			if(width > largestWidth)
				largestWidth = width;
		}

		@Override
		public void nextColumnEvent(int column) {
			largestWidth = Integer.MIN_VALUE;
		}
		
		@Override
		public void columnEndEvent(int column) {
			boardWidths.add(column, largestWidth);
			cummulativeWidth += largestWidth;
		}
		
	}
}

