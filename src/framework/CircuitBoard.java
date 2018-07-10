package framework;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.*;

import appUI.CircuitBoardSelector;
import appUI.Window;
import framework.AbstractGate.GateType;
import preferences.AppPreferences;
import utils.AppDialogs;

/**
 * Contains all data pertaining to a application project
 * such as the list of custom gates, custom oracles, and the list of instructions
 * for a quantum protocol represented by a grid of gates 
 * (A double {@link ArrayList} of {@link SolderedRegister}s)
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
    private DefaultListModel<AbstractGate> customGates = new DefaultListModel<>();
    private DefaultListModel<AbstractGate> customOracles = new DefaultListModel<>();
    
    
    /**
     * @return
     * an empty 5 by 5 {@link CircuitBoard}
     */
    public static CircuitBoard getDefaultCircuitBoard() {
    	CircuitBoard board = new CircuitBoard();
    	for(int i = 0; i < 5; ++i){
    		board.addColumn();
    		board.addRow();
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
    	addRow(getRows());
    }
    
    /**
     * Adds a row at the specified location.
     * <p>
     * @param r
     */
    public void addRow(int r) {
    	setUnsaved();
    	for(ArrayList<SolderedRegister> a : board)
    		a.add(r, SolderedRegister.identity());
    }
    
    
    
    /**
     * Removes the last row of this {@link CircuitBoard}.
     */
    public void removeRow() {
    	removeRow(getRows() - 1);
    }
    
    /**
     * Removes the specified row "r" from this {@link CircuitBoard}
     * @param r
     */
    public void removeRow(int r){
    	if(board.get(0).size() > 1) {
	    	setUnsaved();
	    	for(int i = 0; i < getColumns(); i++)
	    		detachSolderedGate(r, i);
	        for(ArrayList<SolderedRegister> a : board) 
	        	a.remove(r);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.getWindow().getFrame());
    	}
    }
    
    
    /**
     * Adds a column to the end of this board.
     */
    public void addColumn(){
        addColumn(getColumns());
    }
    
    /**
     * Adds a column in the specified location.
     * @param c
     */
    public void addColumn(int c){
    	setUnsaved();
    	ArrayList<SolderedRegister> column = new ArrayList<>();
    	board.add(c, column);
    	for(int i = 0; i < board.get(0).size(); ++i)
            column.add(SolderedRegister.identity());
    }
    
    /**
     * Removes the last column of this {@link CircuitBoard}. If the {@link CircuitBoard} is
     * already one column, it will not reduce rows and a prompt will tell the user.
     */
    public void removeColumn(){
    	removeColumn(getColumns() - 1);
    }
    
    /**
     * Removes the specified column "c" of this {@link CircuitBoard}
     * @param c
     */
    public void removeColumn(int c) {
    	if(board.size() > 1) {
    		setUnsaved();
    		board.remove(c);
    	}else {
       		AppDialogs.couldNotRemoveColumn(Main.getWindow().getFrame());
    	}
    }

    
    
    /**
     * Detaches all {@link SolderedGate}s intersecting with the specified rows and column starting at <code> rowStart </code> and ending at <code> rowEnd </code> (inclusive).
     * If the range happens to be completely enclosed within another {@link SolderedGate}, that {@link SolderedGate} will also be detached.
     * 
     * @param rowStart
     * @param rowEnd
     */
    public void detachAllGatesWithinRange(int rowStart, int rowEnd, int column) {
    	int row = isWithinAnotherGate(rowStart, column);
		
    	if(row != -1)
    		detachSolderedGate(row, column);
    	
		for(int i = rowStart + 1; i <= rowEnd; i++)
			i = detachSolderedGate(i, column);
    }
    
    /**
     * Replaces the {@link SolderedGate} associated with the {@link SolderedRegister} with an identity gate on the {@link CircuitBoard}
     * at a specified row and column.
     * <p>
     * If a {@link SolderedGate} spans multiple rows, all {@link SolderedRegister}'s associated with the {@link SolderedGate} will also
     * be replaced with an identity gate.
     * <p>
     * <b> NOTE: </b> This does not detach any {@link SolderedGate}s that happen to completely enclose the specified row and column.
     * 
     * 
     * @param row
     * @param column
     * 
     * @return
     * the row of the last {@link SolderedRegister} removed from {@link CircuitBoard}
     */
    public int detachSolderedGate(int row, int column) {
    	SolderedRegister sr = getSolderedRegister(column, row);
    	SolderedGate sg = sr.getSolderedGate();
    	int first = sg.getFirstLocalRegister();
    	int last  = sg.getLastLocalRegister();
    	
    	SolderedRegister currentRegister;
    	int y = row - 1;
    	if(sr.getLocalRegisterNumber() != first) {
	    	while(y >= 0) {
	    		currentRegister = getSolderedRegister(column, y);
	    		if(currentRegister.getSolderedGate().equals(sg)) {
	    			setSolderedRegister(column, y, SolderedRegister.identity());
	    			if(currentRegister.getLocalRegisterNumber() == first)
	    				break;
	    		}
	    		y--;
	    	}
    	}
    	y = row;
    	while(y < getRows()) {
    		currentRegister = getSolderedRegister(column, y);
    		if(currentRegister.getSolderedGate().equals(sg)) {
    			setSolderedRegister(column, y, SolderedRegister.identity());
    			if(currentRegister.getLocalRegisterNumber() == last)
    				break;
    		}
    		y++;
    	}
    	return y;
    }
    
    /**
     * Checks whether or not the the specified row and column is within the bounds of another {@link SolderedGate} other than identity.
     * 
     * @param row
     * @param column
     * @return
     * if contained within another {@link SolderedGate}, it will return a row containing a {@link SolderedRegister} attributed to the surrounding
     * gate, otherwise it return -1.
     */
    public int isWithinAnotherGate(int row, int column) {
    	int y = row;
		while(y >= 0) {
			SolderedRegister sr = getSolderedRegister(column, y);
			SolderedGate sg0 = sr.getSolderedGate();
			if(sg0.getAbstractGate().getType() != GateType.I) {
				if(sg0.getLastLocalRegister() != sr.getLocalRegisterNumber() || y == row)
					return y;
				return -1;
			}
			y--;
		}
		return -1;
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
	 * Sets the {@link CircuitBoard} status to unsaved. Use this method every time an action has modified
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
     * All the {@link CustomGate}s associated with this {@link CircuitBoard}.
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
		return board.get(column).get(row).getSolderedGate();
	}
	

	public void setGates(ArrayList<ArrayList<SolderedRegister>> gates) {
		this.board = gates;
	}

}
