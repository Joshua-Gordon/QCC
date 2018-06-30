package appUI.customBorders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

@SuppressWarnings("serial")
public class MatrixBorder extends AbstractBorder{
	
	private static final int SIZE = 3;
	
	@Override
	public Insets getBorderInsets(Component c) {
		return (getBorderInsets(c, new Insets(SIZE, SIZE, SIZE, SIZE)));
	}
	
	@Override
    public Insets getBorderInsets(Component c, Insets insets){
        insets.left = insets.top = insets.right = insets.bottom = SIZE;
        return insets;
    }

    @Override
    public boolean isBorderOpaque(){
        return true;
    }
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		super.paintBorder(c, g, x, y, width, height);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y, SIZE, height);
		g.fillRect(x + width - SIZE, y, SIZE, height);
		g.fillRect(x + SIZE, y, SIZE, SIZE);
		g.fillRect(x + SIZE, y + height - SIZE, SIZE, SIZE);
		g.fillRect(x + width - 2 * SIZE, y, SIZE, SIZE);
		g.fillRect(x + width - 2 * SIZE, y + height - SIZE, SIZE, SIZE);
	}
}
