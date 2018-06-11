import javax.swing.*;

import java.awt.Frame;
import java.awt.image.BufferedImage;

public class Window {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    public static final String TITLE = "Quantum Circuit Board";
    
    private JFrame frame;
    private JScrollPane jsp;
    private JLabel display;
    private Keyboard keyboard;

    public Window() {
        this.frame = new JFrame();
        setTitle("Untitled");
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display = new JLabel();
        jsp = new JScrollPane(display);
        jsp.addMouseListener(new Mouse(this));
        this.keyboard = new Keyboard();
        frame.add(jsp);
        frame.setJMenuBar(new AppMenuBar(this));
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

    public Keyboard getKeyboard() {
    	return keyboard;
    }
    
    public void setTitle(String fileName) {
    	frame.setTitle(TITLE + " - " + fileName);
    }
}
