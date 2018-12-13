package framework2FX;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import framework2FX.gateModels.PresetGateType;
import framework2FX.solderedGates.Solderable;
import framework2FX.solderedGates.SolderedControl;
import framework2FX.solderedGates.SolderedGate;
import framework2FX.solderedGates.SolderedPin;
import framework2FX.solderedGates.SolderedRegister;
import framework2FX.solderedGates.SpacerPin;
import utils.customCollections.CollectionUtils;
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
    
    
    public CircuitBoard(String name, String symbol, String description, int rows, int columns) {
    	super (name, symbol, description);
    	
    	notifier = new Notifier();
    	variableOcurrances = new Hashtable<>();
    	
    	
    	if (rows < 1)
			throw new IllegalArgumentException("rows cannot be less than 1");
		if(columns < 1)
			throw new IllegalArgumentException("columns cannot be less than 1");
		
		this.elements = new LinkedList<>();
		
		LinkedList<SolderedPin> column;
		
		for(int c = 0; c < columns; c++) {
			column = new LinkedList<>();
			for(int r = 0; r < rows; r++)
				column.offerLast(mkIdent());
			elements.offerLast(column);
		}
    }
    
    
    
    
    
    
    
    
    
    
    public void addRows(int index, int amt) {
    	if(amt < 0) throw new IllegalArgumentException("Amount must be positive");
    	if(amt == 0)
    		return;
    	
    	notifier.sendChange(this, "addRow", index, amt);
    	
    	
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
//			boolean isWithinGate;
			for(LinkedList<SolderedPin> column : elements) {
				iterator = column.listIterator(index-1);
				spp = iterator.next();
				spc = iterator.next();
				iterator.previous();
				
				if(spp.getSolderedGate() == spc.getSolderedGate()) {
					sg = spp.getSolderedGate();
					
//					isWithinGate = spp.isWithinBody() && spc.isWithinBody();
					
					for(int i = 0; i< amt; i++)
						iterator.add(new SpacerPin(sg));
					
				} else {
					for(int i = 0; i< amt; i++)
						iterator.add(mkIdent());
				}
			}
		}
	}
	
    
    
    
    
    
    
    
    
    
    public void removeRows(int firstIndex, int lastIndex) {
    	if(firstIndex < 0 || firstIndex > getRows())
			throw new IllegalArgumentException("first arg must be a postive and less than the size");
		if(lastIndex < firstIndex || lastIndex > getRows())
			throw new IllegalArgumentException("last arg must be a postive and less than the size");
		if(lastIndex - firstIndex == getRows())
			throw new IllegalArgumentException("rows cannot be less than 1");
		if(firstIndex == lastIndex)
			return;
		
		notifier.sendChange(this, "removeRows", firstIndex, lastIndex);
		
		for(LinkedList<SolderedPin> column : elements) {
			ListIterator<SolderedPin> iterator = column.listIterator(firstIndex);
			SolderedPin firstP = iterator.next();
			SolderedGate firstG = firstP.getSolderedGate();
			
			boolean hitFirstReg = firstP instanceof SolderedRegister;
			boolean hitLastReg = hitFirstReg;
			
			iterator.previous();
			
			
			SolderedGate lastG = firstG;
			while (iterator.nextIndex() != lastIndex) {
				SolderedPin current = iterator.next();
				
				if(current.getSolderedGate() != lastG) {
					if(lastG != firstG)
						removeVariableOccurances(lastG);
					lastG = current.getSolderedGate();
					hitLastReg = false;
				}
				
				if(current  instanceof SolderedRegister) {
					if(lastG == firstG)
						hitFirstReg = true;
					hitLastReg = true;
				}
				iterator.remove();
			}
			ListIterator<SolderedPin> copy = column.listIterator(iterator.nextIndex());
			removeBoundaryGates(firstG, lastG, hitFirstReg, hitLastReg, iterator, copy);
		}
	}
    
    
    
    
    
    
    
    
    
	public void addColumns(int index, int amt) {
		if(amt < 0) throw new IllegalArgumentException("Amount must be positive");
    	if(amt == 0)
    		return;
    	
    	notifier.sendChange(this, "addColumns", index, amt);
		
		
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
		if(firstIndex < 0 || firstIndex > getColumns())
			throw new IllegalArgumentException("first arg must be a postive and less than the size");
		if(lastIndex < firstIndex || lastIndex > getColumns())
			throw new IllegalArgumentException("first arg must be a postive and less than the size");
		if(lastIndex - firstIndex == getColumns())
			throw new IllegalArgumentException("columns cannot be less than 1");
		if(firstIndex == lastIndex)
			return;
		
		notifier.sendChange(this, "removeColumns", firstIndex, lastIndex);
		
		
		ListIterator<LinkedList<SolderedPin>> iterator = elements.listIterator(firstIndex);
		
		for(int i = 0; i < lastIndex - firstIndex; i++) {
			Iterator<SolderedPin> rowIterator = iterator.next().iterator();
			
			SolderedGate currentG = rowIterator.next().getSolderedGate();
			while(rowIterator.hasNext()) {
				SolderedPin currentP = rowIterator.next();
				if(currentP.getSolderedGate() != currentG) {
					removeVariableOccurances(currentG);
					currentG = currentP.getSolderedGate();
				}
			}
			removeVariableOccurances(currentG);
			
			iterator.remove();
		}
	}
	
	
	
	
	
	
	
	
	public int getRows() {
		return elements.getFirst().size();
	}
	
	
	
	
	
	
	
	
	public int getColumns() {
		return elements.size();
	}

	
	
	
	
    
    
	
	
	
	
	
	public void placeGate(Solderable gate, int column, Integer[] registers, String ... parameters) {
		if(column < 0 || column > getColumns())
			throw new IllegalArgumentException("Column should be greater than 0 and less than circuitboard column size");
		if(gate.getNumberOfRegisters() != registers.length)
			throw new IllegalArgumentException("\"registers\" argument should be the same length as register size defined in Gate Model");
		for(int reg : registers)
			if(reg < 0 || reg > getRows())
				throw new IllegalArgumentException("Register should be greater than 0 and less than circuitboard row size");
		
		ImmutableArray<String> gateArgs = gate.getArguments();
		if(parameters.length != gateArgs.size())
			throw new RuntimeException("Parameters not fulfilled");
		
		notifier.sendChange(this, "placeGate", gate, column, registers, parameters);
		
		
		SolderedGate toPlace = new SolderedGate(gate, parameters);
		
		ArrayList<Integer> localRegs = CollectionUtils.sortedListIndexes(registers);
		int firstGlobalReg = registers[(localRegs.get(0))];
		
		ListIterator<SolderedPin> iterator = elements.get(column).listIterator(firstGlobalReg);
		
		SolderedPin firstP = iterator.next();
		SolderedGate firstG = firstP.getSolderedGate();
		SolderedGate lastG = firstG;
		
		boolean hitFirstReg = firstP instanceof SolderedRegister;
		boolean hitLastReg = hitFirstReg;
		
		iterator.previous();
		
		int i = 0;
		
		while (iterator.hasNext()) {
			SolderedPin current = iterator.next();
			
			if(current.getSolderedGate() != lastG) {
				if(lastG != firstG)
					removeVariableOccurances(lastG);
				lastG = current.getSolderedGate();
				hitLastReg = false;
			}
			
			if(current  instanceof SolderedRegister) {
				if(lastG == firstG)
					hitFirstReg = true;
				hitLastReg = true;
			}
			
			if(registers[localRegs.get(i)] == iterator.previousIndex()) {
				iterator.set(new SolderedRegister(toPlace, localRegs.get(i)));
				if(++i == localRegs.size())
					break;
			} else {
				iterator.set(new SpacerPin(toPlace));
			}
		}
		ListIterator<SolderedPin> copy = elements.get(column).listIterator(iterator.nextIndex());
		removeBoundaryGates(firstG, lastG, hitFirstReg, hitLastReg, iterator, copy);
		addVariableOccurances(toPlace);
	}
	
	
	
