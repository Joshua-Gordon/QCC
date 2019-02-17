package appFX.framework.gateModels;

import appFX.framework.InputDefinitions;
import appFX.framework.InputDefinitions.ArgDefinition;
import appFX.framework.InputDefinitions.CheckDefinitionRunnable;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.InputDefinitions.GroupDefinition;
import appFX.framework.InputDefinitions.MathObject;
import appFX.framework.InputDefinitions.MatrixDefinition;
import appFX.framework.InputDefinitions.ScalarDefinition;
import mathLib.Complex;
import mathLib.Matrix;
import utils.customCollections.ImmutableArray;

public class BasicModel extends GateModel {
	private static final long serialVersionUID = -3974442774420594973L;
	
	public static final String GATE_MODEL_EXTENSION =  "gm";
	
	public static enum BasicModelType {
		UNIVERSAL("Universal"), POVM("POVM"), HAMILTONIAN("Hamiltonian");
		
		private String name;
		
		private BasicModelType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private final int numberOfRegisters;
	private final ImmutableArray<String> latex, userInput;
	private final ImmutableArray<MathObject> definitions;
    private final BasicModelType gateType;
    
    
    
    
    
    public BasicModel(String name, String symbol, String description, String[] parameters, BasicModelType gateType, String ... userDefinitions) 
    		throws DefinitionEvaluatorException {
		super(name, symbol, description, getParameters(parameters, userDefinitions, gateType));
		
		if(!isPreset())
			PresetGateType.checkName(name);
		
		
		
		this.gateType = gateType;
		
		
		RegularGateChecker rgc = new RegularGateChecker();
		GroupDefinition definitions = InputDefinitions.evaluateInput(rgc, parameters, userDefinitions);
		
		
		for(String var : definitions.getArguments()) {
			boolean foundVar = false;
			
			for(String arg : getArguments()) {
				if(arg.equals(var)) {
					foundVar = true;
					break;
				}
			}
			if(!foundVar)
				throw new IllegalArgumentException("Variable \"" + var + "\" is undefined");
		}
		
		this.latex = definitions.getLatexRepresentations();
		this.userInput = definitions.getRawUserInput();
		this.definitions = definitions.getMathDefinitions();
		
		this.numberOfRegisters = rgc.getNumberRegisters();
	}
    
    
    
    private static String[] getParameters(String[] parameters, String[] definitions, BasicModelType gateType) {
    	switch(gateType) {
    	case UNIVERSAL:
    		if(definitions.length != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
    		for(String arg : parameters)
				if(arg.equals("U"))
					throw new RuntimeException("Variable \"U\" cannot bed used as a parameter");
    		return parameters;
    	case HAMILTONIAN:
    		if(definitions.length != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
    		String[] tempParams = new String[parameters.length + 1];
    		tempParams[0] = "t";
    		int i = 1;
    		for(String param : parameters) {
    			if(param.equals("H") || param.equals("t"))
					throw new RuntimeException("Variable \"" + param + "\" cannot be used as a parameter");
    			tempParams[i++] = param;
    		}
    		return tempParams;
    	case POVM:
    		if(definitions.length < 1)
				throw new RuntimeException("There should be at least one matrix to define this gate model");
    		for(String arg : parameters)
				if(arg.matches("k_\\d")) 
					throw new RuntimeException("Variable " + arg + " cannot bed used as a parameter");
    		return parameters;
    	}
    	
    	return null;
    }
    
    
    private BasicModel(String name, String symbol, String description, String[] arguments, BasicModel old) {
    	super(name, symbol, description, arguments);
    	this.numberOfRegisters = old.numberOfRegisters;
    	this.latex = old.getLatex();
		this.userInput = old.getUserInput();
		this.definitions = old.getDefinitions();
    	this.gateType = old.gateType;
    }
    
	
	public BasicModelType getGateModelType() {
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
    	return latex;
    }
    
    public ImmutableArray<String> getUserInput() {
    	return userInput;
    }
    
    public ImmutableArray<MathObject> getDefinitions() {
    	return definitions;
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
	public GateModel shallowCopyToNewName(String name, String symbol, String description, String ... arguments) {
		return new BasicModel(name, symbol, description, arguments, this);
	}
	
	
	
	private static int getNumberOfRegisters (int matrixSize) {
		int i = 0;
		while(((matrixSize >> (++i)) | 1) != 1);
		return i;
	}
	
	
	
	private static int getNumberOfRegisters (Matrix<Complex> matrix) {
		return getNumberOfRegisters(matrix.getRows());
	}
	
	
	private static void checkSize(int rows, int columns, int definitionIndex) throws DefinitionEvaluatorException {
		if(rows != columns) {
			throw new DefinitionEvaluatorException("Number of rows must be equal to number of Columns", definitionIndex);
		} else {
			int size = rows;
			if((size & (size - 1)) != 0 || size < 2)
				throw new DefinitionEvaluatorException("The matrix size must greater than 1 and be some power of 2", definitionIndex);
		}
	}
	
	
	private static void checkSize(Matrix<Complex> mat , int definitionIndex) throws DefinitionEvaluatorException{
		checkSize(mat.getRows(), mat.getColumns(), definitionIndex);
	}
	
	
	public static class RegularGateChecker implements CheckDefinitionRunnable {
		
		private Integer numRegAll = null;
		private Integer numReg = null;
		
		@Override
		public void checkScalarDefinition(ScalarDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
		}

		@Override
		public void checkMatrixDefinition(MatrixDefinition definition, int definitionIndex)   throws DefinitionEvaluatorException{
			checkSize(definition.getMatrix(), definitionIndex);
			
			numReg = getNumberOfRegisters(definition.getMatrix());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}

		@Override
		public void checkArgDefinition(ArgDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			if(!definition.isMatrix())
				throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
			
			checkSize(definition.getRows(), definition.getColumns(), definitionIndex);
			
			
			numReg = getNumberOfRegisters(definition.getRows());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}
		
		public int getNumberRegisters() {
			return numRegAll;
		}
	}
	
	
}
