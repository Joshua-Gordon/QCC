package appUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import framework.Keyboard;
import framework.Mouse;

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
        setTitle(CircuitFileSelector.UNSAVED_FILE_NAME);
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display = new JLabel();
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(display, gbc);
        display.addMouseListener(new Mouse());
        
        jsp = new JScrollPane(panel);
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
    
    public JLabel getDisplay() {
    	return display;
    }
    
    public void setTitle(String fileName) {
    	frame.setTitle(TITLE + " - " + fileName);
    }
}
