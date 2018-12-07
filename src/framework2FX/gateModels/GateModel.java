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
	
	private final int numberOfRegisters;
	private final GroupDefinition gateDefinition;
    private final GateModelType gateType;
    
	
    GateModel (String name, String symbol, String description, int numberOfRegisters, GroupDefinition gateDefinition, GateModelType gateType) {
		super(name, symbol, description);
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
		return gateDefinition.getArguments();
	}
    
    @SuppressWarnings("serial")
	public static class InvalidGateModelMatrixException extends RuntimeException {
		public InvalidGateModelMatrixException (String reason) {
			super (reason);
		}
	}
}
