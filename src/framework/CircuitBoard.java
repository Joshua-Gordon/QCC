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

public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

	private transient URI fileLocation = null;
	private transient boolean unsaved = false;
	
    private ArrayList<ArrayList<SolderedRegister>> board;
    private ArrayList<Integer> boardWidths = new ArrayList<>();
    private DefaultListModel<AbstractGate> customGates = new DefaultListModel<>();
    private DefaultListModel<AbstractGate> customOracles = new DefaultListModel<>();

//    public static EnumMap<DefaultGate.GateType,Supplier<DefaultGate>> gatemap;

    //initialize gatemap
//	static {
//    	gatemap = new EnumMap<DefaultGate.GateType, Supplier<DefaultGate>>(DefaultGate.GateType.class);
//        gatemap.put(DefaultGate.GateType.I,DefaultGate::identity);
//        gatemap.put(DefaultGate.GateType.H,DefaultGate::hadamard);
//        gatemap.put(DefaultGate.GateType.X,DefaultGate::x);
//        gatemap.put(DefaultGate.GateType.Y,DefaultGate::y);
//        gatemap.put(DefaultGate.GateType.Z,DefaultGate::z);
//        gatemap.put(DefaultGate.GateType.MEASURE,DefaultGate::measure);
//        gatemap.put(DefaultGate.GateType.CNOT,DefaultGate::cnot);
//        gatemap.put(DefaultGate.GateType.SWAP,DefaultGate::swap);
//    }

    //Empty 5x5 board
    
    
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
    
    
    
    public void addRow() {
    	setUnsaved();
        for(ArrayList<SolderedRegister> a : board)
            a.add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    }
    
    public void addRow(int r) {
    	if(r == getRows()) {
    		addRow();
    	}else{
    		for(ArrayList<SolderedRegister> a : board)
    			a.add(r, new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    	}
    }
    
    public void removeRow() {
    	if(board.get(0).size() > 1) {
	    	setUnsaved();
	        for(ArrayList<SolderedRegister> a : board)
	            a.remove(a.size() - 1);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.getWindow().getFrame());
    	}
    }
    
    public void addColumn(){
    	setUnsaved();
        board.add(new ArrayList<>());
        boardWidths.add(1);
        for(int i = 0; i < board.get(0).size(); ++i)
            board.get(board.size()-1).add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    }
    
    public void addColumn(int c){
    	if(c == getColumns()) {
    		addColumn();
    	}else {
            boardWidths.add(1);
    		ArrayList<SolderedRegister> sr = new ArrayList<>();
        	for(int i = 0; i < board.get(0).size(); ++i)
        		sr.add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
        	board.add(c, sr);
    	}
    }
    
    public void removeColumn(){
       	if(board.size() > 1) {
	    	setUnsaved();
	    	boardWidths.remove(board.size() - 1);
	        board.remove(board.size() - 1);
       	}else {
       		AppDialogs.couldNotRemoveColumn(Main.getWindow().getFrame());
       	}
    }

	//Takes all selected gates and sets them to type g
//    public void edit(AbstractGate.GateType g) {
//    	mutate();
//        for(int x = 0; x < board.size(); ++x) {
//            for(int y = 0; y < board.get(0).size(); ++y) {
//            	AbstractGate gate = board.get(x).get(y);
//                if(gate.isSelected()) {
//                	AbstractGate newGate = gatemap.get(g).get();
//                    if(newGate != null) {
//                    	board.get(x).set(y,newGate);
//                    }
//                }
//            }
//        }
//        Main.render();
//    }

    
    
    
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
