package appUI;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

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
    private AppSplitPane consoleSplitPane;
    private AppSplitPane gateChooserSplitPane;
    private CircuitBoardSelector fileSelector;
    private CircuitBoard selectedBoard;
    private CircuitBoardRenderContext renderContext;
    private AppToolBar appToolBar;
    
    public Window() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.frame = new JFrame();
        this.keyboard = new Keyboard(this);
        this.console = new ConsoleUI(this);
        this.gateChooser = new GateChooserUI(this);
        this.fileSelector = new CircuitBoardSelector(this);
        this.display = new JLabel();
        this.renderContext = new CircuitBoardRenderContext(this);
        this.appToolBar = new AppToolBar(this);
        
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
        panel.add(appToolBar, BorderLayout.NORTH);
        panel.add(jsp, BorderLayout.CENTER);
        
        
        
        consoleSplitPane = new AppSplitPane(console, 
        		AppPreferences.getInt("View Attributes", "Console Divider Location"), JSplitPane.VERTICAL_SPLIT);
        consoleSplitPane.setTopComponent(panel);
        consoleSplitPane.setBottomComponent(console);
        consoleSplitPane.setResizeWeight(.7);
        
        gateChooserSplitPane = new AppSplitPane(gateChooser, 
        		AppPreferences.getInt("View Attributes", "Gate Chooser Divider Location"), JSplitPane.HORIZONTAL_SPLIT);
        gateChooserSplitPane.setLeftComponent(consoleSplitPane);
        gateChooserSplitPane.setRightComponent(gateChooser);
        gateChooserSplitPane.setResizeWeight(.7);
        
        frame.add(gateChooserSplitPane, BorderLayout.CENTER);
        
        console.setVisible(AppPreferences.getBoolean("Opened Views", "Console"));
        gateChooser.setVisible(AppPreferences.getBoolean("Opened Views", "Gate Chooser"));
        
        frame.setJMenuBar(new AppMenuBar(this));
        frame.addWindowListener(this);
    }
    
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public CircuitBoardRenderContext getRenderContext() {
		return renderContext;
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

	public ConsoleUI getConsole() {
    	return console;
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

	public AppToolBar getAppToolBar() {
		return appToolBar;
	}
	
	
	
	
}
