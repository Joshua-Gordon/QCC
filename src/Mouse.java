import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Mouse implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Click");
        Point p = e.getLocationOnScreen();
        p.x += Main.w.getHorizontalOffset();
        Main.cb.board.get(p.x/Gate.GATE_PIXEL_SIZE).get((p.y-32)/Gate.GATE_PIXEL_SIZE).type = Gate.GateType.Edit;

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
