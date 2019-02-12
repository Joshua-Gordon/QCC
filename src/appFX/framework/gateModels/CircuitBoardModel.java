package appFX.framework.gateModels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import appFX.appUI.AppAlerts;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.UserDefinitions.DefinitionEvaluatorException;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.solderedGates.SolderedControl;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import appFX.framework.solderedGates.SpacerPin;
import utils.customCollections.CollectionUtils;
import utils.customCollections.CustomLinkedList;
import utils.customCollections.Manifest;
import utils.customCollections.Manifest.ManifestObject;
import utils.customCollections.eventTracableCollections.Notifier;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

/**
 * This is a 2D grid of gates that represents a quantum protocol within design (often referred to as a sub-circuit or top-level) <br>
 * <p>
 * For a {@link CircuitBoardModel} to be used within a the application through the GUI, it must be added to <br>
 * a {@link Project} instance.
 * <p>
 * A {@link CircuitBoardModel} instance has two tiers: sub-circuit or top-level <br>
 * The top-level board is top-most 'module' of a quantum protocol. <br>
 * The top-level can be composed of other {@link CircuitBoardModel} instances; These instances are called sub-circuits.<br>
 * <p>
 * There can only be one top-level within a single {@link Project} instance, but there is no <br>
 * limit to the amount of sub-circuits within a {@link Project} instance.
 * <p>
 * 
 * For a {@link CircuitBoardModel} to be identified as a 
 * 
 * 
 * 
 * @author quantumresearch
 *
 */
public class CircuitBoardModel extends GateModel implements  Iterable<RawExportableGateData> {
	private static final long serialVersionUID = -6921131331890897905L;
	
	private final CustomLinkedList<CustomLinkedList<SolderedPin>> elements;
    
	public static final String CIRCUIT_BOARD_EXTENSION =  "cb";
	
	
    // Notifies User-Interface of changes
    private final Notifier notifier;
    private final Notifier renderNotifier;
    
    private final Manifest<String> circuitBoardsUsed;
    private final Manifest<String> defaultGatesUsed;
    private final Manifest<String> presetGatesUsed;
    private final Manifest<String> oraclesUsed;
    
    private CircuitBoardModel(String name, String symbol, String description, String[] arguments, CircuitBoardModel oldModel) {
    	this(name, symbol, description, arguments, oldModel.elements, oldModel.circuitBoardsUsed, 
    			oldModel.defaultGatesUsed, oldModel.presetGatesUsed, oldModel.oraclesUsed);
    }
    
    private CircuitBoardModel(String name, String symbol, String description, String[] arguments, CustomLinkedList<CustomLinkedList<SolderedPin>> elements,
    		Manifest<String> circuitBoardsUsed, Manifest<String> defaultGatesUsed, Manifest<String> presetGatesUsed, Manifest<String> oraclesUsed) {
    	super(name, symbol, description, arguments);
    	
    	this.elements = elements;
    	
    	this.circuitBoardsUsed = circuitBoardsUsed;
    	this.defaultGatesUsed = defaultGatesUsed;
    	this.presetGatesUsed = presetGatesUsed;
    	this.oraclesUsed = oraclesUsed;
    	
    	this.notifier = new Notifier();
    	this.renderNotifier = new Notifier();
    }
    
    
    public CircuitBoardModel(String name, String symbol, String description, int rows, int columns, String ... arguments) {
    	super (name, symbol, description, arguments);
    	
    	circuitBoardsUsed = new Manifest<>();
        defaultGatesUsed = new Manifest<>();
        presetGatesUsed = new Manifest<>();
        oraclesUsed = new Manifest<>();
    	
    	if (rows < 1)
			throw new IllegalArgumentException("Rows cannot be less than 1");
		if(columns < 1)
			throw new IllegalArgumentException("Columns cannot be less than 1");
		
		this.notifier = new Notifier();
    	this.renderNotifier = new Notifier();
		this.elements = new CustomLinkedList<>();
		
		CustomLinkedList<SolderedPin> column;
		for(int c = 0; c < columns; c++) {
			column = new CustomLinkedList<>();
			for(int r = 0; r < rows; r++)
				column.offerLast(mkIdent());
			elements.offerLast(column);
		}
    }
    
