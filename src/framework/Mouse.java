package framework;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Mouse implements MouseListener {
	
    @Override
    public void mouseClicked(MouseEvent e) {
    	
        Point p = Main.w.getDisplay().getMousePosition();
        int grabRow = p.x/Gate.GATE_PIXEL_SIZE;
        int grabColumn = (p.y)/Gate.GATE_PIXEL_SIZE;
        Gate gate = Main.cb.board.get(grabRow).get(grabColumn);
        gate.setSelected(!gate.isSelected());
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
