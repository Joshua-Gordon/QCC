import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Mouse implements MouseListener {
	
    @Override
    public void mouseClicked(MouseEvent e) {
    	
        Point p = Main.w.getDisplay().getMousePosition();
//        p.x += Main.w.getHorizontalOffset();
//        p.y += Main.w.getVerticalOffset();
        int grabRow = p.x/Gate.GATE_PIXEL_SIZE;
        int grabColumn = (p.y)/Gate.GATE_PIXEL_SIZE;
        Main.cb.board.get(grabRow).get(grabColumn).type = Gate.GateType.Edit;
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
