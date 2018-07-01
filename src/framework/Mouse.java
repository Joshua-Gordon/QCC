package framework;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import appUI.CircuitBoardRenderContext;

public class Mouse implements MouseListener {
	
    @Override
    public void mouseClicked(MouseEvent e) {
    	
        Point p = Main.w.getDisplay().getMousePosition();
        int grabRow = p.x/CircuitBoardRenderContext.GATE_PIXEL_SIZE;
        int grabColumn = (p.y)/CircuitBoardRenderContext.GATE_PIXEL_SIZE;
        DefaultGate gate = Main.cb.board.get(grabRow).get(grabColumn);
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
