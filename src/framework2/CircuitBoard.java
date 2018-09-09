package framework2;

import java.io.Serializable;
import java.util.ArrayList;

import appUI.CircuitBoardSelector;
import framework.AbstractGate.GateType;
import framework.Main;
import framework.RegisterActionRunnable;
import framework.SolderedGate;
import framework.SolderedRegister;
import utils.AppDialogs;
import utils.EventArrayList;
import utils.Notifier;

/**
 * Contains all data pertaining to a subcircuit in the application project
 * such as the list of custom gates, custom oracles, and the list of instructions
 * for a quantum protocol represented by a grid of gates 
 * (A double {@link ArrayList} of {@link SolderedRegister}s)
 * <p>
 * This is the main class to be serialized to save a user's project, however all methods pertaining to saving
 * an instance of a {@link CircuitBoard} as a project is contained within {@link CircuitBoardSelector}
 * 
 * @author quantumresearch
 *
 */
public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

    private Board board;
    private Notifier notifier;
    
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
    	return board;
    }
    
    private CircuitBoard() {
    	notifier = new Notifier();
    	setBoard(new Board());
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
    	for(BoardColumn a : board)
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
	    	for(int i = 0; i < getColumns(); i++)
	    		detachSolderedGate(r, i);
	        for(BoardColumn a : board) 
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
    	int size = 0;
    	if(board.size() != 0)
    		size = board.get(0).size();
    	
    	BoardColumn column = new BoardColumn();
    	board.add(c, column);
    	for(int i = 0; i < size; ++i)
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
    		board.remove(c);
    	}else {
       		AppDialogs.couldNotRemoveColumn(Main.getWindow().getFrame());
    	}
    }

    
    
    /**
     * Detaches all {@link SolderedGate}s intersecting with the specified row range and column starting at <code> rowStart </code> and ending at <code> rowEnd </code> (inclusive).
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
     * @deprecated
     * Should use detachAllGatesWithinRange() method.
     * <p>
     * 
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
    @Deprecated
    public int detachSolderedGate(int row, int column) {
    	SolderedRegister sr = getSolderedRegister(column, row);
    	SolderedGate sg = sr.getSolderedGate();
    	int first = sg.getFirstLocalRegister();
    	int last  = sg.getLastLocalRegister();
    	
    	SolderedRegister currentRegister;
    	int y = row - 1;
    	if(sr.getLocalRegisterNumber() != first) {
	    	while(y >= 0 || throwGateBoundsException(sg)) {
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
    	while(y < getRows() || throwGateBoundsException(sg)) {
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
    @Deprecated
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
	 * @deprecated
	 * Should use the method getSolderedRegister()
	 * <p>
	 * 
	 * @param row
	 * @param column
	 * @return
	 * the {@link SolderedGate} associated with the {@link SolderedRegister} at the specified
	 * row and column of this {@link CircuitBoard}.
	 */
	@Deprecated
	public SolderedGate getSolderedGate(int row, int column) {
		return board.get(column).get(row).getSolderedGate();
	}
	
	
	/**
	 * Runs a specified {@link RegisterActionRunnable} to each {@link SolderedRegister} associated with the {@link SolderedGate} at the specified
	 * row and column of this {@link CircuitBoard}. If the selected row and column has an Identity {@link SolderedGate} but it is underneath a multi-Qubit 
	 * {@link SolderedGate}, then the multi-Qubit {@link SolderedGate} is chosen to have the {@link RegisterActionRunnable} run on in instead of the Identity.
	 * 
	 * <p>
	 * Note that there is no specific order in which the {@link SolderedRegister}s are chosen to run the action.
	 * @param row
	 * @param column
	 * @param rar
	 */
	public void runRegisterActionToSolderedGate(int row, int column, RegisterActionRunnable rar) {
		int rowOfSolderedRegister = isWithinAnotherGate(row, column);
		row = rowOfSolderedRegister == -1? row : rowOfSolderedRegister;
		SolderedRegister sr = getSolderedRegister(column, row);
		SolderedGate sg = sr.getSolderedGate();
		
		int first = sg.getFirstLocalRegister();
    	int last  = sg.getLastLocalRegister();
    	
    	SolderedRegister currentRegister;
    	int y = row - 1;
    	if(sr.getLocalRegisterNumber() != first) {
	    	while(y >= 0 || throwGateBoundsException(sg)) {
	    		currentRegister = getSolderedRegister(column, y);
	    		if(currentRegister.getSolderedGate().equals(sg)) {
	    			rar.registerScanned(y, column, currentRegister);
	    			if(currentRegister.getLocalRegisterNumber() == first)
	    				break;
	    		}
	    		y--;
	    	}
    	}
    	y = row;
    	while(y < getRows() || throwGateBoundsException(sg)) {
    		currentRegister = getSolderedRegister(column, y);
    		if(currentRegister.getSolderedGate().equals(sg)) {
    			rar.registerScanned(y, column, currentRegister);
    			if(currentRegister.getLocalRegisterNumber() == last)
    				break;
    		}
    		y++;
    	}
	}

	public void setBoard(Board board) {
		this.board = board;
		board.setReceiver(notifier);
	}

	/**
	 * Throws an {@link ArrayIndexOutOfBoundsException}.
	 * Used when scanning through a {@link SolderedGate} with missing {@link SolderedRegister}s
	 * @param sg
	 * @return
	 */
	public boolean throwGateBoundsException(SolderedGate sg) {
		try {
			throw new ArrayIndexOutOfBoundsException("Could not find all SolderedRegisters of " + sg.getAbstractGate().getName() + "!");
		}catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	
	
	

	
	
	
	
	
	
	
	
	public void setReciever(Notifier reciever) {
		this.notifier.setReceiver(reciever);
	}
	
	public Notifier getNotifier() {
		return notifier;
	}
	
	public static class Board extends EventArrayList<BoardColumn> {
		private static final long serialVersionUID = -4288685239350835141L;

		public Board() {
			super(null);
		}
		
		@Override
		public void add(BoardColumn value) {
			value.setReceiver(this);
			super.add(value);
		}
		
		@Override
		public void add(int index, BoardColumn value) {
			value.setReceiver(this);
			super.add(index, value);
		}
	}
	
	public static class BoardColumn extends EventArrayList<SolderedRegister> {
		private static final long serialVersionUID = 6899202550938350451L;
		public BoardColumn() {
			super(null);
		}
	}
	
}