    @Override
	public boolean isPreset() {
		return false;
	}
    
    
    public void changeAllOccurrences(String oldGateName, String newGateName) {
    	String[] parts = oldGateName.split("\\.");
    	
    	
    	if(parts.length == 2 && newGateName.endsWith("." + parts[1])) {
        	notifier.sendChange(this, "changeAllOccurrences", oldGateName, newGateName);
    		if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
    			circuitBoardsUsed.replace(oldGateName, newGateName);
    		} else if (parts[1].equals(BasicModel.GATE_MODEL_EXTENSION)) {
    			defaultGatesUsed.replace(oldGateName, newGateName);
    		} else if (parts[1].equals(OracleModel.ORACLE_MODEL_EXTENSION)) {
    			oraclesUsed.replace(oldGateName, newGateName);
    		}

        	renderNotifier.sendChange(this, "changeAllOccurrences", oldGateName, newGateName);
    	}
    }
    
    public int getOccurrences(String gateName) {
    	String[] parts = gateName.split("\\.");
    	
    	if(parts.length == 2) {
    		if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
    			return circuitBoardsUsed.getOccurrences(gateName);
    		} else if (parts[1].equals(BasicModel.GATE_MODEL_EXTENSION)) {
    			if(PresetGateType.containsPresetTypeByFormalName(gateName))
    				return presetGatesUsed.getOccurrences(gateName);
    			else	
    				return defaultGatesUsed.getOccurrences(gateName);
    		} else if (parts[1].equals(OracleModel.ORACLE_MODEL_EXTENSION)) {
    			return oraclesUsed.getOccurrences(gateName);
    		}
    	}
    	return 0;
    }
    
    
    
    
    public void addRows(int index, int amt) {
    	if(amt < 0) throw new IllegalArgumentException("Amount must be positive");
    	if(index < 0 || index > getRows())
			throw new IllegalArgumentException("Index must be a postive and less than or equal the size");
    	if(amt == 0)
    		return;
    	
    	notifier.sendChange(this, "addRows", index, amt);
    	
    	
		if(index  == 0 || index == getRows()) {
			ListIterator<SolderedPin> iterator;
			for(CustomLinkedList<SolderedPin> column : elements) {
				iterator = column.listIterator(index);
				for(int i = 0; i < amt; i++)
					iterator.add(mkIdent());
			}
		} else {
			ListIterator<SolderedPin> iterator;
			
			SolderedPin spp, spc;
			SolderedGate sg;
			for(CustomLinkedList<SolderedPin> column : elements) {
				iterator = column.listIterator(index-1);
				spp = iterator.next();
				spc = iterator.next();
				iterator.previous();
				
				if(spp.getSolderedGate() == spc.getSolderedGate()) {
					sg = spp.getSolderedGate();
					
					for(int i = 0; i< amt; i++)
						iterator.add(new SpacerPin(sg, spp.isWithinBody() && spc.isWithinBody()));
					
				} else {
					for(int i = 0; i< amt; i++)
						iterator.add(mkIdent());
				}
			}
		}

    	renderNotifier.sendChange(this, "addRows", index, amt);
	}
	
    
    
    
    
    
    
    
    
    
    public void removeRows(int firstIndex, int lastIndex) {
    	if(firstIndex < 0 || firstIndex > getRows())
			throw new IllegalArgumentException("First arg must be a postive and less than or equal the size");
		if(lastIndex < firstIndex || lastIndex > getRows())
			throw new IllegalArgumentException("Last arg must be a postive and less than or equal the size");
		if(lastIndex - firstIndex == getRows())
			throw new IllegalArgumentException("Rows cannot be less than 1");
		if(firstIndex == lastIndex)
			return;
		
		notifier.sendChange(this, "removeRows", firstIndex, lastIndex);
		
		for(CustomLinkedList<SolderedPin> column : elements) {
			ListIterator<SolderedPin> iterator = column.listIterator(firstIndex);
			SolderedPin firstP = iterator.next();
			SolderedGate firstG = firstP.getSolderedGate();
			
			boolean hitFirstReg = firstP instanceof SolderedRegister;
			boolean hitLastReg = hitFirstReg;
			
			iterator.previous();
			
			
			SolderedGate lastG = firstG;
			
			int i = 0;
			while (i++ < lastIndex - firstIndex) {
				SolderedPin current = iterator.next();
				
				if(current.getSolderedGate() != lastG) {
					if(lastG != firstG)
						removeFromManifest(lastG.getGateModelFormalName());
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
			ListIterator<SolderedPin> copy = column.listIterator(iterator.nextIndex() - 1);
			removeBoundaryGates(firstG, lastG, hitFirstReg, hitLastReg, iterator, copy);
		}
		

		renderNotifier.sendChange(this, "removeRows", firstIndex, lastIndex);
	}
    
    
    
    
    
    
    
    
    
	public void addColumns(int index, int amt) {
		if(amt < 0) throw new IllegalArgumentException("Amount must be positive");
		if(index < 0 || index > getColumns())
			throw new IllegalArgumentException("Index must be a postive and less than or equal the size");
    	if(amt == 0)
    		return;
    	
    	notifier.sendChange(this, "addColumns", index, amt);
		
		
		ListIterator<CustomLinkedList<SolderedPin>> iterator = elements.listIterator(index);
		CustomLinkedList<SolderedPin> column;
		for(int i = 0; i < amt; i++) {
			column = new CustomLinkedList<SolderedPin>();
			for(int r = 0; r < getRows(); r++)
				column.add(mkIdent());
			iterator.add(column);
		}

    	renderNotifier.sendChange(this, "addColumns", index, amt);
	}
	
	
	
	
	
	
	
	
	public void removeColumns(int firstIndex, int lastIndex) {
		if(firstIndex < 0 || firstIndex > getColumns())
			throw new IllegalArgumentException("First arg must be a postive and less than or equal the size");
		if(lastIndex < firstIndex || lastIndex > getColumns())
			throw new IllegalArgumentException("Last arg must be a postive and less than or equal the size");
		if(lastIndex - firstIndex == getColumns())
			throw new IllegalArgumentException("Columns cannot be less than 1");
		if(firstIndex == lastIndex)
			return;
		
		notifier.sendChange(this, "removeColumns", firstIndex, lastIndex);
		

		ListIterator<CustomLinkedList<SolderedPin>> iterator = elements.listIterator(firstIndex);
		
		for(int i = 0; i < lastIndex - firstIndex; i++) {
			Iterator<SolderedPin> rowIterator = iterator.next().iterator();
			
			SolderedGate currentG = rowIterator.next().getSolderedGate();
			while(rowIterator.hasNext()) {
				SolderedPin currentP = rowIterator.next();
				if(currentP.getSolderedGate() != currentG) {
					removeFromManifest(currentG.getGateModelFormalName());
					currentG = currentP.getSolderedGate();
				}
			}
			removeFromManifest(currentG.getGateModelFormalName());
			
			iterator.remove();
		}
		
    	renderNotifier.sendChange(this, "removeColumns", firstIndex, lastIndex);
	}
	
	
	
	
	
	
	
	
	public int getRows() {
		return elements.getFirst().size();
	}
	
	
	
	
	
	
	
	
	public int getColumns() {
		return elements.size();
	}

	
	
	
	
    
    
	
	
	
	
	
	public void placeGate(String gateModelFormalName, int column, Integer[] registers, String ... parameters) throws DefinitionEvaluatorException {
		if(column < 0 || column > getColumns())
			throw new IllegalArgumentException("Column should be greater than 0 and less than circuitboard column size");
		for(int reg : registers)
			if(reg < 0 || reg > getRows())
				throw new IllegalArgumentException("Register should be greater than 0 and less than circuitboard row size");
		
		
		notifier.sendChange(this, "placeGate", gateModelFormalName, column, registers, parameters);
		
		
		
		
		SolderedGate toPlace = new SolderedGate(addToManifest(gateModelFormalName), parameters);
		
		
		ArrayList<Integer> localRegs = CollectionUtils.sortedListIndexes(registers);
		int firstGlobalReg = registers[(localRegs.get(0))];
		
		ListIterator<SolderedPin> iterator = elements.get(column).listIterator(firstGlobalReg);
		ListIterator<SolderedPin> copy = elements.get(column).listIterator(iterator);
		
		SolderedPin firstP = iterator.next();
		SolderedGate firstG = firstP.getSolderedGate();
		SolderedGate lastG = firstG;
		
		boolean hitFirstReg = firstP instanceof SolderedRegister;
		boolean hitLastReg = hitFirstReg;
		boolean isWithinGate = firstP.isWithinBody();
		
		iterator.previous();
		
		int i = 0;
		
		while (iterator.hasNext()) {
			SolderedPin current = iterator.next();
			
			if(current.getSolderedGate() != lastG) {
				if(lastG != firstG)
					removeFromManifest(lastG.getGateModelFormalName());
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
				iterator.set(new SpacerPin(toPlace, true));
			}
		}
		
		if(isWithinGate && !hitFirstReg && !hitLastReg) {
			while(iterator.hasNext()) {
				SolderedPin temp = iterator.next();
				if(temp.getSolderedGate() == firstG)
					iterator.set(mkIdent());
				else break;
			}
			ListIterator<SolderedPin> topIterator = copy;
			while(topIterator.hasPrevious()) {
				SolderedPin temp = topIterator.previous();
				if(temp.getSolderedGate() == firstG)
					topIterator.set(mkIdent());
				else break;
			}
			removeFromManifest(firstG.getGateModelFormalName());
		} else {
			removeBoundaryGates(firstG, lastG, hitFirstReg, hitLastReg, copy, iterator);
		}

		renderNotifier.sendChange(this, "placeGate", gateModelFormalName, column, registers, parameters);
	}
	
	
	
	
	
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
		removeFromManifest(sg.getGateModelFormalName());
		
		renderNotifier.sendChange(this, "removeGate", row, column);
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
		
		if(sg.isIdentity())
			return;
		
		if(sgc == sg) {
			if(spc instanceof SolderedRegister)
				throw new IllegalArgumentException("Gate cannot add control where a local register is currently present");
			
			notifier.sendChange(this, "placeControl", rowControl, rowGate, column, controlStatus);
			
			iterator.set(new SolderedControl(sg, spc.isWithinBody(), controlStatus));
		} else {
			
			notifier.sendChange(this, "placeControl", rowControl, rowGate, column, controlStatus);
			
			SolderedGate currentGate = sgc;
			boolean remove = spc instanceof SolderedRegister;
			
			if(rowControl - rowGate > 0) {
				iterator.set(new SolderedControl(sg, false, controlStatus));
				
				while (iterator.hasPrevious()) {
					SolderedPin current = iterator.previous();
					
					if(current.getSolderedGate() != currentGate) {
						if(currentGate != sgc)
							removeFromManifest(currentGate.getGateModelFormalName());
						currentGate = current.getSolderedGate();
					}
					
					if(currentGate == sg)
						break;
					if(currentGate == sgc && current instanceof SolderedRegister)
						remove = true;
					iterator.set(new SpacerPin(sg, false));
				}
				
				removeBoundaryGates(currentGate, currentGate, false, remove, null, 
						elements.get(column).listIterator(rowControl));
			} else {
				iterator.next();
				iterator.set(new SolderedControl(sg, false, controlStatus));
				
				while (iterator.hasNext()) {
					SolderedPin current = iterator.next();
					
					if(current.getSolderedGate() != currentGate) {
						if(currentGate != sgc)
							removeFromManifest(currentGate.getGateModelFormalName());
						currentGate = current.getSolderedGate();
					}
					
					if(currentGate == sg)
						break;
					if(currentGate == sgc && current instanceof SolderedRegister)
						remove = true;
					iterator.set(new SpacerPin(sg, false));
				}
				
				removeBoundaryGates(currentGate, currentGate, remove, false, 
						elements.get(column).listIterator(rowControl), null);
			}
		}
		
		renderNotifier.sendChange(this, "placeControl", rowControl, rowGate, column, controlStatus);
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
		
		
		notifier.sendChange(this, "removeControl", row, column);
		
		
		boolean checkAbove, checkBelow;
		
		SolderedPin prev = null;
		
		if(iterator.hasPrevious()) {
			prev = iterator.previous();
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
			iterator.set(new SpacerPin(sg, prev.isWithinBody() && next.isWithinBody() ));
		} else if (checkAbove) {
			iterator.set(mkIdent());
			while (iterator.hasPrevious()) {
				prev = iterator.previous();
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
		

		renderNotifier.sendChange(this, "removeControl", row, column);
		
	}
	
	
	
	
	
	public SolderedGate getGateAt(int row, int column) {
		return elements.get(column).get(row).getSolderedGate();
	}
	
	
	
	public SolderedPin getSolderPinAt(int row, int column) {
		return elements.get(column).get(row);
	}
	
	
	@Override
	public String getExtString() {
		return CIRCUIT_BOARD_EXTENSION;
	}
	
	
	@Override
	public CircuitBoardIterator iterator() {
		return new CircuitBoardIterator(0);
	}
	
	
	public CircuitBoardIterator iterator(int index) {
		return new CircuitBoardIterator(index);
	}
	
	
	
	
	@Override
	public int getNumberOfRegisters() {
		return getRows();
	}
	
	
	
	public void setReciever(Notifier reciever) {
		this.notifier.setReceiver(reciever);
	}
	
	
	public Notifier getNotifier() {
		return notifier;
	}
	
	public void setRenderEventHandler(ReceivedEvent re) {
		this.renderNotifier.setReceivedEvent(re);
	}
	
	
	public class CircuitBoardIterator implements Iterator<RawExportableGateData> {
		private Iterator<CustomLinkedList<SolderedPin>> columns;
		private CustomLinkedList<SolderedPin> rowList;
		private ListIterator<SolderedPin> rows;
		private ListIterator<SolderedPin> lookAhead;
		private int r, c;
		private int gateRowBodyStart, gateRowBodyEnd;
		
		public CircuitBoardIterator(int index) {
			columns = elements.listIterator(index);
			c = index - 1;
			nextColumn();
		}
		
		@Override
		public boolean hasNext() {
			return lookAhead.hasNext() || columns.hasNext();
		}
		
		@Override
		public RawExportableGateData next() {
			if(!lookAhead.hasNext())
				nextColumn();
			rows = rowList.listIterator(lookAhead);
			
			
			SolderedPin sp = lookAhead.next();
			
			SolderedGate sg = sp.getSolderedGate();
			SolderedGate current = sg;
			
			LinkedList<Control> controls = new LinkedList<>();
			LinkedList<Integer> underneathIdentityGates = new LinkedList<>();
			Hashtable<Integer, Integer> registers = new Hashtable<>();
			
			gateRowBodyStart = -1;
			gateRowBodyEnd = -1;
			
			incrRowAndAddData(sp, controls, underneathIdentityGates, registers);
			
			int gateRowSpaceStart = r;
			
			boolean hitNoRowEnd;
			while (hitNoRowEnd = lookAhead.hasNext()) {
				sp = lookAhead.next();
				if(sp.getSolderedGate() != current)
					break;
				incrRowAndAddData(sp, controls, underneathIdentityGates, registers);
			}
			
			if(hitNoRowEnd)
				lookAhead.previous();
			
			return new RawExportableGateData(current, registers, controls, underneathIdentityGates,
					gateRowSpaceStart, r, gateRowBodyStart, gateRowBodyEnd, c);
		}
		
		
		
		private void incrRowAndAddData(SolderedPin sp, LinkedList<Control> controls, 
				LinkedList<Integer> underneathIdentityGates, Hashtable<Integer, Integer> registers) {
			
			r++;
			if(sp instanceof SolderedControl) {
				controls.offer(new Control(r, ((SolderedControl) sp).getControlStatus()));
			} else if (sp instanceof SolderedRegister) {
				int pinNumber = ((SolderedRegister) sp).getSolderedGatePinNumber();
				registers.put(pinNumber, r);
				if(gateRowBodyStart == -1)
					gateRowBodyStart = r;
				gateRowBodyEnd = r;
			} else {
				if(gateRowBodyStart != -1 && gateRowBodyEnd == -1)
					underneathIdentityGates.offer(r);
			}
		}
		
		private void nextColumn() {
			rowList = columns.next();
			lookAhead = rowList.listIterator();
			c++;
			r = -1;
		}
		
		public ListIterator<SolderedPin> iteratorAtStart() {
			return rowList.listIterator(rows);
		}
		
		public ListIterator<SolderedPin> iteratorAtEnd() {
			return rowList.listIterator(rows);
		}
		
		@Override
		public void remove() {
			
			notifier.sendChange(this, "remove");
			
			SolderedGate sg = null;
			ListIterator<SolderedPin> iterator = iteratorAtStart();
			

			while (iterator.nextIndex() != r) {
				sg = iterator.next().getSolderedGate();
				iterator.set(mkIdent());
			}
			removeFromManifest(sg.getGateModelFormalName());
			
			renderNotifier.sendChange(this, "remove");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	private SolderedRegister mkIdent() {
		
		ManifestObject mo = presetGatesUsed.add(PresetGateType.IDENTITY.getModel().getFormalName());
		
		try {
			return new SolderedRegister(new SolderedGate(mo), 0);
		} catch (DefinitionEvaluatorException e) {
			AppAlerts.showJavaExceptionMessage(AppStatus.get().getPrimaryStage(), "Program Crashed", "Could not make Identity Gate", e);
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	private ManifestObject addToManifest(String gateModel) {
		Project p = AppStatus.get().getFocusedProject();
		GateModel gm = p.getGateModel(gateModel);
		
		if(gm instanceof CircuitBoardModel) {
			if(((CircuitBoardModel) gm).findRecursion(p, gateModel))
				throw new RuntimeException("The circuit board " + gateModel + " makes circuit board " + getName() + " recusively defined");
			return circuitBoardsUsed.add(gateModel); 
		} else if (gm instanceof BasicModel) {
			if(gm.isPreset())
				return presetGatesUsed.add(gm.getFormalName());
			else
				return defaultGatesUsed.add(gm.getFormalName());
		} else if (gm instanceof OracleModel) {
			return oraclesUsed.add(gm.getFormalName());
		}
		return null;
	}
	
	
	private boolean findRecursion(Project p, String circuitBoardName) {
		if(getFormalName().equals(circuitBoardName))
			return true;
		
		if(circuitBoardsUsed.contains(circuitBoardName)) {
			return true;
		} else {
			for(String usedB : circuitBoardsUsed.getElements()) {
				CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(usedB);
				if(cb.findRecursion(p, circuitBoardName)) return true;
			}
		}
		return false;
	}
	
	
	private void removeFromManifest(String gateModel) {
		Project p = AppStatus.get().getFocusedProject();
		GateModel gm = p.getGateModel(gateModel);
		
		if(gm instanceof CircuitBoardModel) {
			circuitBoardsUsed.remove(gateModel); 
		} else if (gm instanceof BasicModel) {
			if(gm.isPreset())
				presetGatesUsed.remove(gm.getFormalName());
			else
				defaultGatesUsed.remove(gm.getFormalName());
		} else if (gm instanceof OracleModel) {
			oraclesUsed.remove(gm.getFormalName());
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
			removeFromManifest(firstG.getGateModelFormalName());
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
			while(lastIt.nextIndex() < getRows()) {
				SolderedPin sp = lastIt.next();
				if(sp.getSolderedGate() == lastG)					
					lastIt.set(mkIdent());
				else
					break;
			}
			if(diffGates)
				removeFromManifest(lastG.getGateModelFormalName());
		} else if (diffGates) {
			while(lastIt.nextIndex() < getRows() && lastIt != null) {
				SolderedPin sp = lastIt.next();
				if(sp instanceof SpacerPin)					
					lastIt.set(mkIdent());
				else
					break;
			}
		}
	}

	@Override
	public CircuitBoardModel shallowCopyToNewName(String name, String symbol, String description, String ... parameters) {
		
		return new CircuitBoardModel(name, symbol, description, parameters, this);
	}
	
	public CircuitBoardModel createDeepCopyToNewName(String name, String symbol, String description, String ... parameters) {
		CustomLinkedList<CustomLinkedList<SolderedPin>> temp = new CustomLinkedList<>();
		
		for(CustomLinkedList<SolderedPin> columns : elements) {
			CustomLinkedList<SolderedPin> tempColumn = new CustomLinkedList<>();
			
			for(SolderedPin sp : columns)
				tempColumn.addLast(sp);
			temp.addLast(tempColumn);
		}
		
		return new CircuitBoardModel(name, symbol, description, parameters, temp, circuitBoardsUsed.deepCopy(), 
				defaultGatesUsed.deepCopy(), presetGatesUsed.deepCopy(), oraclesUsed.deepCopy());
	}

}