package framework;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import appUI.Window;

public class Mouse implements MouseListener, MouseMotionListener {
	private Window window;
	
	public Mouse(Window window) {
		this.window = window;
	}
	
	
    @Override
    public void mouseClicked(MouseEvent e) {
    	

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


	@Override
	public void mouseDragged(MouseEvent e) {
		
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if(window.getGateChooser().isSelected()) {
			
		}
		
	}
}
