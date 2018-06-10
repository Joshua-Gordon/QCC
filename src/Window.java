import javax.swing.*;
import java.awt.image.BufferedImage;

public class Window {

    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    private JFrame frame;
    private JScrollPane jsp;
    private JLabel display;

    public Window() {
        this.frame = new JFrame("Qubits? Maybe.");
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display = new JLabel();
        jsp = new JScrollPane(display);
        jsp.addMouseListener(new Mouse(this));
        frame.addKeyListener(new Keyboard());
        frame.add(jsp);
        jsp.getVerticalScrollBar().getValue();
    }

    public void init() {
        frame.setVisible(true);
    }

    public boolean isActive() {
        return frame.isVisible();
    }

    public void display(BufferedImage bi) {
        display.setIcon(new ImageIcon(bi));
    }

    public int getHorizontalOffset(){
        return jsp.getHorizontalScrollBar().getValue();
    }
    
    public int getVerticalOffset(){
        return jsp.getVerticalScrollBar().getValue();
    }
    
    public JFrame getFrame() {
    	return frame;
    }

}
