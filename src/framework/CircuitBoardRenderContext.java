package framework;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utils.ResourceLoader;


public class CircuitBoardRenderContext {
	
		
	private static void drawCenteredString(Graphics g, String text, int x, int y, int width, int height) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r2 = fm.getStringBounds(text, g);
		int xc = (int) (x + (width - r2.getWidth()) / 2);
		int yc = (int) (y + (height + r2.getHeight()) / 2 - fm.getDescent());
		g.drawString(text, xc, yc);
	}
	
	private static final BasicStroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.f}, 0.0f);
	private static final BasicStroke HEAVY = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final BasicStroke BASIC = new BasicStroke(1);
	@SuppressWarnings("incomplete-switch")
	public static BufferedImage render(CircuitBoard circuitBoard){
		ArrayList<ArrayList<Gate>> board = circuitBoard.board;
        int unit = Gate.GATE_PIXEL_SIZE;
        BufferedImage image = new BufferedImage(board.size()*unit, board.get(0).size()*unit,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,image.getWidth(),image.getHeight());
        g.setFont(ResourceLoader.VAST_SHADOW);
        Graphics2D g2d = (Graphics2D) g;
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                g.setColor(Color.LIGHT_GRAY);
                g2d.setStroke(DASHED);
                g2d.draw(new RoundRectangle2D.Double(x*unit,y*unit,unit,unit, 10, 10));
                g.drawRect(x*unit,y*unit,unit-1,unit-1);
                g2d.setStroke(BASIC);
                Gate gate = board.get(x).get(y);
                switch(gate.type){
                    case I:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + 32,(x+1)*unit,y*unit + 32);
                        break;
                    case H:
                    	g.setColor(Color.BLACK);
                    	g.drawRect(x*unit, y*unit, unit-1, unit-1);
                        drawCenteredString(g, "H", x*unit, y*unit, unit, unit);
                        break;
                    case X:
                    	g.setColor(Color.BLACK);
                    	g.drawRect(x*unit, y*unit, unit-1, unit-1);
                        drawCenteredString(g, "X", x*unit, y*unit, unit, unit);
                        break;
                    case Y:
                    	g.setColor(Color.BLACK);
                    	g.drawRect(x*unit, y*unit, unit-1, unit-1);
                        drawCenteredString(g, "Y", x*unit, y*unit, unit, unit);
                        break;
                    case Z:
                    	g.setColor(Color.BLACK);
                    	g.drawRect(x*unit, y*unit, unit-1, unit-1);
                        drawCenteredString(g, "Z", x*unit, y*unit, unit, unit);
                        break;
                    case MEASURE:
                    	g.setColor(Color.BLACK);
                    	g.drawRect(x*unit, y*unit, unit-1, unit-1);
                        drawCenteredString(g, "M", x*unit, y*unit, unit, unit);
                        break;
                    case CNOT:
                        g.setColor(Color.DARK_GRAY);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                    	g2d.setStroke(HEAVY);
                        int len = board.get(x).get(y).length;
                        int tx = 1;
                        if(len < 0)
                        	tx = -1;
                        g2d.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+len)*unit + (unit>>1) + tx * (unit>>2));
                        g2d.drawLine(x*unit + (unit>>1) - (unit>>2), (y+len)*unit + (unit>>1), (x)*unit + (unit>>1) + (unit>>2), (y+len)*unit + (unit>>1));
                        g2d.fillOval(x*unit + unit / 2 - 4, y*unit +unit / 2 - 4, 8, 8);
                        int centerX = x*unit + (unit>>2);
                        int centerY = (y+len)*unit + (unit>>2);
                        g2d.drawOval(centerX,centerY,unit>>1,unit>>1);
                        g2d.setStroke(BASIC);
                        break;
                    case SWAP:
                    	g.setColor(Color.DARK_GRAY);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        //Diagonal lines
                    	g2d.setStroke(HEAVY);
                        g2d.drawLine(x*unit + (unit>>2),y*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+1)*unit - (unit>>2));
                        g2d.drawLine(x*unit + (unit>>2),(y+1)*unit - (unit>>2),(x+1)*unit - (unit >> 2), y*unit + (unit>>2));
                        int swaplen = board.get(x).get(y).length;
                        g2d.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+swaplen)*unit + (unit>>1));
                        //More diagonal lines
                        g2d.drawLine(x*unit + (unit>>2),(y+swaplen)*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen+1)*unit - (unit>>2));
                        g2d.drawLine(x*unit + (unit>>2),(y+1+swaplen)*unit - (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen)*unit + (unit>>2));
                        g2d.setStroke(BASIC);
                        break;
                }
                if(gate.isSelected()) {
                	g.setColor(Color.RED);
                    g.drawRect(x*unit,y*unit,unit-1,unit-1);
                }
            }
        }
        return image;
    }	
	
}
