import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Mouse implements MouseListener {
	
	private Window window;
	
	public Mouse(Window window) {
		this.window = window;
	}
	
	
    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Click");
    	JFrame frame = window.getFrame();
        Point p = frame.getMousePosition();
        p.x -= frame.getInsets().left;
        p.x += Main.w.getHorizontalOffset();
        p.y -= frame.getInsets().top;
        p.y += Main.w.getVerticalOffset();
        Main.cb.board.get(p.x/Gate.GATE_PIXEL_SIZE).get((p.y)/Gate.GATE_PIXEL_SIZE).type = Gate.GateType.Edit;
        Main.render();

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
