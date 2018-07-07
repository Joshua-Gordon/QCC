package framework;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import appUI.CircuitBoardRenderContext;
import appUI.CircuitBoardSelector;
import preferences.AppPreferences;
import utils.AppDialogs;

/**
 * Contains all data pertaining to a application project
 * such as the list of custom gates, custom oracles, and the list of instructions
 * for a quantum protocol represented by a grid of gates 
 * (A double {@link Arraylist} of {@link SolderedRegister}s)
 * <p>
 * This is main class to be serialized to save a user's project, however all methods pertaining to saving
 * an instance of a {@link CircuitBoard} as a project is contained within {@link CircuitBoardSelector}
 * 
 * @author quantumresearch
 *
 */
public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

	private transient URI fileLocation = null;
	private transient boolean unsaved = false;
	
    private ArrayList<ArrayList<SolderedRegister>> board;
    private ArrayList<Integer> boardWidths = new ArrayList<>();
    private DefaultListModel<AbstractGate> customGates = new DefaultListModel<>();
    private DefaultListModel<AbstractGate> customOracles = new DefaultListModel<>();
    
    
    /**
     * @return
     * an empty 4 by 5 {@link CircuitBoard}
     */
    public static CircuitBoard getDefaultCircuitBoard() {
    	CircuitBoard board = new CircuitBoard();
    	for(int i = 0; i < 5; ++i){
    		board.addRow();
    		board.addColumn();
        }
    	board.setSaved();
    	return board;
    }
    
    private CircuitBoard() {
        board = new ArrayList<>();
    }
    
    
    /**
     * Adds a row to the end of this {@link CircuitBoard}.
     */
    public void addRow() {
    	setUnsaved();
        for(ArrayList<SolderedRegister> a : board)
            a.add(SolderedRegister.identity());
    }
    
    /**
     * Adds a row at the specified location.
     * @param r
     */
    public void addRow(int r) {
    	if(r == getRows()) {
    		addRow();
    	}else{
    		for(ArrayList<SolderedRegister> a : board)
    			a.add(r, SolderedRegister.identity());
    	}
    }
    
    /**
     * Removes the last row of this {@link CircuitBoard}.
     */
    public void removeRow() {
    	if(board.get(0).size() > 1) {
	    	setUnsaved();
	        for(ArrayList<SolderedRegister> a : board)
	            a.remove(a.size() - 1);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.getWindow().getFrame());
    	}
    }
    
    /**
     * Adds a column to the end of this board.
     */
    public void addColumn(){
    	setUnsaved();
        board.add(new ArrayList<>());
        boardWidths.add(1);
        for(int i = 0; i < board.get(0).size(); ++i)
            board.get(board.size()-1).add(SolderedRegister.identity());
    }
    
    /**
     * Adds a column in the specified location.
     * @param c
     */
    public void addColumn(int c){
    	if(c == getColumns()) {
    		addColumn();
    	}else {
            boardWidths.add(1);
    		ArrayList<SolderedRegister> sr = new ArrayList<>();
        	for(int i = 0; i < board.get(0).size(); ++i)
        		sr.add(SolderedRegister.identity());
        	board.add(c, sr);
    	}
    }
    
    /**
     * Removes the last column of this {@link CircuitBoard}. If the {@link CircuitBoard} is
     * already one column, it will not reduce rows and a prompt will tell the user.
     */
    public void removeColumn(){
       	if(board.size() > 1) {
	    	setUnsaved();
	    	boardWidths.remove(board.size() - 1);
	        board.remove(board.size() - 1);
       	}else {
       		AppDialogs.couldNotRemoveColumn(Main.getWindow().getFrame());
       	}
    }

    
    /**
     * @return
     * the file location at where this {@link CircuitBoard} is stored on the hard drive.
     * It will return null if no location has been previously set.
     */
	public URI getFileLocation() {
		return fileLocation;
	}

	/**
	 * Sets the file location on the hard drive where this {@link CircuitBoard} was last stored.
	 * This is so that "Save" Action Command in {@link Keyboard} can be used to save file instead of
	 * "Save as". 
	 * <p>
	 * This method is used primarily in {@link CircuitBoardSelector}.
	 * @param fileLocation
	 */
	public void setFileLocation(URI fileLocation) {
		this.fileLocation = fileLocation;
	}
    
	
	/**
	 * Set's the {@link CircuitBoard} status to unsaved. Use this method every time an action has modified
	 * this instance of the {@link CircuitBoard} after it has last been saved to the hard drive.
	 * <p>
	 * 
	 * Every time the application closes, this ensures a prompt will notified that this instance has not been saved.
	 */
	public void setUnsaved() {
		unsaved = true;
	}
	
	/**
	 * Sets the {@link CircuitBoard} status to saved. Use this method directly after this instance of the {@link CircuitBoard}
	 * has been saved to the hard drive. This method is usually used in {@link CircuitBoardSelector}.
	 */
	public void setSaved() {
		unsaved = false;
	}
    
	/**
	 * @return
	 * whether or not this instance of the {@link CircuitBoard} has been edited since last time it has been saved to the hard drive.
	 */
	public boolean hasBeenEdited() {
		return unsaved;
	}
    
	
	/**
	 * Saves this instance of the {@link CircuitBoard} to Preferences. This is so that when this application opens again,
	 * this {@link CircuitBoard} will be loaded immediately. This is used on the the window closed event within {@link Window}.
	 */
    public void saveFileLocationToPreferences() {
    	if(fileLocation != null)
    		AppPreferences.put("File IO", "Previous File Location", new File(fileLocation).getAbsolutePath());
    	else
    		AppPreferences.put("File IO", "Previous File Location", null);
    }
    
    
    /**
     * @return
     * All the {@link CustomGates} associated with this {@link CircuitBoard}.
     */
	public DefaultListModel<AbstractGate> getCustomGates() {
		return customGates;
	}

	/**
	 * @return
	 * All the CustomOracles associated with this {@link CircuitBoard}.
	 */
	public DefaultListModel<AbstractGate> getCustomOracles() {
		return customOracles;
	}

	/**
	 * @return
	 * the name of the {@link CircuitBoard} on the hard drive.
	 */
	public String getName() {
    	if(fileLocation == null) {
    		return CircuitBoardSelector.UNSAVED_FILE_NAME;
    	}
    	File file = new File(fileLocation);
    	return file.getName();
    }
	
	
	/**
	 * @return
	 * the number of rows on this {@link CircuitBoard}.
	 */
	public int getRows() {
		return board.get(0).size();
	}
	
	/**
	 * @return
	 * the number of Columns on this {@link CircuitBoard}.
	 */
	public int getColumns() {
		return board.size();
	}
	
	/**
	 * @param column
	 * @param row
	 * @return
	 * the {@link SolderedRegister} at the specified row and column on this {@link CircuitBoard}.
	 */
	public SolderedRegister getSolderedRegister(int column, int row) {
		return board.get(column).get(row);
	}
	
	/**
	 * Sets the {@link SolderedRegister} at the specified row and column on this {@link CircuitBoard}.
	 * @param row
	 * @param column
	 * @param sr
	 */
	public void setSolderedRegister(int column, int row, SolderedRegister sr) {
		board.get(column).set(row, sr);
	}
	
	/**
	 * @param row
	 * @param column
	 * @return
	 * the {@link SolderedGate} associated with the {@link SolderedRegister} at the specified
	 * row and column of this {@link CircuitBoard}.
	 */
	public SolderedGate getSolderedGate(int row, int column) {
		return board.get(row).get(column).getSolderedGate();
	}
	
	/**
	 * @param column
	 * @return
	 * the amount of grid spaces the specified column takes up on this {@link CircuitBoard}.
	 */
	public int getColumnWidth(int column) {
		return boardWidths.get(column);
	}
	
	/**
	 * Sets the amount of grid spaces this specified column takes up on this {@link CircuitBoard}.
	 * @param column
	 * @param value
	 */
	public void setColumnWidth(int column, int value) {
		boardWidths.set(column, value);
	}
	
}