//	private boolean findRecursion(CircuitBoard cb) {
//		
//	}
	
	
	
	public void removeGate(int row, int column) {
		if(row < 0 || row >= getRows())
			throw new IllegalArgumentException("first arg must be a postive and less than the size");
		if(column < 0|| column >= getColumns())
			throw new IllegalArgumentException("last arg must be a postive and less than the size");
		
		notifier.sendChange(this, "removeGate", row, column);
		
		ListIterator<SolderedPin> iterator = elements.get(column).listIterator(row);
		SolderedGate sg = iterator.next().getSolderedGate();
		
		iterator.previous();
		
		while(iterator.hasPrevious()) {
			SolderedPin sp = iterator.previous();
			if(sp.getSolderedGate() == sg)
				iterator.set(mkIdent());
			else break;
		}
		
		while(iterator.hasNext()) {
			SolderedPin sp = iterator.next();
			if(sp.getSolderedGate() == sg)
				iterator.set(mkIdent());
			else break;
		}
		removeVariableOccurances(sg);
	}
	
	
	
	
	
	
	
	public void placeControl(int rowControl, int rowGate, int column, boolean controlStatus) {
		if(rowControl < 0 || rowControl >= getRows())
			throw new IllegalArgumentException("row must be a postive and less than the size");
		if(rowGate < 0 || rowGate >= getRows())
			throw new IllegalArgumentException("row must be a postive and less than the size");
		if(column < 0|| column >= getColumns())
			throw new IllegalArgumentException("column must be a postive and less than the size");
		
		ListIterator<SolderedPin> iterator = elements.get(column).listIterator(rowControl);
		SolderedPin spc = iterator.next();
		SolderedGate sgc = spc.getSolderedGate();
		iterator.previous();
		
		SolderedGate sg = getGateAt(rowGate, column);
		
		if(sgc == sg) {
			if(spc instanceof SolderedRegister)
				throw new IllegalArgumentException("Gate cannot add control where a local register is currently present");
			iterator.set(new SolderedControl(sg, controlStatus));
		} else {
			SolderedGate currentGate = sgc;
			boolean remove = spc instanceof SolderedRegister;
			
			if(rowControl - rowGate > 0) {
				iterator.set(new SolderedControl(sg, controlStatus));
				
				while (iterator.hasPrevious()) {
					SolderedPin current = iterator.previous();
					
					if(current.getSolderedGate() != currentGate) {
						if(currentGate != sgc)
							removeVariableOccurances(currentGate);
						currentGate = current.getSolderedGate();
					}
					
					if(currentGate == sg)
						break;
					if(currentGate == sgc && current instanceof SolderedRegister)
						remove = true;
					iterator.set(new SpacerPin(sg));
				}
				
				removeBoundaryGates(currentGate, currentGate, false, remove, null, 
						elements.get(column).listIterator(rowControl));
			} else {
				iterator.next();
				iterator.set(new SolderedControl(sg, controlStatus));
				
				while (iterator.hasNext()) {
					SolderedPin current = iterator.next();
					
					if(current.getSolderedGate() != currentGate) {
						if(currentGate != sgc)
							removeVariableOccurances(currentGate);
						currentGate = current.getSolderedGate();
					}
					
					if(currentGate == sg)
						break;
					if(currentGate == sgc && current instanceof SolderedRegister)
						remove = true;
					iterator.set(new SpacerPin(sg));
				}
				
				removeBoundaryGates(currentGate, currentGate, remove, false, 
						elements.get(column).listIterator(rowControl), null);
			}
		}
	}
	
	
	
	public void removeControl(int row, int column) {
		if(row < 0 || row >= getRows())
			throw new IllegalArgumentException("row must be a postive and less than the size");
		if(column < 0|| column >= getColumns())
			throw new IllegalArgumentException("column must be a postive and less than the size");
		
		ListIterator<SolderedPin> iterator = elements.get(column).listIterator(row);
		SolderedPin sp = iterator.next();
		SolderedGate sg = sp.getSolderedGate();
		iterator.previous();
		
		if(!(sp instanceof SolderedControl))
			throw new IllegalArgumentException("There is no control at (row, column): (" + row + ", " + column + " )");
		
		boolean checkAbove, checkBelow;
		
		if(iterator.hasPrevious()) {
			SolderedPin prev = iterator.previous();
			checkAbove = prev.getSolderedGate() == sg;
			iterator.next();
		} else {
			checkAbove = false;
		}
		
		SolderedPin next = iterator.next();
		checkBelow = next.getSolderedGate() == sg;
		iterator.previous();
		
		if(checkAbove && checkBelow) {
			iterator.next();
			iterator.set(new SpacerPin(sg));
		} else if (checkAbove) {
			iterator.set(mkIdent());
			while (iterator.hasPrevious()) {
				SolderedPin prev = iterator.previous();
				if(prev instanceof SpacerPin)
					iterator.set(mkIdent());
				else
					break;
			}
			
		} else if (checkBelow) {
			while (iterator.hasNext()) {
				next = iterator.next();
				if(next instanceof SpacerPin)
					iterator.set(mkIdent());
				else
					break;
			}
		}
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
	
	
	private static SolderedRegister mkIdent() {
		return new SolderedRegister(SolderedGate.mkIdent(), 0);
	}
	
	private void addVariableOccurances(SolderedGate sg) {
		ImmutableArray<String> varsToAdd = sg.getParameterSet().getArguments();
		for(String s : varsToAdd) {
			Integer occurrances = variableOcurrances.get(s);
			if(occurrances == null)
				variableOcurrances.put(s, 1);
			else
				variableOcurrances.put(s, occurrances + 1);
		}
	}
	
	private void removeVariableOccurances(SolderedGate sg) {
		ImmutableArray<String> varsToAdd = sg.getParameterSet().getArguments();
		for(String s : varsToAdd) {
			Integer occurrances = variableOcurrances.get(s);
			
			if(occurrances == null)
				throw new RuntimeException("Variable \"" + s + "\" is not defined within this circuitboard and cannot be removed" );
			
			if(occurrances == 1)
				variableOcurrances.remove(s);
			else
				variableOcurrances.put(s, occurrances - 1);
		}
	}
	
	
	private void removeBoundaryGates (SolderedGate firstG, SolderedGate lastG, 
			boolean hitFirstReg, boolean hitLastReg, 
			ListIterator<SolderedPin> firstIt, ListIterator<SolderedPin> lastIt) {
		
		boolean diffGates = firstG != lastG;
		
		if(hitFirstReg) {
			while(firstIt.previousIndex() >= 0) {
				SolderedPin sp = firstIt.previous();
				if(sp.getSolderedGate() == firstG)					
					firstIt.set(mkIdent());
				else
					break;
			}
			removeVariableOccurances(firstG);
		} else if (diffGates && firstIt != null) {
			while(firstIt.previousIndex() >= 0) {
				SolderedPin sp = firstIt.previous();
				if(sp instanceof SpacerPin)					
					firstIt.set(mkIdent());
				else
					break;
			}
		}
		
		if(hitLastReg) {
			while(lastIt.previousIndex() < getRows()) {
				SolderedPin sp = lastIt.next();
				if(sp.getSolderedGate() == lastG)					
					lastIt.set(mkIdent());
				else
					break;
			}
			if(diffGates)
				removeVariableOccurances(lastG);
		} else if (diffGates) {
			while(lastIt.previousIndex() < getRows() && lastIt != null) {
				SolderedPin sp = lastIt.next();
				if(sp instanceof SpacerPin)					
					lastIt.set(mkIdent());
				else
					break;
			}
		}
	}
	
}