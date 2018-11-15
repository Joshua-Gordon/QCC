package framework2FX;

import java.io.Serializable;

import framework2FX.gateModels.PresetModel;
import framework2FX.solderedGates.Solderable;
import framework2FX.solderedGates.SolderedGate;
import framework2FX.solderedGates.SolderedPin;
import framework2FX.solderedGates.SolderedRegister;
import utils.customCollections.ImmutableArray;
import utils.customCollections.eventTracableCollections.EventArrayList;
import utils.customCollections.eventTracableCollections.Notifier;

/**
 * This is a 2D grid of gates that represents a quantum protocol within design (often referred to as a sub-circuit or top-level) <br>
 * <p>
 * For a {@link CircuitBoard} to be used within a the application through the GUI, it must be added to <br>
 * a {@link Project} instance.
 * <p>
 * A {@link CircuitBoard} instance has two tiers: sub-circuit or top-level <br>
 * The top-level board is top-most 'module' of a quantum protocol. <br>
 * The top-level can be composed of other {@link CircuitBoard} instances; These instances are called sub-circuits.<br>
 * <p>
 * There can only be one top-level within a single {@link Project} instance, but there is no <br>
 * limit to the amount of sub-circuits within a {@link Project} instance.
 * <p>
 * 
 * For a {@link CircuitBoard} to be identified as a 
 * 
 * 
 * 
 * @author quantumresearch
 *
 */
public class CircuitBoard extends Solderable implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

    private Board board;
    
    // Notifies User-Interface of changes
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
    		a.add(r, mkIdent());
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
	    		 // detachSolderedGate(r, i);
	    		System.out.println("CircuitBoard Line 96"); // TODO: Fix this
	        for(BoardColumn a : board) 
	        	a.remove(r);
    	}else {
    		throw new InvalidRowException();
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
            column.add(mkIdent());
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
    		throw new InvalidColumnException();
    	}
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
	
	
	public void placeGate(Solderable gate, int column, int[] registers) {
		
	}
	
	public void removeGate(int row, int column) {
		
	}
	
	public SolderedGate getGateAt(int row, int column) {
		return board.get(column).get(row).getSolderedGate();
	}
	
	public SolderedPin getSolderPinAt(int row, int column) {
		return board.get(column).get(row);
	}
	
	
	@Override
	public int getNumberOfRegisters() {
		return getRows();
	}

	@Override
	public ImmutableArray<String> getArguments() {
		return null;
	}
	

	
	
	
	
	
	
	public void setBoard(Board board) {
		this.board = board;
		board.setReceiver(notifier);
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
	
	public static class BoardColumn extends EventArrayList<SolderedPin> {
		private static final long serialVersionUID = 6899202550938350451L;
		public BoardColumn() {
			super(null);
		}
	}
	
	@SuppressWarnings("serial")
	public static class InvalidRowException extends RuntimeException {
		public InvalidRowException () {
			super("Can not have less than 1 row");
		}
	}
	
	@SuppressWarnings("serial")
	public static class InvalidColumnException extends RuntimeException {
		public InvalidColumnException () {
			super("Can not have less than 1 column");
		}
	}
	
	private static SolderedRegister mkIdent() {
		return new SolderedRegister(new SolderedGate(PresetModel.IDENTITY.getModel()), 0);
	}
	
	
}