package appUI;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import framework.Keyboard;
import framework.Main;
import framework.Mouse;
import utils.ResourceLoader;

public class Window extends WindowAdapter{

    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    public static final String TITLE = "Quantum Circuit Board";
    
    private JFrame frame;
    private JScrollPane jsp;
    private JLabel display;
    private Keyboard keyboard;
    private ConsoleUI console = new ConsoleUI();
    private JSplitPane consoleSplitPane;
    
    public Window() {
        this.frame = new JFrame();
        setTitle(CircuitFileSelector.UNSAVED_FILE_NAME);
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        
        // This Allows for prompting save before exiting application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
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
        consoleSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        consoleSplitPane.setResizeWeight(.7);
        consoleSplitPane.setTopComponent(jsp);
        consoleSplitPane.setBottomComponent(console);
        consoleSplitPane.setEnabled(false);
        frame.add(consoleSplitPane, BorderLayout.CENTER);
//        frame.add(console, BorderLayout.SOUTH);
        frame.setJMenuBar(new AppMenuBar(this));
        frame.addWindowListener(this);
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
    
    public ConsoleUI getConsole() {
    	return console;
    }
    
    public JSplitPane getConsoleSplitPane() {
    	return consoleSplitPane;
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
    	if(Main.cb.hasBeenEdited()) {
    		final int option = AppDialogs.continueWithoutSaving(frame, Main.cb.getName());
    		if(option == 0) {
    			frame.dispose();
            	System.exit(0);
    		}else if(option == 1) {
    			if(CircuitFileSelector.saveBoard()) {
    				Main.cb.saveFileLocationToPreferences();
    				frame.dispose();
                	System.exit(0);
    			}
    		}
    	}else {
        	frame.dispose();
        	System.exit(0);
    	}
    }
    
    
}
