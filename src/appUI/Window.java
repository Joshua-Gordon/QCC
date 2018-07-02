package appUI;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import framework.CircuitBoard;
import framework.SolderingGun;
import framework.Keyboard;
import framework.Main;
import framework.Mouse;
import preferences.AppPreferences;

public class Window extends WindowAdapter{

    public static final int WIDTH = 700;
    public static final int HEIGHT = 600;
    public static final String TITLE = "Quantum Circuit Board";
    
    private JFrame frame;
    private JScrollPane jsp;
    private JLabel display;
    private Keyboard keyboard;
    private ConsoleUI console;
    private GateChooserUI gateChooser;
    private JSplitPane consoleSplitPane;
    private JSplitPane gateChooserSplitPane;
    private SolderingGun gsg;
    private CircuitBoardSelector fileSelector;
    private CircuitBoard selectedBoard;
    private CircuitBoardRenderContext renderContext;
    
    public Window() {
        this.frame = new JFrame();
        this.keyboard = new Keyboard(this);
        this.console = new ConsoleUI(this);
        this.gateChooser = new GateChooserUI(this);
        this.gsg = new SolderingGun(this);
        this.fileSelector = new CircuitBoardSelector(this);
        this.display = new JLabel();
        this.renderContext = new CircuitBoardRenderContext(this);
        setSelectedBoard(fileSelector.loadPreviousCircuitBoard());
        
        frame.setSize(WIDTH,HEIGHT);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        // This Allows for prompting save before exiting application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addComponents();
    }
    
    
    
    private void addComponents() {
    	frame.setLayout(new BorderLayout());
        frame.add(new AppToolBar(), BorderLayout.NORTH);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(display, gbc);
        display.addMouseListener(new Mouse(this));
        jsp = new JScrollPane(panel);
        
        consoleSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        consoleSplitPane.setTopComponent(jsp);
        consoleSplitPane.setBottomComponent(console);
        consoleSplitPane.setResizeWeight(.7);
        consoleSplitPane.setDividerLocation(-100);
        
        gateChooserSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        gateChooserSplitPane.setLeftComponent(consoleSplitPane);
        gateChooserSplitPane.setRightComponent(gateChooser);
        gateChooserSplitPane.setResizeWeight(.7);
        gateChooserSplitPane.setDividerLocation(-100);
        
        
        
        frame.add(gateChooserSplitPane, BorderLayout.CENTER);
        
        console.changeVisibility(false);
        gateChooser.changeVisibility(false);
        
        frame.setJMenuBar(new AppMenuBar(this));
        frame.addWindowListener(this);
    }
    
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
        renderContext.paintRerenderedBaseImageOnly();
    }

    public boolean isActive() {
        return frame.isVisible();
    }

    public CircuitBoardRenderContext getRenderContext() {
		return renderContext;
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
    
    public GateChooserUI getGateChooser() {
		return gateChooser;
	}


	public JSplitPane getGateChooserSplitPane() {
		return gateChooserSplitPane;
	}


	public ConsoleUI getConsole() {
    	return console;
    }

	public JSplitPane getConsoleSplitPane() {
    	return consoleSplitPane;
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
    	if(selectedBoard.hasBeenEdited()) {
    		final int option = AppDialogs.closeWithoutSaving(frame, selectedBoard.getName());
    		if(option == 0) {
    			closeProgram();
    		}else if(option == 1) {
    			if(fileSelector.saveBoard(selectedBoard))
    				closeProgram();
    		}
    	}else {
    		closeProgram();
    	}
    }
    
    private void closeProgram() {
    	selectedBoard.saveFileLocationToPreferences();
    	AppPreferences.putBoolean("Opened Views", "Console", console.isVisible());
    	frame.dispose();
    	System.exit(0);
    }


	public SolderingGun getSolderingGun() {
		return gsg;
	}

	public CircuitBoardSelector getFileSelector() {
		return fileSelector;
	}


	public CircuitBoard getSelectedBoard() {
		return selectedBoard;
	}


	public void setSelectedBoard(CircuitBoard selectedBoard) {
		this.selectedBoard = selectedBoard;
		if(selectedBoard.getFileLocation() != null) {
			setTitle(new File(selectedBoard.getFileLocation()).getName());
		}else {
			setTitle(CircuitBoardSelector.UNSAVED_FILE_NAME);
		}
		gateChooser.updateListModels();
        renderContext.paintRerenderedBaseImageOnly();
	}
	
	public void updateSelectedBoardTitle() {
		if(selectedBoard.getFileLocation() != null) {
			setTitle(new File(selectedBoard.getFileLocation()).getName());
		}else {
			setTitle(CircuitBoardSelector.UNSAVED_FILE_NAME);
		}
	}

	public JLabel getDisplay() {
		return display;
	}
	
	
	
}
