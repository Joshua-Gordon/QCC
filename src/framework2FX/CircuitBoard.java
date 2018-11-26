package framework2FX;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import framework2FX.gateModels.PresetModel;
import framework2FX.solderedGates.Solderable;
import framework2FX.solderedGates.SolderedGate;
import framework2FX.solderedGates.SolderedPin;
import framework2FX.solderedGates.SolderedRegister;
import framework2FX.solderedGates.SpacerPin;
import utils.customCollections.ImmutableArray;
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
	
	private final LinkedList<LinkedList<SolderedPin>> elements;
    
    // Notifies User-Interface of changes
    private Notifier notifier;
    private Hashtable<String, Integer> variableOcurrances;
    
    
    /**
     * @return
     * an empty 5 by 5 {@link CircuitBoard}
     */
    public static CircuitBoard getDefaultCircuitBoard() {
    	return new CircuitBoard(5, 5);
    }
    
    public CircuitBoard(int rows, int columns) {
    	notifier = new Notifier();
    	variableOcurrances = new Hashtable<>();
    	
    	
    	if (rows < 1)
			throw new InvalidRowException("rows cannot be less than 1");
		if(columns < 1)
			throw new InvalidColumnException("columns cannot be less than 1");
		
		this.elements = new LinkedList<>();
		
		LinkedList<SolderedPin> column;
		
		for(int c = 0; c < columns; c++) {
			column = new LinkedList<>();
			for(int r = 0; r < rows; r++)
				column.offerLast(mkIdent());
			elements.offerLast(column);
		}
    }
    
    
    
    public void addRow(int index, int amt) {
		if(index  == 0 || index == getRows() - 1) {
			ListIterator<SolderedPin> iterator;
			for(LinkedList<SolderedPin> column : elements) {
				iterator = column.listIterator(index);
				for(int i = 0; i < amt; i++)
					iterator.add(mkIdent());
			}
		} else {
			ListIterator<SolderedPin> iterator;
			
			SolderedPin spp, spc;
			SolderedGate sg;
			boolean isWithinGate;
			for(LinkedList<SolderedPin> column : elements) {
				iterator = column.listIterator(index-1);
				spp = iterator.next();
				spc = iterator.next();
				iterator.previous();
				
				if(spp.getSolderedGate() == spc.getSolderedGate()) {
					sg = spp.getSolderedGate();
					
					isWithinGate = spp.isWithinBody() && spc.isWithinBody();
					
					for(int i = 0; i< amt; i++)
						iterator.add(new SpacerPin(sg, isWithinGate));
					
				} else {
					for(int i = 0; i< amt; i++)
						iterator.add(mkIdent());
				}
			}
		}
	}
	
    
    public void removeRows(int firstIndex, int lastIndex) {
    	if(firstIndex < 0 || firstIndex >= getColumns())
			throw new InvalidColumnException("first arg must be a postive and less than the size");
		if(lastIndex < firstIndex || lastIndex >= getColumns())
			throw new InvalidColumnException("first arg must be a postive and less than the size");
		if(lastIndex - firstIndex + 1 == getRows())
			throw new InvalidColumnException("columns cannot be less than 1");
		// TODO: Finish this method
//		ListIterator<SolderedPin> iterator;
//		SolderedPin fp = null, lp = null, temp = null;
//		boolean firstHitReg, lastHitReg;
//		
//		for(LinkedList<SolderedPin> column : elements) {
//	    	iterator = column.listIterator(firstIndex);
//	    	if() {
//	    		
//	    	}
//	    	
//	    	fp = iterator.next();
//	    	iterator.remove();
//	    	hitRegister = fp instanceof SolderedRegister;
//	    	
//	    	for(int i = 0; i < lastIndex - firstIndex; i++) {
//	    		lp = iterator.next();
//	    		hitRegister |= lp instanceof SolderedRegister;
//	    		iterator.remove();
//	    	}
//	    	
//	    	
//	    	if(lp == null) {
//	    		
//	    	} else if(lp.getSolderedGate() == fp.getSolderedGate()) {
//	    		
//	    	} else {
//	    		
//	    	}
//		}
		
		
	}
    
    
	public void addColumns(int index, int amt) {
		ListIterator<LinkedList<SolderedPin>> iterator = elements.listIterator(index);
		LinkedList<SolderedPin> column;
		for(int i = 0; i < amt; i++) {
			column = new LinkedList<SolderedPin>();
			for(int r = 0; r < getRows() - 1; r++)
				column.add(mkIdent());
			iterator.add(column);
		}
	}
	
	public void removeColumns(int firstIndex, int lastIndex) {
		if(firstIndex < 0 || firstIndex >= getColumns())
			throw new InvalidColumnException("first arg must be a postive and less than the size");
		if(lastIndex < firstIndex || lastIndex >= getColumns())
			throw new InvalidColumnException("first arg must be a postive and less than the size");
		if(lastIndex - firstIndex + 1 == getColumns())
			throw new InvalidColumnException("columns cannot be less than 1");
		
		
		ListIterator<LinkedList<SolderedPin>> iterator = elements.listIterator(firstIndex);
		
		for(int i = 0; i < lastIndex - firstIndex + 1; i++) {
			iterator.next();
			iterator.remove();
		}
	}
	
	
	
	public int getRows() {
		return elements.getFirst().size();
	}
	
	
	
	public int getColumns() {
		return elements.size();
	}

	
	
	@SuppressWarnings("serial")
	public static class InvalidRowException extends RuntimeException {
		public InvalidRowException (String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class InvalidColumnException extends RuntimeException {
		public InvalidColumnException (String message) {
			super(message);
		}
	}
	
	private static SolderedRegister mkIdent() {
		return new SolderedRegister(new SolderedGate(PresetModel.IDENTITY.getModel()), 0);
	}
    
    
	
	public void placeGate(Solderable gate, int column, int[] registers, String ... parameters) {
		ImmutableArray<String> gateArgs = gate.getArguments();
		if(parameters.length != gateArgs.size()) {
			throw new RuntimeException("Can not be able to then");
		}
	}
	
	public void removeGate(int row, int column) {
		
	}
	
	
	public SolderedGate getGateAt(int row, int column) {
		return elements.get(column).get(row).getSolderedGate();
	}
	
	public SolderedPin getSolderPinAt(int row, int column) {
		return elements.get(column).get(row);
	}
	
	
	@Override
	public int getNumberOfRegisters() {
		return getRows();
	}

	@Override
	public ImmutableArray<String> getArguments() {
		Set<String> argSet = variableOcurrances.keySet();
		String[] args = new String[argSet.size()];
		int i = 0;
		for(String arg : argSet)
			args[i++] = arg;
		return new ImmutableArray<>(args);
	}
	
	public void setReciever(Notifier reciever) {
		this.notifier.setReceiver(reciever);
	}
	
	public Notifier getNotifier() {
		return notifier;
	}
	
}