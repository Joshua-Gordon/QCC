package appUI;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import appTools.AppToolBar;
import framework.CircuitBoard;
import framework.Keyboard;
import preferences.AppPreferences;
import utils.AppDialogs;

public class Window extends WindowAdapter{
	
    public static final String TITLE = "Quantum Circuit Board";
    
    private JFrame frame;
    private JScrollPane jsp;
    private JLabel display;
    private Keyboard keyboard;
    private ConsoleUI console;
    private GateChooserUI gateChooser;
    private JSplitPane consoleSplitPane;
    private JSplitPane gateChooserSplitPane;
    private CircuitBoardSelector fileSelector;
    private CircuitBoard selectedBoard;
    private CircuitBoardRenderContext renderContext;
    
    public Window() {
        this.frame = new JFrame();
        this.keyboard = new Keyboard(this);
        this.console = new ConsoleUI(this);
        this.gateChooser = new GateChooserUI(this);
        this.fileSelector = new CircuitBoardSelector(this);
        this.display = new JLabel();
        this.renderContext = new CircuitBoardRenderContext(this);
        setSelectedBoard(fileSelector.loadPreviousCircuitBoard());
        
        frame.setSize(AppPreferences.getInt("View Attributes", "Window Width"),
        		AppPreferences.getInt("View Attributes", "Window Height"));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        // This allows for prompting save before exiting application
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addComponents();
    }
    
    
    
    private void addComponents() {
    	frame.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(display, gbc);
        jsp = new JScrollPane(panel);
        
        panel = new JPanel(new BorderLayout());
        panel.add(new AppToolBar(this), BorderLayout.NORTH);
        panel.add(jsp, BorderLayout.CENTER);
        
        
        
        consoleSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        consoleSplitPane.setTopComponent(panel);
        consoleSplitPane.setBottomComponent(console);
        consoleSplitPane.setResizeWeight(.7);
        consoleSplitPane.setDividerLocation(AppPreferences.getInt("View Attributes", "Console Divider Location"));
        
        gateChooserSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        gateChooserSplitPane.setLeftComponent(consoleSplitPane);
        gateChooserSplitPane.setRightComponent(gateChooser);
        gateChooserSplitPane.setResizeWeight(.7);
        gateChooserSplitPane.setDividerLocation(AppPreferences.getInt("View Attributes", "Gate Chooser Divider Location"));
        
        frame.add(gateChooserSplitPane, BorderLayout.CENTER);
        
        console.changeVisibility(AppPreferences.getBoolean("Opened Views", "Console"));
        gateChooser.changeVisibility(AppPreferences.getBoolean("Opened Views", "Gate Chooser"));
        
        frame.setJMenuBar(new AppMenuBar(this));
        frame.addWindowListener(this);
    }
    
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
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
    	AppPreferences.putBoolean("Opened Views", "Gate Chooser", gateChooser.isVisible());
    	AppPreferences.putInt("View Attributes", "Window Width", frame.getWidth());
    	AppPreferences.putInt("View Attributes", "Window Height", frame.getHeight());
    	AppPreferences.putInt("View Attributes", "Console Divider Location", consoleSplitPane.getDividerLocation());
    	AppPreferences.putInt("View Attributes", "Gate Chooser Divider Location", gateChooserSplitPane.getDividerLocation());
    	frame.dispose();
    	System.exit(0);
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
