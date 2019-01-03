package framework2FX.gateModels;

import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MathObject;
import utils.customCollections.ImmutableArray;

public class BasicModel extends GateModel {
	private static final long serialVersionUID = -3974442774420594973L;
	
	public static final String GATE_MODEL_EXTENSION =  "gm";
	
	public static enum DefaultModelType {
		UNIVERSAL("Universal"), POVM("POVM"), HAMILTONIAN("Hamiltonian");
		
		private String name;
		
		private DefaultModelType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private final int numberOfRegisters;
	private final GroupDefinition gateDefinition;
    private final DefaultModelType gateType;
    
    private BasicModel(String name, String symbol, String description, BasicModel old) {
    	super(name, symbol, description);
    	this.numberOfRegisters = old.numberOfRegisters;
    	this.gateDefinition = old.gateDefinition;
    	this.gateType = old.gateType;
    }
	
    BasicModel (String name, String symbol, String description, int numberOfRegisters, GroupDefinition gateDefinition, DefaultModelType gateType) {
		super(name, symbol, description);
		this.numberOfRegisters			= numberOfRegisters;
		this.gateDefinition				= gateDefinition;
		this.gateType 					= gateType;
		if(!isPreset())
			PresetGateType.checkName(name);
	}
	

	public DefaultModelType getGateModelType() {
		return gateType;
	}
	
	@Override
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

	@Override
	public String getExtString() {
		return GATE_MODEL_EXTENSION;
	}

	@Override
	public GateModel getAsNewModel(String name, String symbol, String description) {
		return new BasicModel(name, symbol, description, this);
	}
}
