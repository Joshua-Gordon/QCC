package framework2FX.gateModels;

import java.io.Serializable;

import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MathObject;
import framework2FX.solderedGates.Solderable;
import utils.customCollections.ImmutableArray;

public class GateModel extends Solderable implements Serializable{
	private static final long serialVersionUID = -3974442774420594973L;

	public static enum GateModelType {
		REGULAR_GATE, ORACLE, POVM, HAMILTONIAN;
	}
	
	private final String description;
    private final String name;
    private final String symbol;
	private final int numberOfRegisters;
	private final GroupDefinition gateDefinition;
    private final GateModelType gateType;
    
	
    GateModel (String name, String symbol, String description, int numberOfRegisters, GroupDefinition gateDefinition, GateModelType gateType) {
		this.description 				= description;
		this.name 						= name;
		this.symbol 					= symbol;
		this.numberOfRegisters			= numberOfRegisters;
		this.gateDefinition				= gateDefinition;
		this.gateType 					= gateType;
	}
	

	public GateModelType getGateModelType() {
		return gateType;
	}
	
	
	public boolean isPreset() {
		return false;
	}
	
	@Override
	public int getNumberOfRegisters() {
		return numberOfRegisters;
	}
	
	
	public boolean isMultiQubitGate () {
		return getNumberOfRegisters() > 1;
	}

	
	
	public String getName() {
		return name;
	}
	
    
	public String getSymbol() {
		return symbol;
	}
	
    public String getDescription() {
		return description;
    }
    
    public ImmutableArray<String> getLatex() {
    	return gateDefinition.getLatexRepresentations();
    }
    
    public ImmutableArray<String> getUserInput() {
    	return gateDefinition.getRawUserInput();
    }
    
    public ImmutableArray<MathObject> getDefinitions() {
    	return gateDefinition.getMathDefinitions();
    }
    
    public boolean hasArguments() {
		return gateDefinition.getArguments().size() != 0;
	}
	
	public ImmutableArray<String> getArguments() {
		if(hasArguments()) throw new RuntimeException("This model does not have arguments");
		return gateDefinition.getArguments();
	}
    
    @SuppressWarnings("serial")
	public static class InvalidGateModelMatrixException extends RuntimeException {
		public InvalidGateModelMatrixException (String reason) {
			super (reason);
		}
	}
}
